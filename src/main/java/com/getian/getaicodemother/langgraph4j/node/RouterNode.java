package com.getian.getaicodemother.langgraph4j.node;

import com.getian.getaicodemother.ai.AiCodeGenTypeRoutingService;
import com.getian.getaicodemother.langgraph4j.state.WorkflowContent;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;
import com.getian.getaicodemother.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * 路由Node，整个工作流中的第三步
 */
@Slf4j
public class RouterNode {
    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContent context = WorkflowContent.getContext(state);
            log.info("执行节点: 智能路由");
            CodeGenTypeEnum codeGenTypeEnum = null;
            try {
                // 实际执行智能路由逻辑
                String originalPrompt = context.getOriginalPrompt();
                AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService = SpringContextUtil.getBean(AiCodeGenTypeRoutingService.class);
                codeGenTypeEnum = aiCodeGenTypeRoutingService.routeCodeGenType(originalPrompt);
                log.info("路由决策完成，选择类型: {}", codeGenTypeEnum.getText());
            } catch (Exception e) {
                log.error("智能路由失败:{}", e.getMessage(),e);
            }
            // 更新状态
            context.setCurrentStep("智能路由");
            context.setGenerationType(codeGenTypeEnum);
            return WorkflowContent.saveContext(context);
        });
    }
}
