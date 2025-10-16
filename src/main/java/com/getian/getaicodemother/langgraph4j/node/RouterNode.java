package com.getian.getaicodemother.langgraph4j.node;

import com.getian.getaicodemother.langgraph4j.state.WorkflowContent;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
public class RouterNode {
    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContent context = WorkflowContent.getContext(state);
            log.info("执行节点: 智能路由");

            // TODO: 实际执行智能路由逻辑

            // 简单的假数据
            CodeGenTypeEnum generationType = CodeGenTypeEnum.HTML;
            // 更新状态
            context.setCurrentStep("智能路由");
            context.setGenerationType(generationType);
            log.info("路由决策完成，选择类型: {}", generationType.getText());
            return WorkflowContent.saveContext(context);
        });
    }
}
