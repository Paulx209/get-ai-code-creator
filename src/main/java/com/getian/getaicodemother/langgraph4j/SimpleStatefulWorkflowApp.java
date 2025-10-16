package com.getian.getaicodemother.langgraph4j;

import com.getian.getaicodemother.langgraph4j.state.WorkflowContent;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.*;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import org.bsc.langgraph4j.prebuilt.MessagesStateGraph;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;

/**
 * 简化版带状态定义的工作流 --- 只定义状态结构，不具体实现逻辑
 */
@Slf4j
public class SimpleStatefulWorkflowApp {
    //创建节点
    static AsyncNodeAction<MessagesState<String>> makeStatefulNode(String nodeName,String message){
        return AsyncNodeAction.node_async(state -> {
            //1.先获取状态
            WorkflowContent context =WorkflowContent.getContext(state);
            log.info("执行节点:{} - {}",nodeName,message);
            //2.只记录当前步骤，不做状态的流转
            if (context != null) {
                context.setCurrentStep(nodeName);
            }
            //3.返回状态
            return WorkflowContent.saveContext(context);
        });
    }

    public static void main(String[] args) throws GraphStateException {
        // 创建工作流图
        CompiledGraph<MessagesState<String>> workflow = new MessagesStateGraph<String>()
                // 添加节点
                .addNode("image_collector", makeStatefulNode("image_collector","获取图片素材"))
                .addNode("prompt_enhancer", makeStatefulNode("prompt_enhancer","增强提示词"))
                .addNode("router", makeStatefulNode("router","智能路由选择"))
                .addNode("code_generator", makeStatefulNode("code_generator","网站代码生成"))
                .addNode("project_builder", makeStatefulNode("project_builder","项目构建"))

                // 添加边
                .addEdge(START, "image_collector")                // 开始 -> 图片收集
                .addEdge("image_collector", "prompt_enhancer")    // 图片收集 -> 提示词增强
                .addEdge("prompt_enhancer", "router")             // 提示词增强 -> 智能路由
                .addEdge("router", "code_generator")              // 智能路由 -> 代码生成
                .addEdge("code_generator", "project_builder")     // 代码生成 -> 项目构建
                .addEdge("project_builder", END)                  // 项目构建 -> 结束

                // 编译工作流
                .compile();

        log.info("开始执行工作流");

        String originalPrompt = "生成一个简单的网站即可，主题为旅游";
        WorkflowContent workflowContent=WorkflowContent.builder()
                .originalPrompt(originalPrompt)
                .currentStep("初始化")
                .build();
        log.info("初始输入:{}",workflowContent.getOriginalPrompt());
        log.info("开始执行工作流");

        //显示工作流
        GraphRepresentation graph = workflow.getGraph(GraphRepresentation.Type.MERMAID);
        log.info("工作流图:{}",graph.content());

        //执行工作流
        int stepCounter = 1;
        //workflow.stream，以流式方法启动工作流，要往里面放入一个初始化的map
        for(NodeOutput<MessagesState<String>> step: workflow.stream(Map.of(WorkflowContent.WORKFLOW_CONTEXT_KEY,workflowContent))){
            log.info("第 {} 步完成 ",stepCounter);
            //显示当前状态
            WorkflowContent context = WorkflowContent.getContext(step.state());
            if(context != null){
                log.info("当前状态:{}",context.getCurrentStep());
            }
            stepCounter++;
        }
        log.info("工作流执行完成");

    }
}
