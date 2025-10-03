package com.getian.getaicodemother.controller;

import cn.hutool.json.JSONUtil;
import com.getian.getaicodemother.model.dto.app.AppQueryRequest;
import com.getian.getaicodemother.model.entity.App;
import com.getian.getaicodemother.service.AppService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AppControllerTest {
    @Resource
    private AppService appService;

    @Test
    public void test1(){
        AppQueryRequest appQueryRequest=new AppQueryRequest();
        QueryWrapper appQueryWrapper = appService.getAppQueryWrapper(appQueryRequest);
        System.out.println(appQueryWrapper);
        Page<App> page=new Page<>(1,10);
        Page<App> resPage = appService.page(page, appQueryWrapper);
        System.out.println(JSONUtil.toJsonStr(resPage));
    }

}
