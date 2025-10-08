package com.getian.getaicodemother.configuration;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.chat-model")
@Data
public class ReasoningStreamingChatModelConfig {
    private String apiKey;
    private String baseUrl;

    @Bean
    public StreamingChatModel reasoningStreamingChatModel() {
        //测试环境
        final String modelName = "deepseek-chat";
        final int maxTokens = 8192;
        //开发环境
//        final String modelName = "deepseek-reasoner";
//        final int maxTokens = 32768;
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .maxTokens(maxTokens)
                .modelName(modelName)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
