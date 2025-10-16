package com.getian.getaicodemother.langgraph4j.node;

import com.getian.getaicodemother.langgraph4j.state.WorkflowContent;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

public class PromptEnhancerNode {
    public static AsyncNodeAction<MessagesState<String>> create(){
        return AsyncNodeAction.node_async(state -> {
            //1.第一步，获取状态信息
            WorkflowContent context = WorkflowContent.getContext(state);
            //2.第二步，todo 增强提示词逻辑
            String enhancedPrompt="这是增强后的假数据提示词";
            context.setEnhancedPrompt(enhancedPrompt);
            //3.第三步，返回结果
            return WorkflowContent.saveContext(context);
        });
    }
}
