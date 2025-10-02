package com.getian.getaicodemother.ai.model;

import jdk.jfr.Description;
import lombok.Data;

@Description(value = "生成多文件代码的结果")
@Data
public class MultiFileCodeResult {
    @Description(value = "HTMl代码")
    private String htmlCode;

    @Description(value = "CSS代码")
    private String cssCode;

    @Description(value = "JS代码")
    private String jsCode;

    @Description(value = "生成代码的描述")
    private String description;
}
