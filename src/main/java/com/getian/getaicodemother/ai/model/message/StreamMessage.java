package com.getian.getaicodemother.ai.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流式信息响应基类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamMessage {
    /**
     * 消息类型 共有三种  AI响应 / 工具调用 / 工具调用完成
     */
    private String type;
}
