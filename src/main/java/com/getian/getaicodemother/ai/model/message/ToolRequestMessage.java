package com.getian.getaicodemother.ai.model.message;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 工具调用信息
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ToolRequestMessage extends StreamMessage {

    /**
     * 工具调用请求的唯一标识，可以根据id将 工具调用 和 工具执行结果 关联起来
     */
    private String id;

    /**
     * 要调用的工具名称  eg:writeFile
     */
    private String name;

    /**
     * 工具调用传递的参数
     */
    private String arguments;

    public ToolRequestMessage(ToolExecutionRequest toolExecutionRequest) {
        super(StreamMessageTypeEnum.TOOL_REQUEST.getValue());
        this.id = toolExecutionRequest.id();
        this.arguments = toolExecutionRequest.arguments();
        this.name = toolExecutionRequest.name();
    }
}
