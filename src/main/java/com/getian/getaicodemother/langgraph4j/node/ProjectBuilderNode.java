package com.getian.getaicodemother.langgraph4j.node;

import com.getian.getaicodemother.core.builder.VueProjectBuilder;
import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.langgraph4j.state.WorkflowContent;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;
import com.getian.getaicodemother.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.io.File;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * 项目打包节点，整个流程中第五步
 */
@Slf4j
public class ProjectBuilderNode {
    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContent context = WorkflowContent.getContext(state);
            log.info("执行节点: 项目构建");

            //获取必要的参数
            String generatedCodeDir  = context.getGeneratedCodeDir();
            CodeGenTypeEnum generationType = context.getGenerationType();
            String buildResultDir = "";
            if(generationType == CodeGenTypeEnum.VUE_PROJECT){
                try {
                    VueProjectBuilder vueProjectBuilder = SpringContextUtil.getBean(VueProjectBuilder.class);
                    boolean buildSuccess = vueProjectBuilder.buildVueProject(generatedCodeDir);
                    if(buildSuccess){
                        //构建成功，返回dir目录路径
                        buildResultDir = generatedCodeDir + File.separator +"dist";
                        log.info("Vue项目构建成功，结果目录: {}", buildResultDir);
                    }else{
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"Vue项目构建失败");
                    }
                } catch (BusinessException e) {
                    log.error("Vue项目构建失败:{}", e.getMessage(),e);
                }
            }else{
                buildResultDir = generatedCodeDir;
            }
            // 更新状态
            context.setCurrentStep("项目构建");
            context.setBuildResultDir(buildResultDir);
            log.info("项目构建完成，结果目录: {}", buildResultDir);
            return WorkflowContent.saveContext(context);
        });
    }
}
