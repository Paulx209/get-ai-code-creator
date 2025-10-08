package com.getian.getaicodemother.service.impl;

import cn.hutool.core.util.StrUtil;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.exception.ThrowUtils;
import com.getian.getaicodemother.model.constant.UserConstant;
import com.getian.getaicodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.getian.getaicodemother.model.entity.App;
import com.getian.getaicodemother.model.entity.User;
import com.getian.getaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.getian.getaicodemother.service.AppService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.getian.getaicodemother.model.entity.ChatHistory;
import com.getian.getaicodemother.mapper.ChatHistoryMapper;
import com.getian.getaicodemother.service.ChatHistoryService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 服务层实现。
 *
 * @author sonicge
 */
@Service
@Slf4j
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {
    @Resource
    @Lazy
    private AppService appService;

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

    /**
     * 根据appId删除对话记录
     *
     * @param appId
     * @return
     */
    @Override
    public boolean deleteByAppId(Long appId) {
        //校验参数
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "参数非法");
        //构造queryWrapper
        QueryWrapper queryWrapper = QueryWrapper.create().eq("appId", appId);
        return this.remove(queryWrapper);
    }

    /**
     * 构造查询条件器
     * @param chatHistoryQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getChatHistoryQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        ThrowUtils.throwIf(chatHistoryQueryRequest == null, ErrorCode.PARAMS_ERROR, "参数非法");
        Long id = chatHistoryQueryRequest.getId();
        String message = chatHistoryQueryRequest.getMessage();
        String messageType = chatHistoryQueryRequest.getMessageType();
        Long appId = chatHistoryQueryRequest.getAppId();
        Long userId = chatHistoryQueryRequest.getUserId();
        LocalDateTime lastCreateTime = chatHistoryQueryRequest.getLastCreateTime();
        String sortField = chatHistoryQueryRequest.getSortField();
        String sortOrder = chatHistoryQueryRequest.getSortOrder();

        QueryWrapper queryWrapper=QueryWrapper.create()
                .eq("id",id)
                .like("message",message)
                .eq("messageType",messageType)
                .eq("appId",appId)
                .eq("userId",userId);
        //游标查询逻辑 --- 只使用createTime作为游标
        if (lastCreateTime != null) {
            queryWrapper.lt("createTime",lastCreateTime);
        }
        //排序
        if(StrUtil.isNotBlank(sortField)){
            queryWrapper.orderBy(sortField,"ascend".equals(sortOrder));
        }else{
            //默认按照创建时间排序
            queryWrapper.orderBy("createTime",false);
        }
        return queryWrapper;
    }

    /**
     * 游标分页查询应用对话记录
     * @param appId
     * @param pageSize
     * @param lastCreateTime
     * @param loginUser
     * @return
     */
    @Override
    public Page<ChatHistory> listAppChatHistoryPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "appId不能为空");
        ThrowUtils.throwIf(pageSize <=0 || pageSize >50 , ErrorCode.PARAMS_ERROR, "pageSize参数错误");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.PARAMS_ERROR, "用户未登录");
        //验证权限，只有应用创建者和管理员可以查看;
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "应用不存在");
        boolean isAdmin = loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE);
        ThrowUtils.throwIf(!isAdmin && !app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限");
        //构建查询条件
        ChatHistoryQueryRequest chatHistoryQueryRequest=new ChatHistoryQueryRequest();
        chatHistoryQueryRequest.setAppId(appId);
        chatHistoryQueryRequest.setLastCreateTime(lastCreateTime);
        QueryWrapper chatHistoryQueryWrapper = this.getChatHistoryQueryWrapper(chatHistoryQueryRequest);
        return this.page(new Page<>(1,pageSize),chatHistoryQueryWrapper);
    }

    /**
     * 加载聊天历史记录到redis缓存中
     * @param appId
     * @param chatMemory
     * @param maxCount
     * @return
     */
    @Override
    public int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount) {
        log.info("开始加载appId:{}的聊天记录",appId);
        try {
            //1.构造查询条件，查询appId对应的聊天记录
            QueryWrapper queryWrapper=QueryWrapper.create()
                    .eq("appId",appId)
                    .orderBy(ChatHistory::getCreateTime,false)
                    .limit(0,maxCount);
            List<ChatHistory> chatHistoryList = this.list(queryWrapper);
            //2.进行翻转,覆盖之前变量
            chatHistoryList=chatHistoryList.reversed();
            //3.根据不同messageType，将聊天记录添加到chatMemory中
            int loadedCount=0;
            chatMemory.clear();
            for(ChatHistory chatHistory:chatHistoryList){
                String messageType = chatHistory.getMessageType();
                if(messageType.equals(ChatHistoryMessageTypeEnum.USER.getValue())){
                    chatMemory.add(UserMessage.from(chatHistory.getMessage()));
                }else if(messageType.equals(ChatHistoryMessageTypeEnum.AI.getValue())){
                    chatMemory.add(AiMessage.from(chatHistory.getMessage()));
                }
                loadedCount++;
            }
            log.info("成功添加了{}条聊天记录到chatMemory中",loadedCount);
            return loadedCount;
        } catch (Exception e) {
            log.error("加载历史会话失败,appId:{},error:{}",appId,e.getMessage());
            return 0;
        }
    }
}
