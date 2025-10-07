package com.getian.getaicodemother.service.impl;

import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.exception.ThrowUtils;
import com.getian.getaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.getian.getaicodemother.model.entity.ChatHistory;
import com.getian.getaicodemother.mapper.ChatHistoryMapper;
import com.getian.getaicodemother.service.ChatHistoryService;
import org.springframework.stereotype.Service;

/**
 * 对话历史 服务层实现。
 *
 * @author sonicge
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {
    /**
     * 添加对话消息记录
     *
     * @param appId
     * @param message
     * @param messageType
     * @param userId
     * @return
     */
    @Override
    public boolean addChatMessage(Long appId, String message, String messageType, Long userId) {
        //校验参数
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "appId不能为空");
        ThrowUtils.throwIf(message == null || message.isEmpty(), ErrorCode.PARAMS_ERROR, "message不能为空");
        ThrowUtils.throwIf(messageType == null || messageType.isEmpty(), ErrorCode.PARAMS_ERROR, "messageType不能为空");
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "userId不能为空");
        //校验消息类型
        ChatHistoryMessageTypeEnum enumType = ChatHistoryMessageTypeEnum.getEnumByValue(messageType);
        ThrowUtils.throwIf(enumType == null, ErrorCode.PARAMS_ERROR, "messageType类型错误");
        //添加消息记录
        ChatHistory history = ChatHistory.builder()
                .appId(appId)
                .messageType(enumType.getValue())
                .message(message)
                .userId(userId).build();
        return this.save(history);
    }
}
