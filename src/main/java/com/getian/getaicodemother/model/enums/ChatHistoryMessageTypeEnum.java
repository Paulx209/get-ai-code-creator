package com.getian.getaicodemother.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum ChatHistoryMessageTypeEnum {
    USER("用户", "user"),
    AI("AI", "ai");


    private final String text;
    private final String value;

    ChatHistoryMessageTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
    /**
     * 根据枚举值获取枚举
     * @param value
     * @return
     */
    public static ChatHistoryMessageTypeEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (ChatHistoryMessageTypeEnum enumType : ChatHistoryMessageTypeEnum.values()) {
            if (enumType.getValue().equals(value)) {
                return enumType;
            }
        }
        return null;
    }
}
