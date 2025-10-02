package com.getian.getaicodemother.controller;


import cn.hutool.core.util.StrUtil;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.getian.getaicodemother.model.entity.App;
import com.getian.getaicodemother.service.AppService;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

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

    @PostMapping("/admin/upadte")
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

    /**
     * 管理员根据 id 获取应用详情
     * @param id 应用 id
     * @return 应用详情
     */
    @GetMapping("/admin/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<AppVO> getAppVOByIdByAdmin(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(appService.getAppVO(app));
    }



}
