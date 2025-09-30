package com.getian.getaicodemother.exception;

import com.getian.getaicodemother.common.BaseResponse;
import com.getian.getaicodemother.common.ResultUtils;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    //全局异常处理器，当抛出异常后会被该类中的方法拦截掉处理！
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e){
        log.error("BusinessException ", e);
        return ResultUtils.error(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(BusinessException e){
        log.error("RuntimeException ",e);
        return ResultUtils.error(e.getCode(),e.getMessage());
    }
}
