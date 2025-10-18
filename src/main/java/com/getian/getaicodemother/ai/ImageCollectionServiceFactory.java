package com.getian.getaicodemother.ai;

import com.getian.getaicodemother.langgraph4j.tools.ImageSearchTool;
import com.getian.getaicodemother.langgraph4j.tools.LogoGeneratorTool;
import com.getian.getaicodemother.langgraph4j.tools.MermaidDiagramTool;
import com.getian.getaicodemother.langgraph4j.tools.UndrawIllustrationTool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ImageCollectionServiceFactory {

    @Resource
    private ChatModel chatModel;
    @Resource
    private ImageSearchTool imageSearchTool;
    @Resource
    private LogoGeneratorTool logoGeneratorTool;
    @Resource
    private MermaidDiagramTool mermaidDiagramTool;
    @Resource
    private UndrawIllustrationTool undrawIllustrationTool;

    @Bean
    public ImageCollectionService imageCollectionService() {
        return AiServices.builder(ImageCollectionService.class)
                .chatModel(chatModel)
                .tools(imageSearchTool,logoGeneratorTool,mermaidDiagramTool,undrawIllustrationTool)
                .build();
    }
}
