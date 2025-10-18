package com.getian.getaicodemother.langgraph4j;

import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.langgraph4j.node.*;
import com.getian.getaicodemother.langgraph4j.state.WorkflowContent;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphRepresentation;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.NodeOutput;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import org.bsc.langgraph4j.prebuilt.MessagesStateGraph;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;

@Slf4j
public class CodeGenWorkflow {
    public CompiledGraph<MessagesState<String>> createWorkflow() {
        try {
            // 创建工作流图
            return new MessagesStateGraph<String>()
                    // 添加节点 - 使用真实的工作节点
                    .addNode("image_collector", ImageCollectorNode.create())
                    .addNode("prompt_enhancer", PromptEnhancerNode.create())
                    .addNode("router", RouterNode.create())
                    .addNode("code_generator", CodeGeneratorNode.create())
                    .addNode("project_builder", ProjectBuilderNode.create())
                    // 添加边
                    .addEdge(START, "image_collector")
                    .addEdge("image_collector", "prompt_enhancer")
                    .addEdge("prompt_enhancer", "router")
                    .addEdge("router", "code_generator")
                    .addEdge("code_generator", "project_builder")
                    .addEdge("project_builder", END)
                    // 编译工作流
                    .compile();
        } catch (GraphStateException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "工作流创建失败");
        }
    }

    /**
     * 执行工作流
     */
    public WorkflowContent executeWorkflow(String originalPrompt) {
        //1.创建一个worlflow
        WorkflowContent initWorkflowContent = WorkflowContent.builder()
                .originalPrompt(originalPrompt)
                .currentStep("初始化")
                .build();
        CompiledGraph<MessagesState<String>> workflow = createWorkflow();
        GraphRepresentation graph = workflow.getGraph(GraphRepresentation.Type.MERMAID);
        log.info("工作流图:\n{}", graph.content());
        log.info("开始执行代码生成工作流");

        //2.开始执行工作流
        WorkflowContent finalContext = null;
        Integer stepCounter = 1;
        for (NodeOutput<MessagesState<String>> nodeOutput : workflow.stream(Map.of(WorkflowContent.WORKFLOW_CONTEXT_KEY, initWorkflowContent))) {
            log.info("--- 第 {} 步完成 ---", stepCounter);
            //显示当前的状态
            MessagesState<String> state = nodeOutput.state();
            WorkflowContent currentContext = WorkflowContent.getContext(state);
            if (currentContext != null) {
                finalContext = currentContext;
                log.info("当前步骤上下文为:{}", currentContext);
            }
            stepCounter++;
        }
        log.info("工作流执行完毕");
        return finalContext;
    }
}
