package com.hotelpricing.config;

import com.hotelpricing.knowledge.KnowledgeBase;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Multi-agent configuration.
 * Creates three distinct ChatClients, each with its own system prompt
 * and role specialization per the Option 3 multi-agent paradigm.
 */
@Configuration
public class AgentConfig {

    private final KnowledgeBase knowledgeBase;

    public AgentConfig(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    @Bean
    public ChatClient designerChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem(knowledgeBase.getDesignerSystemInstructions())
                .build();
    }

    @Bean
    public ChatClient reviewerChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem(knowledgeBase.getReviewerSystemInstructions())
                .build();
    }

    @Bean
    public ChatClient finalizerChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem(knowledgeBase.getFinalizerSystemInstructions())
                .build();
    }
}
