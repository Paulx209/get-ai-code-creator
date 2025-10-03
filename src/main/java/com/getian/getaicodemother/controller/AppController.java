package com.getian.getaicodemother.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.getian.getaicodemother.annotation.AuthCheck;
import com.getian.getaicodemother.common.BaseResponse;
import com.getian.getaicodemother.common.DeleteRequest;
import com.getian.getaicodemother.common.ResultUtils;
import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.exception.ThrowUtils;
import com.getian.getaicodemother.model.constant.AppConstant;
import com.getian.getaicodemother.model.constant.UserConstant;
import com.getian.getaicodemother.model.dto.app.AppAddRequest;
import com.getian.getaicodemother.model.dto.app.AppAdminUpdateRequest;
import com.getian.getaicodemother.model.dto.app.AppQueryRequest;
import com.getian.getaicodemother.model.dto.app.AppUpdateRequest;
import com.getian.getaicodemother.model.entity.User;
import com.getian.getaicodemother.model.enums.UserRoleEnum;
import com.getian.getaicodemother.model.vo.app.AppVO;
import com.getian.getaicodemother.service.UserService;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import dev.langchain4j.agent.tool.P;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import com.getian.getaicodemother.model.entity.App;
import com.getian.getaicodemother.service.AppService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author sonicge
 */
@RestController
@RequestMapping("/app")
@Tag(name = "应用相关接口")
@Log4j2
public class AppController {

    @Autowired
    private AppService appService;
    @Resource
    private UserService userService;
    /**
     * 新增
     * @param addRequest 应用
     */
    @PostMapping("/add")
    @Operation(summary = "新增应用")
    public BaseResponse<Long> addApp(@RequestBody AppAddRequest addRequest, HttpServletRequest request) {
        log.info("新增应用:{}",addRequest);
        ThrowUtils.throwIf(addRequest == null , ErrorCode.PARAMS_ERROR,"参数为空");

        String initPrompt=addRequest.getPrompt();
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt),ErrorCode.PARAMS_ERROR,"提示词为空");

        Long appId=appService.insertApp(addRequest,request);
        return ResultUtils.success(appId);
    }


    /**
     * 删除应用
     * @param deleteRequest
     * @return
     */
    @DeleteMapping("delete")
    @Operation(summary = "删除应用")
    public BaseResponse<Boolean> remove(@RequestBody DeleteRequest deleteRequest,HttpServletRequest request) {
        log.info("删除应用:{}",deleteRequest);
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null , ErrorCode.PARAMS_ERROR,"参数为空");
        Long appId=deleteRequest.getId();
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "应用不存在");
        User loginUser = userService.getCurrentLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "请先登录");
        Long createUserId = app.getUserId();
        //不是创建者 & 不是管理员 -> 无权限
        if(!createUserId.equals(loginUser.getId()) && !loginUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue())){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限");
        }
        boolean removeFlag = appService.removeById(appId);
        return ResultUtils.success(removeFlag);
    }

    /**
     * 更新应用信息
     * @param appUpdateRequest
     * @param request
     * @return
     */
    @PutMapping("update")
    @Operation(summary = "更新应用信息")
    public BaseResponse<Boolean> update(@RequestBody AppUpdateRequest appUpdateRequest,HttpServletRequest request) {
        log.info("更新应用:{}",appUpdateRequest);
        ThrowUtils.throwIf(appUpdateRequest == null || appUpdateRequest.getAppId() == null , ErrorCode.PARAMS_ERROR,"参数为空");
        Boolean flag=appService.updateApp(appUpdateRequest,request);
        return ResultUtils.success(flag);
    }

    /**
     * 根据 id 获取应用详情
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 id 获取应用详情")
    public BaseResponse<AppVO> getAppVOById(long id) {
        log.info("根据 id 获取应用详情:{}", id);
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类（包含用户信息）
        return ResultUtils.success(appService.getAppVO(app));
    }

    /**
     * 分页获取应用列表
     * @param appQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    @Operation(summary = "分页获取应用列表")
    public BaseResponse<Page<AppVO>> getAppVOListPage(@RequestBody AppQueryRequest appQueryRequest,HttpServletRequest request){
        log.info("分页获取应用列表:{}",appQueryRequest);
        ThrowUtils.throwIf(appQueryRequest ==null,ErrorCode.PARAMS_ERROR,"参数为空");
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize <=0 || pageSize >=20,ErrorCode.PARAMS_ERROR,"分页大小不合法");
        int pageNum= appQueryRequest.getPageNum();
        User loginUser = userService.getCurrentLoginUser(request);
        appQueryRequest.setUserId(loginUser.getId());
        QueryWrapper appQueryWrapper = appService.getAppQueryWrapper(appQueryRequest);
        Page<App> page=new Page<>(pageNum,pageSize);
        Page<App> resPage = appService.page(page, appQueryWrapper);
        Page<AppVO> ansPage=new Page<>(pageNum,pageSize,resPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(resPage.getRecords());
        ansPage.setRecords(appVOList);
        return ResultUtils.success(ansPage);
    }

    /**
     * 分页获取精品应用列表
     * @param appQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/good/list/page/vo")
    @Operation(summary = "分页获取精品应用列表")
    public BaseResponse<Page<AppVO>> getGoodAppVOListPage(@RequestBody AppQueryRequest appQueryRequest,HttpServletRequest request){
        log.info("分页获取精品应用列表:{}",appQueryRequest);
        ThrowUtils.throwIf(appQueryRequest ==null,ErrorCode.PARAMS_ERROR,"参数为空");
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize <=0 || pageSize >=20,ErrorCode.PARAMS_ERROR,"分页大小不合法");
        int pageNum= appQueryRequest.getPageNum();
        appQueryRequest.setPriority(AppConstant.GOOD_APP_PRIORITY);
        QueryWrapper appQueryWrapper = appService.getAppQueryWrapper(appQueryRequest);
        Page<App> resPage = appService.page(new Page<>(pageNum, pageSize), appQueryWrapper);
        Page<AppVO> ansPage=new Page<>(pageNum,pageSize,resPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(resPage.getRecords());
        ansPage.setRecords(appVOList);
        return ResultUtils.success(ansPage);
    }

    @PostMapping("/admin/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(summary = "管理员删除应用")
    public BaseResponse<Boolean> adminDelete(@RequestBody DeleteRequest deleteRequest,HttpServletRequest request) {
        log.info("管理员删除应用:{}",deleteRequest);
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null , ErrorCode.PARAMS_ERROR,"参数为空");
        Long appId=deleteRequest.getId();
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "应用不存在");
        boolean removeFlag = appService.removeById(appId);
        return ResultUtils.success(removeFlag);
    }

    @PostMapping("/admin/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(summary = "管理员更新应用信息")
    public BaseResponse<Boolean> adminUpdate(@RequestBody AppAdminUpdateRequest appAdminUpdateRequest,HttpServletRequest request){
        log.info("管理员更新应用信息:{}",appAdminUpdateRequest);
        ThrowUtils.throwIf(appAdminUpdateRequest == null || appAdminUpdateRequest.getId() == null , ErrorCode.PARAMS_ERROR,"参数为空");
        Long appId=appAdminUpdateRequest.getId();
        App oldApp = appService.getById(appId);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.PARAMS_ERROR, "应用不存在");
        App app=new App();
        BeanUtils.copyProperties(appAdminUpdateRequest,app);
        app.setEditTime(LocalDateTime.now());
        boolean res=appService.updateById(app);
        ThrowUtils.throwIf(!res,ErrorCode.SYSTEM_ERROR,"更新失败");
        return ResultUtils.success(res);
    }

    @PostMapping("/admin/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(summary = "管理员分页获取应用列表")
    public BaseResponse<Page<AppVO>> adminGetAppVOListPage(@RequestBody AppQueryRequest appQueryRequest, HttpServletRequest request){
        log.info("管理员分页获取应用列表:{}",appQueryRequest);
        ThrowUtils.throwIf(appQueryRequest ==null,ErrorCode.PARAMS_ERROR,"参数为空");
        long pageSize= appQueryRequest.getPageSize();
        long pageNum =appQueryRequest.getPageNum();
        QueryWrapper appQueryWrapper = appService.getAppQueryWrapper(appQueryRequest);
        Page<App> resPage=appService.page(new Page<>(pageSize,pageNum),appQueryWrapper);
        Page<AppVO> ansPage=new Page<>(pageSize,pageNum,resPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(resPage.getRecords());
        ansPage.setRecords(appVOList);
        return ResultUtils.success(ansPage);
    }

    /**
     * 管理员根据 id 获取应用详情
     * @param id 应用 id
     * @return 应用详情
     */
    @GetMapping("/admin/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(summary = "管理员根据 id 获取应用详情")
    public BaseResponse<AppVO> getAppVOByIdByAdmin(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(appService.getAppVO(app));
    }

    @GetMapping(value = "/chat/gen/code",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "应用聊天生成代码")
    public Flux<ServerSentEvent<String>> chatToGenCode(@RequestParam("appId") Long appId,@RequestParam("message") String message,HttpServletRequest request){
        log.info("应用聊天生成代码:{},{}",appId,message);
        ThrowUtils.throwIf(appId == null || appId <0 ,ErrorCode.PARAMS_ERROR,"应用id不合法");
        ThrowUtils.throwIf(StrUtil.isBlank(message),ErrorCode.PARAMS_ERROR,"消息不合法");
        //获取当前登录用户
        User loginUser = userService.getCurrentLoginUser(request);
        Flux<String> codeStream= appService.chatToGenCode(appId, message, loginUser);
        return codeStream.map(chunk -> {
            Map<String,String> map=Map.of("v",chunk);
            String jsonStr = JSONUtil.toJsonStr(map);
            return ServerSentEvent.<String>builder().data(jsonStr).build();
            //当前面所有的流数据都发送完后，再发送一个don，只包含单个元素的响应式流。
        }).concatWith(Mono.just(
                ServerSentEvent.<String>builder()
                        .event("done")
                        .data("")
                        .build()
        ));
    }

}
