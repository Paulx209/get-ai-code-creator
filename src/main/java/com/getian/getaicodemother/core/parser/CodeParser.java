package com.getian.getaicodemother.core.parser;

public interface CodeParser<T> {
    /**
     * 解析代码内容
     * @param code
     * @return T HtmlCodeResult / MultiFileCodeResult
     */
    T parseCode(String code);
}
