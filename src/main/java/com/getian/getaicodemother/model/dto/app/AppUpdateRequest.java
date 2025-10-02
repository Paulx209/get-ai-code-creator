package com.getian.getaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppUpdateRequest implements Serializable {
    /**
     * 应用初始化的prompt
     */
    private Long appId;

    /**
     * 应用名称
     */
    private String appName;

    private static final long serialVersionUID = 1L;

}
