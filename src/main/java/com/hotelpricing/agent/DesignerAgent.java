package com.hotelpricing.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Designer Agent — first agent in the multi-agent pipeline.
 * Generates the initial architecture design following ADD 3.0 Steps 2-7.
 */
@Component
public class DesignerAgent {

    private static final Logger log = LoggerFactory.getLogger(DesignerAgent.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 5000;

    private final ChatClient chatClient;

    public DesignerAgent(ChatClient designerChatClient) {
        this.chatClient = designerChatClient;
    }

    public String generateDesign(String iterationPrompt) {
        log.info("[Designer] Generating architecture design...");
        return callWithRetry(iterationPrompt, "generateDesign");
    }

    private String callWithRetry(String prompt, String operation) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                String output = chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content();
                log.info("[Designer] {} complete (attempt {}), {} chars", operation, attempt, output.length());
                return output;
            } catch (Exception e) {
                log.warn("[Designer] Attempt {}/{} failed: {}", attempt, MAX_RETRIES, e.getMessage());
                if (attempt == MAX_RETRIES) {
                    log.error("[Designer] All {} attempts failed", MAX_RETRIES);
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
