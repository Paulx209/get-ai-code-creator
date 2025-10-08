package com.getian.getaicodemother.service;

import com.getian.getaicodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.getian.getaicodemother.model.entity.User;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.getian.getaicodemother.model.entity.ChatHistory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author sonicge
 */
public interface ChatHistoryService extends IService<ChatHistory> {
    boolean addChatMessage(Long appId,String message,String messageType,Long userId);

    boolean deleteByAppId(Long appId);

    QueryWrapper getChatHistoryQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    Page<ChatHistory> listAppChatHistoryPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser);

    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory,int maxCount);
}
