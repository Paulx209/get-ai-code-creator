package com.getian.getaicodemother.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AiCodeGenTypeRoutingServiceFactory {
    @Resource
    private ChatModel chatModel;

    /**
     * 创建AI代码生成类型的路由服务实例
     * @return
     */
    @Bean
    AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService(){
        return AiServices.builder(AiCodeGenTypeRoutingService.class)
                .chatModel(chatModel)
                .build();
    }
}
