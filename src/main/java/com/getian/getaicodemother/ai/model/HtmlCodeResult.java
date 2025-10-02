package com.getian.getaicodemother.ai.model;

import jdk.jfr.Description;
import lombok.Data;

@Description(value = "生成原生单页面代码的结果")
@Data
public class HtmlCodeResult {
    @Description(value = "HTMl代码")
    private String htmlCode;

    @Description(value = "生成代码的描述")
    private String description;
}
