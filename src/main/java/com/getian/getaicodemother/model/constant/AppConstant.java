package com.getian.getaicodemother.model.constant;

import java.io.File;

public interface AppConstant {
    /**
     * 精选应用优先级
     */
    Integer GOOD_APP_PRIORITY = 99;

    /**
     * 默认应用优先级
     */
    Integer DEFAULT_APP_PRIORITY = 0;

    /**
     * 代码保存目录
     */
    String CODE_OUTPUT_ROOT_DIR = System.getProperty("user.dir")+ File.separator+"tmp"+File.separator+"code_output";

    /**
     * 应用部署目录
     */
    String CODE_DEPLOY_ROOT_DIR = System.getProperty("user.dir")+ File.separator+"tmp"+File.separator+"code_deploy";

    /**
     * 应用部署域名
     */
    String CODE_DEPLOY_HOST="http://localhost";
}
