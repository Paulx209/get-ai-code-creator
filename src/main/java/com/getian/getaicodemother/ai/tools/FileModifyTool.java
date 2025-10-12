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
import java.nio.file.StandardOpenOption;

@Slf4j
@Component
public class FileModifyTool extends BaseTool {

    @Override
    public String generateToolExecutedResult(JSONObject arguments) {
        String relativeFilePath = arguments.getStr("relativePath");
        String oldContent = arguments.getStr("oldContent");
        String newContent = arguments.getStr("newContent");
        // 显示对比内容
        return String.format("""
                [工具调用] %s %s
                
                替换前：
                ```
                %s
                ```
                
                替换后：
                ```
                %s
                ```
                """, getDisplayName(), relativeFilePath, oldContent, newContent);
    }

    @Override
    public String getDisplayName() {
        return "修改文件";
    }

    @Override
    public String getToolName() {
        return "modifyTool";
    }

    @Tool("修改指定路径的代码文件")
    public String modifyTool(@P("文件的相对路径") String relativePath,
                             @P("旧数据") String oldContent,
                             @P("新数据") String newContent,
                             @ToolMemoryId Long appId){
        try {
            //1.判断相对路径是否存在
            Path path= Paths.get(relativePath);
            if(!path.isAbsolute()){
                //如果不是绝对路径
                String projectDirname="vue_project_"+appId;
                Path projectRoot=Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR,projectDirname);
                path=projectRoot.resolve(relativePath);
            }
            //2.读取旧文件中的内容
            String originContent = Files.readString(path);
            if(!originContent.contains(oldContent)){
                return "错误，文件中未找到要替换的内容，文件未修改 - "+relativePath;
            }
            String replaceContent = originContent.replace(oldContent, newContent);
            if(replaceContent.equals(originContent)){
                return "警告，替换后文件并没有发生变化 - " + relativePath;
            }
            //3.修改文件
            Files.writeString(path,replaceContent, StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
            log.info("文件修改成功 - {}",path.toAbsolutePath());
            return "文件修改成功："+relativePath;
        } catch (IOException e) {
            String errorMessage = "读取文件失败：" + relativePath + "，错误："+e.getMessage();
            log.info("文件修改失败,{}",errorMessage);
            return errorMessage;
        }
    }
}









