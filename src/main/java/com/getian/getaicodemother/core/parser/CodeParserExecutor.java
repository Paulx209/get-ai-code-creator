package com.getian.getaicodemother.core.parser;

import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;

/**
 * 代码执行解析器
 * 根据代码生成类型执行对应的解析逻辑
 */
public class CodeParserExecutor {

    private static final HtmlCodeParser HTML_CODE_PARSER=new HtmlCodeParser();
    private static final MultiFileCodeParser MULTI_FILE_CODE_PARSER=new MultiFileCodeParser();
    public static Object executorCodeParser(String code, CodeGenTypeEnum codeGenTypeEnum){
        return switch(codeGenTypeEnum){
            case HTML -> HTML_CODE_PARSER.parseCode(code);
            case MULTI_FILE -> MULTI_FILE_CODE_PARSER.parseCode(code);
            default -> throw new IllegalArgumentException("不支持该类型代码解析");
        };
    }
}
