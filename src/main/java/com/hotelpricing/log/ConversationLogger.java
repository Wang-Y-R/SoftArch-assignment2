package com.hotelpricing.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Records complete multi-agent conversation logs with timestamps for assignment submission.
 * Each iteration produces a separate log file capturing all three agents' exchanges.
 */
@Component
public class ConversationLogger {

    private static final Logger log = LoggerFactory.getLogger(ConversationLogger.class);
    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter FILE_TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss.SSS").withZone(ZoneId.systemDefault());

    private final Path logDir;

    public ConversationLogger() {
        this.logDir = Paths.get("src/main/resources/logs");
        try {
            Files.createDirectories(logDir);
        } catch (IOException e) {
            log.error("Failed to create log directory: {}", e.getMessage());
        }
    }

    /**
     * Logs ADD Step 1 (Review Inputs) executed by the Designer Agent.
     */
    public Path logStep1(String step1Prompt, String step1Output) {
        StringBuilder sb = new StringBuilder();

        sb.append("=".repeat(80)).append("\n");
        sb.append("  HOTEL PRICING SYSTEM — ADD STEP 1: REVIEW INPUTS\n");
        sb.append("  AI Paradigm: Multi-Agent (Designer → Reviewer → Finalizer)\n");
        sb.append("  Model: DeepSeek-V4-Pro\n");
        sb.append("=".repeat(80)).append("\n\n");

        appendSection(sb, "ADD STEP 1: REVIEW INPUTS",
                "Agent", "Designer Agent",
                "User Prompt", step1Prompt,
                "Agent Response", step1Output);

        sb.append("=".repeat(80)).append("\n");
        sb.append("  END OF STEP 1 LOG\n");
        sb.append("=".repeat(80)).append("\n");

        return writeLogFile("step1-review", sb.toString());
    }

    /**
     * Logs a complete multi-agent iteration with all three phases.
     */
    public Path logIterationMultiAgent(int iteration,
                                        String iterationPrompt,
                                        String designerOutput,
                                        String reviewPrompt,
                                        String reviewerOutput,
                                        String finalizationPrompt,
                                        String finalOutput) {
        StringBuilder sb = new StringBuilder();

        sb.append("=".repeat(80)).append("\n");
        sb.append("  HOTEL PRICING SYSTEM — ARCHITECTURE DESIGN LOG\n");
        sb.append("  AI Paradigm: Multi-Agent (Designer → Reviewer → Finalizer)\n");
        sb.append("  Model: DeepSeek-V4-Pro\n");
        sb.append("  Iteration: ").append(iteration).append(" of 4\n");
        sb.append("=".repeat(80)).append("\n\n");

        // Phase 1: Designer Agent
        appendSection(sb, "PHASE 1: DESIGNER AGENT",
                "Agent Role", "Designer — generates initial ADD 3.0 architecture design",
                "User Prompt (Iteration Goal)", iterationPrompt,
                "Designer Agent Response", designerOutput);

        // Phase 2: Reviewer Agent
        appendSection(sb, "PHASE 2: REVIEWER AGENT",
                "Agent Role", "Reviewer — critically evaluates design across 6 dimensions",
                "User Prompt (Review Request)", reviewPrompt,
                "Reviewer Agent Response", reviewerOutput);

        // Phase 3: Finalizer Agent
        appendSection(sb, "PHASE 3: FINALIZER AGENT",
                "Agent Role", "Finalizer — synthesizes design + review into final output",
                "User Prompt (Finalization Request)", finalizationPrompt,
                "Finalizer Agent Response (Final Output)", finalOutput);

        sb.append("=".repeat(80)).append("\n");
        sb.append("  END OF ITERATION ").append(iteration).append(" LOG\n");
        sb.append("=".repeat(80)).append("\n");

        return writeLogFile("iteration-" + iteration, sb.toString());
    }

    /**
     * Appends a timestamped section with alternating label/content pairs.
     */
    private void appendSection(StringBuilder sb, String sectionTitle, String... labelContentPairs) {
        String timestamp = TIMESTAMP_FMT.format(Instant.now());
        sb.append("-".repeat(60)).append("\n");
        sb.append("  ").append(sectionTitle).append("\n");
        sb.append("  Timestamp: ").append(timestamp).append("\n");
        sb.append("-".repeat(60)).append("\n\n");

        for (int i = 0; i < labelContentPairs.length; i += 2) {
            sb.append(">>> ").append(labelContentPairs[i]).append(" <<<\n");
            sb.append(labelContentPairs[i + 1]).append("\n\n");
        }
    }

    private Path writeLogFile(String label, String content) {
        try {
            String filename = String.format("%s-log-%s.txt",
                    label,
                    FILE_TIMESTAMP_FMT.format(Instant.now()));
            Path filePath = logDir.resolve(filename);
            Files.writeString(filePath, content);
            log.info("Log written to: {}", filePath.toAbsolutePath());
            return filePath;
        } catch (IOException e) {
            log.error("Failed to write log file: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Writes a summary report aggregating all iterations.
     */
    public Path writeSummaryReport(String summary, int totalTurns,
                                    long totalTokens, long timeCostMinutes) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(80)).append("\n");
        sb.append("  INTERACTION COST ANALYSIS SUMMARY\n");
        sb.append("=".repeat(80)).append("\n\n");
        sb.append("Completion Method: Multi-Agent\n");
        sb.append("AI Paradigm: Distributed reasoning + collaborative verification\n");
        sb.append("Agent Pipeline: Designer → Reviewer → Finalizer\n");
        sb.append("LLM Used: DeepSeek-V4-Pro\n");
        sb.append("Number of Human Interactions (turns): ").append(totalTurns).append("\n");
        sb.append("Token Consumption (K tokens): ").append(String.format("%.1f", totalTokens / 1000.0)).append("\n");
        sb.append("Time Cost (min): ").append(timeCostMinutes).append("\n\n");
        sb.append(summary);

        try {
            Path filePath = logDir.resolve("summary-report.txt");
            Files.writeString(filePath, sb.toString());
            log.info("Summary report written to: {}", filePath.toAbsolutePath());
            return filePath;
        } catch (IOException e) {
            log.error("Failed to write summary: {}", e.getMessage());
            return null;
        }
    }
}
