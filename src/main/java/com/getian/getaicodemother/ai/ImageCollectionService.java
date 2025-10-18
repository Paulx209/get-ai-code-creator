package com.getian.getaicodemother.ai;

import com.getian.getaicodemother.langgraph4j.state.ImageResource;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.util.List;

/**
 * 收集图片服务 接口
 * 使用AI调用工具收集不同类型的图片资源
 */
public interface ImageCollectionService {

    /**
     * 根据用户提示词收集所需的图片资源
     * AI会根据需求自主选择相应的资源
     * @param userPrompt
     * @return
     */
    @SystemMessage(fromResource = "prompt/image-collection-system-prompt.txt")
    List<String> collectImages(@UserMessage String userPrompt);
}
