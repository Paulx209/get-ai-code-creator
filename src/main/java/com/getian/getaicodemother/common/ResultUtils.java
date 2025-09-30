package com.getian.getaicodemother.common;

import com.getian.getaicodemother.exception.ErrorCode;

public class ResultUtils {
    //成功响应
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"success");
    }
    //失败响应  使用已定义的错误码和错误信息
    public static <T> BaseResponse<T> error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    //失败响应  使用用户输入的错误码和错误信息
    public static <T> BaseResponse<T> error(int code,String message){
        return new BaseResponse<>(code,null,message);
    }

    //失败响应
    public static <T> BaseResponse<T> error(ErrorCode errorCode,String message){
        return new BaseResponse<>(errorCode.getCode(),null,message);
    }
}
