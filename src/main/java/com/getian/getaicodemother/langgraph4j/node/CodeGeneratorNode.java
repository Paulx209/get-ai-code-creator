package com.getian.getaicodemother.langgraph4j.node;

import com.getian.getaicodemother.core.AiCodeGeneratorFacade;
import com.getian.getaicodemother.langgraph4j.state.WorkflowContent;
import com.getian.getaicodemother.model.constant.AppConstant;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;
import com.getian.getaicodemother.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * 代码生成节点  整个流程中的第四步
 */
@Slf4j
public class CodeGeneratorNode {
    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContent context = WorkflowContent.getContext(state);
            log.info("执行节点: 代码生成");
            //使用增强提示词作为发给AI的用户消息
            String enhancedPrompt = context.getEnhancedPrompt();
            CodeGenTypeEnum generationType = context.getGenerationType();
            log.info("开始生成代码,类型:{}({})",generationType.getValue(),generationType.getText());
            //先将appId写死，后续再根据实际情况修改
            Long appId=0L;
            AiCodeGeneratorFacade aiCodeGeneratorFacade = SpringContextUtil.getBean(AiCodeGeneratorFacade.class);

            Flux<String> stringFlux = aiCodeGeneratorFacade.generateAndSaveCodeStream(enhancedPrompt, generationType, appId);
            stringFlux.blockLast(Duration.ofMinutes(10)); //最多等待十分钟
            //根据类型设置生成目录
            String generatedCodeDir = String.format("%s/%s_%s", AppConstant.CODE_OUTPUT_ROOT_DIR,generationType.getValue(),appId);
            log.info("生成代码完成，目录: {}", generatedCodeDir);
            // 更新状态
            context.setCurrentStep("代码生成");
            context.setGeneratedCodeDir(generatedCodeDir);
            return WorkflowContent.saveContext(context);
        });
    }
}
