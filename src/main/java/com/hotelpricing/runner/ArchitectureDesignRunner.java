package com.hotelpricing.runner;

import com.hotelpricing.agent.DesignerAgent;
import com.hotelpricing.agent.FinalizerAgent;
import com.hotelpricing.agent.ReviewerAgent;
import com.hotelpricing.knowledge.KnowledgeBase;
import com.hotelpricing.log.ConversationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates the multi-agent ADD 3.0 design process across 4 iterations.
 *
 * Multi-agent pipeline per iteration:
 *   Designer → Reviewer → Finalizer
 *
 * Runs automatically on application startup.
 */
@Component
public class ArchitectureDesignRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ArchitectureDesignRunner.class);

    private final DesignerAgent designer;
    private final ReviewerAgent reviewer;
    private final FinalizerAgent finalizer;
    private final KnowledgeBase knowledgeBase;
    private final ConversationLogger conversationLogger;

    public ArchitectureDesignRunner(DesignerAgent designer,
                                     ReviewerAgent reviewer,
                                     FinalizerAgent finalizer,
                                     KnowledgeBase knowledgeBase,
                                     ConversationLogger conversationLogger) {
        this.designer = designer;
        this.reviewer = reviewer;
        this.finalizer = finalizer;
        this.knowledgeBase = knowledgeBase;
        this.conversationLogger = conversationLogger;
    }

    @Override
    public void run(String... args) {
        log.info("=".repeat(60));
        log.info("  Starting Hotel Pricing System Architecture Design");
        log.info("  Method: ADD 3.0 via Multi-Agent (DeepSeek-V4-Pro)");
        log.info("  Pipeline: Designer → Reviewer → Finalizer");
        log.info("=".repeat(60));

        Instant totalStart = Instant.now();
        List<IterationRecord> records = new ArrayList<>();
        int totalTurns = 0;
        long estimatedTokens = 0;

        // --- ADD Step 1: Review Inputs (executed once by Designer) ---
        log.info("\n>>> ADD Step 1: Reviewing Inputs (Designer Agent)...");
        Instant step1Start = Instant.now();
        String step1Prompt = knowledgeBase.getStep1ReviewPrompt();
        String step1Output = designer.generateDesign(step1Prompt);
        Duration step1Duration = Duration.between(step1Start, Instant.now());
        log.info("<<< ADD Step 1 complete ({}s)", step1Duration.toSeconds());

        // Log Step 1 as standalone
        conversationLogger.logStep1(
                step1Prompt,
                step1Output
        );
        totalTurns += 1;

        // --- Iterations 1-4 ---
        String previousFinalOutput = step1Output;

        for (int i = 1; i <= 4; i++) {
            log.info("\n{}", "=".repeat(60));
            log.info("  ITERATION {} OF 4 — Multi-Agent Pipeline", i);
            log.info("{}", "=".repeat(60));

            Instant iterStart = Instant.now();

            // Build iteration prompt with context from previous iteration
            String iterationPrompt = knowledgeBase.getIterationPrompt(i);
            if (i > 1) {
                iterationPrompt = buildContextualPrompt(i, iterationPrompt, previousFinalOutput);
            }

            // ── Phase 1: Designer ──
            log.info("  [Phase 1/3] Designer Agent generating design...");
            Instant dStart = Instant.now();
            String designerOutput = designer.generateDesign(iterationPrompt);
            Duration dDuration = Duration.between(dStart, Instant.now());
            log.info("  [Phase 1/3] Designer complete ({}s, {} chars)",
                    dDuration.toSeconds(), designerOutput.length());

            // ── Phase 2: Reviewer ──
            log.info("  [Phase 2/3] Reviewer Agent reviewing design...");
            Instant rStart = Instant.now();
            String reviewPrompt = knowledgeBase.getReviewerPrompt(designerOutput);
            String reviewerOutput = reviewer.review(reviewPrompt);
            Duration rDuration = Duration.between(rStart, Instant.now());
            log.info("  [Phase 2/3] Reviewer complete ({}s, {} chars)",
                    rDuration.toSeconds(), reviewerOutput.length());

            // ── Phase 3: Finalizer ──
            log.info("  [Phase 3/3] Finalizer Agent producing final design...");
            Instant fStart = Instant.now();
            String finalizationPrompt = knowledgeBase.getFinalizerPrompt(designerOutput, reviewerOutput);
            String finalOutput = finalizer.finalize(finalizationPrompt);
            Duration fDuration = Duration.between(fStart, Instant.now());
            log.info("  [Phase 3/3] Finalizer complete ({}s, {} chars)",
                    fDuration.toSeconds(), finalOutput.length());

            Duration iterDuration = Duration.between(iterStart, Instant.now());

            // ── Log the complete multi-agent conversation ──
            conversationLogger.logIterationMultiAgent(
                    i,
                    iterationPrompt,
                    designerOutput,
                    reviewPrompt,
                    reviewerOutput,
                    finalizationPrompt,
                    finalOutput
            );

            previousFinalOutput = finalOutput;

            // Track statistics: 3 turns per iteration (one per agent)
            totalTurns += 3;
            estimatedTokens += estimateTokens(
                    iterationPrompt, designerOutput,
                    reviewPrompt, reviewerOutput,
                    finalizationPrompt, finalOutput
            );

            records.add(new IterationRecord(i, iterDuration, finalOutput));
            log.info("  Iteration {} complete — total duration: {}s", i, iterDuration.toSeconds());
        }

        Duration totalDuration = Duration.between(totalStart, Instant.now());

        // Generate summary
        StringBuilder summaryBuilder = new StringBuilder();
        summaryBuilder.append("\n--- Per-Iteration Summary ---\n\n");
        for (IterationRecord r : records) {
            summaryBuilder.append(String.format(
                    "Iteration %d: %ds, output %d chars\n",
                    r.iteration(),
                    r.duration().toSeconds(),
                    r.finalOutput().length()
            ));
        }

        // Write summary report
        conversationLogger.writeSummaryReport(
                summaryBuilder.toString(),
                totalTurns,
                estimatedTokens,
                totalDuration.toMinutes()
        );

        log.info("\n{}", "=".repeat(60));
        log.info("  DESIGN COMPLETE");
        log.info("  Paradigm: Multi-Agent (Designer → Reviewer → Finalizer)");
        log.info("  Model: DeepSeek-V4-Pro");
        log.info("  Total iterations: 4");
        log.info("  Total turns: {}", totalTurns);
        log.info("  Estimated tokens: {}K", estimatedTokens / 1000);
        log.info("  Total time: {}min", totalDuration.toMinutes());
        log.info("  Logs saved to: src/main/resources/logs/");
        log.info("{}", "=".repeat(60));
    }

    private String buildContextualPrompt(int iteration, String basePrompt, String previousFinalOutput) {
        // Truncate previous output to avoid exceeding context window
        int maxPrevChars = 2000;
        String truncated = previousFinalOutput.length() > maxPrevChars
                ? previousFinalOutput.substring(0, maxPrevChars) + "\n... [truncated]"
                : previousFinalOutput;

        return basePrompt + "\n\n--- CONTEXT FROM PREVIOUS ITERATION ---\n\n" + truncated
                + "\n\n--- END OF PREVIOUS CONTEXT ---";
    }

    private long estimateTokens(String... texts) {
        long totalChars = 0;
        for (String t : texts) {
            if (t != null) {
                totalChars += t.length();
            }
        }
        return totalChars / 4;
    }

    private record IterationRecord(int iteration, Duration duration, String finalOutput) {
    }
}
