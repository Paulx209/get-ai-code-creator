package com.getian.getaicodemother.ai;

import com.getian.getaicodemother.ai.tools.FileWriteTool;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;
import com.getian.getaicodemother.service.ChatHistoryService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class AiCodeGeneratorServiceFactory {
    @Resource
    private ChatModel chatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    private StreamingChatModel openAiStreamingChatModel ;

    @Resource
    private StreamingChatModel reasoningStreamingChatModel;

    @Resource
    private ChatHistoryService chatHistoryService;

    /**
     * AI 服务实例缓存
     * 缓存策略：
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<String, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，appId: {}, 原因: {}", key, cause);
            })
            .build();

    public AiCodeGeneratorService createAiCodeGeneratorService(Long appId, CodeGenTypeEnum codeGenType){
        log.info("创建AI服务实例，appId: {}", appId);
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        //从数据库加载对话记录到redis缓存
        chatHistoryService.loadChatHistoryToMemory(appId,chatMemory,20);
        return switch (codeGenType){
            case VUE_PROJECT -> AiServices.builder(AiCodeGeneratorService.class)
                    .streamingChatModel(reasoningStreamingChatModel)
                    .chatMemoryProvider(memoryId -> chatMemory)
                    .tools(new FileWriteTool())
                    .hallucinatedToolNameStrategy(toolExecutionRequest ->
                        ToolExecutionResultMessage.from(toolExecutionRequest,
                                "Error: no suitable tool called"+toolExecutionRequest.name()
                        ))
                    .build();
            case HTML , MULTI_FILE -> AiServices.builder(AiCodeGeneratorService.class)
                    .streamingChatModel(openAiStreamingChatModel)
                    .chatModel(chatModel)
                    .chatMemory(chatMemory)
                    .build();
            default ->  throw new IllegalArgumentException("不支持的代码生成类型: " + codeGenType.getValue());
        };
    }

    public AiCodeGeneratorService getAiCodeGeneratorService(Long appId,CodeGenTypeEnum codeGenType) {
        String cacheKey = appId + "_" +codeGenType.getValue();
        //因为这里调用的方法，需要传递两个参数，就没办法使用 this::createAiCodeGeneratorService 的方式了
        return serviceCache.get(cacheKey,key -> createAiCodeGeneratorService(appId,codeGenType));
    }

    //兼容老的接口，使用HTML模式
    public AiCodeGeneratorService createAiCodeGeneratorService(Long appId) {
        return createAiCodeGeneratorService(appId,CodeGenTypeEnum.HTML);
    }

    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return getAiCodeGeneratorService(0L,CodeGenTypeEnum.HTML);
    }
}
