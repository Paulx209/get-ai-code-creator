package com.getian.getaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppAddRequest implements Serializable {
    /**
     * 应用初始化的prompt
     */
    private String prompt;

    private static final long serialVersionUID = 1L;

}
