package com.getian.getaicodemother.core.saver;

import com.getian.getaicodemother.ai.model.HtmlCodeResult;
import com.getian.getaicodemother.ai.model.MultiFileCodeResult;
import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;

public class CodeFileSaverExecutor {
    private static final HtmlCodeFileSaver htmlCodeFileSaver=new HtmlCodeFileSaver();
    private static final MultiFileCodeFileSaver multiFileCodeFileSaver=new MultiFileCodeFileSaver();

    public static File executeFileSave(Object codeResult, CodeGenTypeEnum codeGenType){
        return switch (codeGenType){
            case HTML -> htmlCodeFileSaver.save((HtmlCodeResult) codeResult);
            case MULTI_FILE -> multiFileCodeFileSaver.save((MultiFileCodeResult) codeResult);
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR,"不支持代码生成类型："+codeGenType);
        };
    }
}
