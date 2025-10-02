package com.getian.getaicodemother.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.getian.getaicodemother.ai.model.HtmlCodeResult;
import com.getian.getaicodemother.ai.model.MultiFileCodeResult;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class CodeFileSaver {
    //文件保存根目录
    private static final String FILE_SAVE_ROOT_DIR=System.getProperty("user.dir")+ File.separator+"tmp"+File.separator+"code_output";

    //保存HtmlCodeResult
    public static File saveHtmlCodeResult(HtmlCodeResult htmlCodeResult){
        String dirPath = buildUniqueDirPath(CodeGenTypeEnum.HTML.getValue());
        writeToFile(dirPath,"index.html",htmlCodeResult.getHtmlCode());
        return new File(dirPath);
    }

    //保存MultiFileCodeResult
    public static File saveMultiFileCodeResult(MultiFileCodeResult multiFileCodeResult){
        String dirPath = buildUniqueDirPath(CodeGenTypeEnum.MULTI_FILE.getValue());
        //写入css文件
        writeToFile(dirPath,"style.css",multiFileCodeResult.getCssCode());
        //写入js文件
        writeToFile(dirPath,"script.js", multiFileCodeResult.getJsCode());
        //写入html文件
        writeToFile(dirPath,"index.html", multiFileCodeResult.getHtmlCode());
        return new File(dirPath);
    }

    //构建唯一目录路径
    private static String buildUniqueDirPath(String bizType) {
        String uniqueDirPath = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirPath;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }
    //写入单个文件
    private  static void writeToFile(String dirPath,String fileName,String content){
        String filePath=dirPath+File.separator+fileName;
        FileUtil.writeString(content,filePath, StandardCharsets.UTF_8);
    }
}
