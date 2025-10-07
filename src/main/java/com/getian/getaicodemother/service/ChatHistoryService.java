package com.getian.getaicodemother.service;

import com.mybatisflex.core.service.IService;
import com.getian.getaicodemother.model.entity.ChatHistory;

/**
 * 对话历史 服务层。
 *
 * @author sonicge
 */
public interface ChatHistoryService extends IService<ChatHistory> {
    boolean addChatMessage(Long appId,String message,String messageType,Long userId);
}
