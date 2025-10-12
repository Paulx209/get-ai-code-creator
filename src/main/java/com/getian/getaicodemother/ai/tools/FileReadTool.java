package com.getian.getaicodemother.ai.tools;

import cn.hutool.json.JSONObject;
import com.getian.getaicodemother.model.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件读取工具
 */
@Slf4j
@Component
public class FileReadTool extends BaseTool {
    @Override
    public String generateToolExecutedResult(JSONObject arguments) {
        String relativePath = arguments.getStr("relativePath");
        return String.format("[工具调用] %s %s ",getDisplayName(),relativePath);
    }

    @Override
    public String getDisplayName() {
        return "读取文件";
    }

    @Override
    public String getToolName() {
        return "readFile";
    }

    @Tool("读取指定路径的文件代码")
    public String readFile(@ToolMemoryId Long appId, @P("读取文件的相对路径") String relativePath){
        try {
            Path path = Paths.get(relativePath);
            //如果该路径不是绝对路径的话，就需要拼接了
            if (!path.isAbsolute()) {
                String projectDirName = "vue_project_" + appId;
                Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
                path = projectRoot.resolve(relativePath);
            }
            if (!Files.exists(path)) {
                return "警告：文件不存在，无需删除 - " + relativePath;
            }
            if (!Files.isRegularFile(path)) {
                return "错误：指定路径不是文件，无法删除 - " + relativePath;
            }
            return Files.readString(path);
        } catch (IOException e) {
            String errorMsg ="读取文件失败" +relativePath +"，错误："+e.getMessage();
            log.info(errorMsg,e);
            return errorMsg;
        }
    }

}
