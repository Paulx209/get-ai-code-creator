package com.getian.getaicodemother.ai.tools;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.exception.ThrowUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具管理器
 * 统一管理所有工具，提供根据名称获取工具的功能
 */
@Slf4j
@Component
@Getter
public class ToolManager {
    /**
     * 工具名称到工具的映射
     */
    private final Map<String, BaseTool> toolMap = new HashMap<>();

    /**
     * 自动注入所有工具
     */
    @Resource
    private BaseTool[] baseTools;

    @PostConstruct
    public void initTools() {
        for (BaseTool tool : baseTools) {
            toolMap.put(tool.getToolName(), tool);
            log.info("注册工具：{} -> {}", tool.getToolName(), tool.getDisplayName());
        }
        log.info("工具初始化完毕，共初始化{}个工具", toolMap.size());
    }

    public BaseTool[] getAllTools(){
        return baseTools;
    }

    /**
     * 根据toolName获取tool工具
     * @param toolName
     * @return
     */
    public BaseTool getTool(String toolName){
        ThrowUtils.throwIf(StrUtil.isBlank(toolName), ErrorCode.PARAMS_ERROR,"工具名称为空");
        BaseTool baseTool = toolMap.get(toolName);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(baseTool),ErrorCode.PARAMS_ERROR,"工具类型不存在，工具类型:"+toolName);
        return baseTool;
    }
}
