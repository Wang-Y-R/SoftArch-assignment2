package com.hotelpricing.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Reviewer Agent — second agent in the multi-agent pipeline.
 * Critically reviews the Designer's output across six dimensions.
 */
@Component
public class ReviewerAgent {

    private static final Logger log = LoggerFactory.getLogger(ReviewerAgent.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 5000;

    private final ChatClient chatClient;

    public ReviewerAgent(ChatClient reviewerChatClient) {
        this.chatClient = reviewerChatClient;
    }

    public String review(String reviewPrompt) {
        log.info("[Reviewer] Reviewing design...");
        return callWithRetry(reviewPrompt, "review");
    }

    private String callWithRetry(String prompt, String operation) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                String output = chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content();
                log.info("[Reviewer] {} complete (attempt {}), {} chars", operation, attempt, output.length());
                return output;
            } catch (Exception e) {
                log.warn("[Reviewer] Attempt {}/{} failed: {}", attempt, MAX_RETRIES, e.getMessage());
                if (attempt == MAX_RETRIES) {
                    log.error("[Reviewer] All {} attempts failed", MAX_RETRIES);
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
