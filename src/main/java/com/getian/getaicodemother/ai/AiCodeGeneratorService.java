package com.getian.getaicodemother.ai;

import com.getian.getaicodemother.ai.model.HtmlCodeResult;
import com.getian.getaicodemother.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface AiCodeGeneratorService {
    /**
     * 生成原生单页面文件
     *
     * @param userMessage 用户信息
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generatorHtmlCode(String userMessage);


    /**
     * 生成原生多页面文件
     *
     * @param userMessage 用户信息
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileCodeResult generatorMultiFileCode(String userMessage);


    /**
     * 流式生成原生单页面文件
     *
     * @param userMessage 用户信息
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    Flux<String> generatorHtmlCodeStream(String userMessage);

    /**
     * 流式生成原生多页面文件
     *
     * @param userMessage 用户信息
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    Flux<String> generatorMultiFileCodeStream(String userMessage);

    /**
     * 流式生成vue工程项目代码
     * @param appId
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource ="prompt/codegen-vue-project-system-prompt.txt" )
    Flux<String> generateVueProjectCodeStream(@MemoryId Long appId, @UserMessage String userMessage);
}
