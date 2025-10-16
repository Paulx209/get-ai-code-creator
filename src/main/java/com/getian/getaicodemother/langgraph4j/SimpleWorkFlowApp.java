package com.getian.getaicodemother.langgraph4j;

import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.NodeOutput;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import org.bsc.langgraph4j.prebuilt.MessagesStateGraph;
import java.util.Map;
import java.util.Optional;


import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * 简化版网站生成工作流应用 - 使用 MessagesState
 */
@Slf4j
public class SimpleWorkFlowApp {

    /**
     * 创建工作节点的通用方法
     */
    static AsyncNodeAction<MessagesState<String>> makeNode(String message) {
        //为什么是MessageState类型的？ mcp协议是什么？
        return node_async(state -> {
            log.info("执行节点: {}", message);
            return Map.of("messages", message);
        });
    }

    public static void main(String[] args) throws GraphStateException {

        // 创建工作流图
        CompiledGraph<MessagesState<String>> workflow = new MessagesStateGraph<String>()
                // 添加节点
                .addNode("image_collector", makeNode("获取图片素材"))
                .addNode("prompt_enhancer", makeNode("增强提示词"))
                .addNode("router", makeNode("智能路由选择"))
                .addNode("code_generator", makeNode("网站代码生成"))
                .addNode("project_builder", makeNode("项目构建"))

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
//
//        GraphRepresentation graph = workflow.getGraph(GraphRepresentation.Type.MERMAID);
//        log.info("工作流图: \n{}", graph.content());

        int stepCounter = 1;
        //可以理解为：按照顺序遍历图中的各个Node
        for (NodeOutput<MessagesState<String>> step : workflow.stream(Map.of())) {
            log.info("第 {} 步完成 ", stepCounter);
            MessagesState<String> state = step.state();
            Optional<String> data = state.lastMessage();
            if(!data.isEmpty()){
                log.info("步骤输出：{}", data.get());
            }
            stepCounter++;
        }

        log.info("工作流执行完成！");
    }
}
