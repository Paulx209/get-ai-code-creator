package com.getian.getaicodemother.exception;


public class ThrowUtils {
    /**
     * 如果条件成立，抛出异常
     * @param condition 条件
     * @param runtimeException 异常
     */
    public static void throwIf(boolean condition,RuntimeException runtimeException){
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 如果条件成立，抛出异常
     * @param condition 条件
     * @param errorCode 错误状态码
     */
    public static void throwIf(boolean condition,ErrorCode errorCode){
        if(condition){
            throw new BusinessException(errorCode);
        }
    }

    /**
     * 如果条件成立，抛出异常
     * @param condition 条件
     * @param errorCode 错误状态码
     * @param message 错误信息
     */
    public static void throwIf(boolean condition,ErrorCode errorCode,String message){
        throwIf(condition,new BusinessException(errorCode.getCode(),message));
    }

}
