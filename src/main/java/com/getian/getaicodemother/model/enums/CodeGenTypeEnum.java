package com.getian.getaicodemother.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum CodeGenTypeEnum {

    HTML("原生HTML模式","html"),
    MULTI_FILE("多文件模式","multi_file"),
    VUE_PROJECT("Vue项目模式","vue_project");


    private final String text;
    private final String value;

    CodeGenTypeEnum(String text,String value){
        this.text=text;
        this.value=value;
    }

    /**
     * 通过value获取枚举
     * @param value
     * @return
     */
    public static CodeGenTypeEnum getCodeGenTypeEnum(String value){
        if(ObjUtil.isEmpty(value)){
            return null;
        }
        for (CodeGenTypeEnum codeGenTypeEnum : CodeGenTypeEnum.values()) {
            if (codeGenTypeEnum.getValue().equals(value)){
                return codeGenTypeEnum;
            }
        }
        return null;
    }
}
