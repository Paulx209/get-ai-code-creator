package com.getian.getaicodemother.common;

import com.getian.getaicodemother.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {
    // 状态码
    private int code;
    // 数据
    private T data;
    // 返回信息
    private String message;

    public BaseResponse(int code,T data,String message){
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code,T data){
        this.code=code;
        this.data=data;
        this.message="";
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage());
    }
}
