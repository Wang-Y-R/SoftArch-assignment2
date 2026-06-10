package com.hotelpricing.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Finalizer Agent — third agent in the multi-agent pipeline.
 * Synthesizes the Designer's output and Reviewer's critique into a final design.
 */
@Component
public class FinalizerAgent {

    private static final Logger log = LoggerFactory.getLogger(FinalizerAgent.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 10000;

    private final ChatClient chatClient;

    public FinalizerAgent(ChatClient finalizerChatClient) {
        this.chatClient = finalizerChatClient;
    }

    public String finalize(String finalizationPrompt) {
        log.info("[Finalizer] Producing final design...");
        return callWithRetry(finalizationPrompt, "finalize");
    }

    private String callWithRetry(String prompt, String operation) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                String output = chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content();
                log.info("[Finalizer] {} complete (attempt {}), {} chars", operation, attempt, output.length());
                return output;
            } catch (Exception e) {
                log.warn("[Finalizer] Attempt {}/{} failed: {}", attempt, MAX_RETRIES, e.getMessage());
                if (attempt == MAX_RETRIES) {
                    log.error("[Finalizer] All {} attempts failed", MAX_RETRIES);
                    return "ERROR: " + e.getMessage();
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return "ERROR: Interrupted";
                }
            }
        }
        return "ERROR: Unexpected";
    }
}
