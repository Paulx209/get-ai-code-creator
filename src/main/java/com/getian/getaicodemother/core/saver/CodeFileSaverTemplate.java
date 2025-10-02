package com.getian.getaicodemother.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

public abstract class CodeFileSaverTemplate<T> {
    //文件保存根目录
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "code_output";


    public final File save(T result){
        //1.校验输入
        validateInput(result);
        //2.构建文件的唯一目录路径
        String basicFilePath=buildUniqueDirPath();

        //3.保存文件
        saveFile(basicFilePath,result);

        //4.返回目录对象
        return new File(basicFilePath);
    }

    /**
     * 生成唯一目录路径
     * @return
     */
    private  String buildUniqueDirPath(){
        String bizType=getBizType().getValue();
        String filePathDir= StrUtil.format("{}_{}",bizType,IdUtil.getSnowflakeNextIdStr());
        String filePath=FILE_SAVE_ROOT_DIR+File.separator+filePathDir;
        FileUtil.mkdir(filePath);
        return filePath;
    }

    /**
     * 校验输入
     * @param result
     */
    protected void validateInput(T result){
        if(result == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
    }

    //写入单个文件
    protected final void writeToFile(String dirPath,String fileName,String content){
        String filePath=dirPath+File.separator+fileName;
        FileUtil.writeString(content,filePath, StandardCharsets.UTF_8);
    }

    /**
     * 保存文件
     * @param basicFilePath 目录路径
     * @param result  HtmlCodeResult / MultiFileCodeResult
     */
    protected abstract void saveFile(String basicFilePath, T result);

    /**
     * 获取生成类型
     * @return
     */
    protected abstract CodeGenTypeEnum getBizType();
}
