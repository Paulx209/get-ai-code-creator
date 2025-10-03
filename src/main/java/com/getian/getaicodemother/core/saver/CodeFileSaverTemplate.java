package com.getian.getaicodemother.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.model.constant.AppConstant;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

public abstract class CodeFileSaverTemplate<T> {
    public final File save(T result,Long appId){
        //1.校验输入
        validateInput(result);
        //2.构建文件的唯一目录路径
        String basicFilePath=buildUniqueDirPath(appId);

        //3.保存文件
        saveFile(basicFilePath,result);

        //4.返回目录对象
        return new File(basicFilePath);
    }

    /**
     * 生成唯一目录路径
     * @return
     */
    private  String buildUniqueDirPath(Long appId){
        String bizType=getBizType().getValue();
        String filePathDir= StrUtil.format("{}_{}",bizType,appId);
        String filePath= AppConstant.CODE_OUTPUT_ROOT_DIR +File.separator+filePathDir;
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
