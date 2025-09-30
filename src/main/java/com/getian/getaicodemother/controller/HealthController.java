package com.getian.getaicodemother.controller;

import com.getian.getaicodemother.common.BaseResponse;
import com.getian.getaicodemother.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")

public class HealthController {
    @GetMapping("")
    public BaseResponse healthCheck(){
        return ResultUtils.success("OK");
    }
}
