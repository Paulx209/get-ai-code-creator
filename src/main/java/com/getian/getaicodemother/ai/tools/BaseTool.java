package com.getian.getaicodemother.ai.tools;

import cn.hutool.json.JSONObject;
import com.google.gson.JsonObject;

/**
 * 工具基类
 * 定义所有工具的通用接口
 */
public  abstract   class BaseTool {
    /**
     * 获取所有工具的英文名称(对应方法名)
     * @return
     */
    public abstract  String getToolName();

    /**
     * 获取所有工具的中文展示名称
     * @return
     */
    public abstract String getDisplayName();

    /**
     * 生成工具请求时的响应
     * @return
     */
    public  String generateToolRequestResponse(){
        return String.format("\n\n [选择工具] %s \n\n",getDisplayName());
    };

    /**
     * 执行工具完成时的响应
     * @return
     */
    public abstract String generateToolExecutedResult(JSONObject arguments);


}
