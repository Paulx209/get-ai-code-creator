package com.getian.getaicodemother.langgraph4j.state;

import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowContent implements Serializable {
    /**
     * 数据宝箱的key
     */
    public static final String WORKFLOW_CONTEXT_KEY = "workflowContext";

    /**
     * 当前的步骤
     */
    private String currentStep;

    /**
     * 初始的提示词
     */
    private String originalPrompt;

    /**
     * 图片资源字符串
     */
    private String imageListStr;

    /**
     * 图片资源列表
     */
    private List<ImageResource> imageList;

    /**
     * 增强的提示词
     */
    private String enhancedPrompt;

    /**
     * 智能路由选择生成的文件类型
     */
    private CodeGenTypeEnum generationType;

    /**
     * 生成的代码目录
     */
    private String generatedCodeDir;

    /**
     * 构建成功的目录
     */
    private String buildResultDir;

    /**
     * 错误信息
     */
    private String errorMessage;

    @Serial
    private static final Long serialVersionUID = 1L;

    //  =====================上下文操作方法===================== workflowContext Map<String,WorkFlowContent>

    public static WorkflowContent getContext(MessagesState<String> state) {
        return (WorkflowContent) state.data().get(WORKFLOW_CONTEXT_KEY);
    }

    public static Map<String, Object> saveContext(WorkflowContent content) {
        return Map.of(WORKFLOW_CONTEXT_KEY, content);
    }
}
