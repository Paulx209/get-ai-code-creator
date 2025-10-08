package com.getian.getaicodemother.controller;

import com.getian.getaicodemother.annotation.AuthCheck;
import com.getian.getaicodemother.common.BaseResponse;
import com.getian.getaicodemother.common.ResultUtils;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.exception.ThrowUtils;
import com.getian.getaicodemother.model.constant.UserConstant;
import com.getian.getaicodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.getian.getaicodemother.model.entity.User;
import com.getian.getaicodemother.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.Response;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.getian.getaicodemother.model.entity.ChatHistory;
import com.getian.getaicodemother.service.ChatHistoryService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author sonicge
 */
@RestController
@RequestMapping("/chatHistory")
@Log4j2
@Tag(name = "对话历史")
public class ChatHistoryController {

    @Resource
    private ChatHistoryService chatHistoryService;
    @Resource
    private UserService userService;

    @GetMapping("/app/{appId}")
    @Operation(summary = "分页查询应用对话历史")
    public BaseResponse<Page<ChatHistory>> listAppChatHistory(@PathVariable Long appId,
                                                              @RequestParam(defaultValue = "10") int pageSize,
                                                              @RequestParam(required = false)LocalDateTime lastCreateTime,
                                                              HttpServletRequest request){
        log.info("分页查询应用对话历史,appId:{},lastCreateTime:{}",appId,lastCreateTime);
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "参数非法");
        User loginUser = userService.getCurrentLoginUser(request);
        Page<ChatHistory> result = chatHistoryService.listAppChatHistoryPage(appId, pageSize, lastCreateTime, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/admin/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(summary = "管理员分页查询应用对话历史")
    public BaseResponse<Page<ChatHistory>> listAllChatHistoryPage(@RequestBody ChatHistoryQueryRequest chatHistoryQueryRequest){
        log.info("管理员分页查询应用对话历史,chatHistoryQueryRequest:{}",chatHistoryQueryRequest);
        ThrowUtils.throwIf(chatHistoryQueryRequest == null, ErrorCode.PARAMS_ERROR, "参数非法");
        int pageNum = chatHistoryQueryRequest.getPageNum();
        int pageSize = chatHistoryQueryRequest.getPageSize();
        //查询数据
        QueryWrapper queryWrapper = chatHistoryService.getChatHistoryQueryWrapper(chatHistoryQueryRequest);
        Page<ChatHistory> page = chatHistoryService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return ResultUtils.success(page);
    }
}
