package com.getian.getaicodemother.core.handler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.getian.getaicodemother.ai.model.message.*;
import com.getian.getaicodemother.core.builder.VueProjectBuilder;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.exception.ThrowUtils;
import com.getian.getaicodemother.model.constant.AppConstant;
import com.getian.getaicodemother.model.entity.User;
import com.getian.getaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.getian.getaicodemother.service.ChatHistoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class JsonMessageStreamHandler {
    @Resource
    private VueProjectBuilder vueProjectBuilder;
    public Flux<String> handleJsonMessage(Flux<String> originFlux, ChatHistoryService chatHistoryService, Long appId, User loginUser){
        StringBuilder chatHistoryBuilder=new StringBuilder();
        Set<String> seenTools=new HashSet<>();

        return originFlux.map(chunk ->{
            //解析每个data数据,并且返回
            return handleJsonMessageChunk(chunk,chatHistoryBuilder,seenTools);
        }).filter(StrUtil :: isNotBlank) //过滤空字符串
                .doOnComplete(()->{
                    //流式响应完成之后，需要将信息添加到数据库中
                    String aiResponse=chatHistoryBuilder.toString();
                    chatHistoryService.addChatMessage(appId,aiResponse, ChatHistoryMessageTypeEnum.AI.getValue(),loginUser.getId());
                    String vueProjectPath= AppConstant.CODE_OUTPUT_ROOT_DIR+ File.separator+"vue_project_"+appId;
                    vueProjectBuilder.buildVueProjectAsync(vueProjectPath);
                }).doOnError(error -> {
                    //如果AI回复失败，也要记录错误消息
                    String errorMessage = "AI回复失败: " + error.getMessage();
                    chatHistoryService.addChatMessage(appId, errorMessage, ChatHistoryMessageTypeEnum.AI.getValue(), loginUser.getId());
                });
    }
    /**
     * 解析并收集stream数据
     * @param chunk
     * @param chatHistoryBuilder
     * @param seenTools
     * @return
     */
    private String handleJsonMessageChunk(String chunk,StringBuilder chatHistoryBuilder,Set<String> seenTools){
        //解析json
        StreamMessage streamMessage = JSONUtil.toBean(chunk, StreamMessage.class);
        StreamMessageTypeEnum typeEnum = StreamMessageTypeEnum.getEnumByValue(streamMessage.getType());
        ThrowUtils.throwIf(typeEnum == null, ErrorCode.PARAMS_ERROR, "未知的stream消息类型: " + streamMessage.getType());
        switch (typeEnum){
            //如果是AI响应的内容
            case AI_RESPONSE -> {
                AiResponseMessage aiResponseMessage = JSONUtil.toBean(chunk, AiResponseMessage.class);
                String data = aiResponseMessage.getContent();
                chatHistoryBuilder.append(data);
                return data;
            }
            //如果是工具请求内容
            case TOOL_REQUEST -> {
                ToolRequestMessage toolRequestMessage = JSONUtil.toBean(chunk, ToolRequestMessage.class);
                String toolId=toolRequestMessage.getId();
                //如果工具ID不为空，且没有见过这个工具，就添加到seenTools中
                if(toolId !=null  && !seenTools.contains(toolId)){
                    seenTools.add(toolId);
                    return "\n\n[选择工具] 写入文件\n\n";
                }else{
                    //不是第一次调用该工具了，直接返回空字符串
                    return "";
                }
            }
            case TOOL_EXECUTED -> {
                ToolExecutedMessage toolExecutedMessage = JSONUtil.toBean(chunk, ToolExecutedMessage.class);
                JSONObject jsonObject = JSONUtil.parseObj(toolExecutedMessage.getArguments());
                String relativeFilePath = jsonObject.getStr("relativeFilePath");
                String suffix = FileUtil.getSuffix(relativeFilePath);
                String content = jsonObject.getStr("content");
                String result=String.format("""
                        [工具调用] 写入文件 %s
                        ``` %s
                        %s
                        ```
                        """,relativeFilePath,suffix,content);
                //输出前端和要持久化的内容
                String output = String.format("\n\n%s\n\n", result);
                chatHistoryBuilder.append(output);
                return output;
            }
            default -> {
                log.info("未知的stream消息类型: {}",streamMessage.getType());
                return "";
            }
        }
    }
}
