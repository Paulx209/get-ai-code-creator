package com.getian.getaicodemother.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.getian.getaicodemother.annotation.AuthCheck;
import com.getian.getaicodemother.common.BaseResponse;
import com.getian.getaicodemother.common.DeleteRequest;
import com.getian.getaicodemother.common.ResultUtils;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.exception.ThrowUtils;
import com.getian.getaicodemother.model.constant.UserConstant;
import com.getian.getaicodemother.model.dto.user.*;
import com.getian.getaicodemother.model.entity.User;
import com.getian.getaicodemother.model.vo.user.LoginUserVO;
import com.getian.getaicodemother.model.vo.user.UserVO;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.getian.getaicodemother.service.UserService;

import java.util.List;

/**
 * 用户控制层。
 *
 * @author sonicge
 */
@RestController
@RequestMapping("/user")
@Log4j2
@Tag(name = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 新增用户
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("register")
    @Operation(summary = "用户注册")
    public BaseResponse<Long> save(@RequestBody UserRegisterRequest userRegisterRequest,HttpServletRequest request) {
        log.info("用户注册：{}", userRegisterRequest);
        ThrowUtils.throwIf(userRegisterRequest==null, ErrorCode.PARAMS_ERROR,"参数为空");
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long userId = userService.userRegister(userAccount, userPassword, checkPassword,request);
        return ResultUtils.success(userId);
    }

    @PostMapping("login")
    @Operation(summary = "用户登录")
    public BaseResponse<LoginUserVO> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        log.info("用户登录：{}", userLoginRequest);
        ThrowUtils.throwIf(userLoginRequest == null,ErrorCode.PARAMS_ERROR,"参数为空");
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO  loginUserVO=userService.login(userAccount,userPassword,request);
        return ResultUtils.success(loginUserVO);
    }

    @GetMapping("/get/login")
    @Operation(summary = "获取当前登录用户")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request){
        log.info("获取当前登录用户");
        ThrowUtils.throwIf(request==null,ErrorCode.PARAMS_ERROR,"参数为空");
        User user=userService.getCurrentLoginUser(request);
        LoginUserVO loginUserVO = userService.getLoginUserVO(user);
        return ResultUtils.success(loginUserVO);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户退出登录")
    public BaseResponse<Boolean> logout(HttpServletRequest request){
        log.info("用户退出登录");
        ThrowUtils.throwIf(request==null,ErrorCode.PARAMS_ERROR,"参数为空");
        Boolean flag=userService.logout(request);
        return ResultUtils.success(flag);
    }

    @PostMapping("/add")
    @Operation(summary = "管理员添加用户")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserVO> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request){
        log.info("创建用户：{}", userAddRequest);
        ThrowUtils.throwIf(userAddRequest==null, ErrorCode.PARAMS_ERROR,"参数为空");
        User user=new User();
        BeanUtil.copyProperties(userAddRequest,user);
        //默认密码 123456
        final String DEFAULT_PASSWORD="123456";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);

        boolean saveRes = userService.save(user);
        ThrowUtils.throwIf(!saveRes, ErrorCode.OPERATION_ERROR,"创建失败");
        return ResultUtils.success(userService.getUserVO(user));
    }
    @GetMapping("/get")
    @Operation(summary = "根据id获取用户信息")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(Long id){
        log.info("根据id获取用户信息：{}", id);
        ThrowUtils.throwIf(id==null, ErrorCode.PARAMS_ERROR,"参数为空");
        User user = userService.getById(id);
        ThrowUtils.throwIf(user==null, ErrorCode.PARAMS_ERROR,"用户不存在");
        return ResultUtils.success(user);
    }

    @GetMapping("/get/vo")
    @Operation(summary = "根据id获取用户信息VO")
    public BaseResponse<UserVO> getUserVOById(Long id){
        log.info("根据id获取用户信息VO：{}", id);
        ThrowUtils.throwIf(id==null, ErrorCode.PARAMS_ERROR,"参数为空");
        User user = userService.getById(id);
        ThrowUtils.throwIf(user==null, ErrorCode.PARAMS_ERROR,"用户不存在");
        UserVO userVO = userService.getUserVO(user);
        return ResultUtils.success(userVO);
    }

    @PostMapping("/delete")
    @Operation(summary = "根据id删除用户")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserById(@RequestBody DeleteRequest deleteRequest){
        log.info("根据id删除用户：{}", deleteRequest);
        ThrowUtils.throwIf(deleteRequest==null, ErrorCode.PARAMS_ERROR,"参数为空");
        boolean res = userService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR,"删除失败");
        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    @Operation(summary = "更新用户信息")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest){
        log.info("更新用户信息：{}", userUpdateRequest);
        ThrowUtils.throwIf(userUpdateRequest==null || userUpdateRequest.getId()==null, ErrorCode.PARAMS_ERROR,"参数为空");
        User user=new User();
        CopyOptions copyOptions=CopyOptions.create().setIgnoreNullValue(true);
        BeanUtil.copyProperties(userUpdateRequest,user,copyOptions);
        boolean res = userService.updateById(user);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR,"更新失败");
        return ResultUtils.success(true);
    }

    @PostMapping("/list/vo/page")
    @Operation(summary = "分页获取用户列表")
    public BaseResponse<Page<UserVO>> getUserVOPage(@RequestBody UserQueryRequest userQueryRequest){
        log.info("分页获取用户列表：{}", userQueryRequest);
        ThrowUtils.throwIf(userQueryRequest==null, ErrorCode.PARAMS_ERROR,"参数为空");
        int pageNum = userQueryRequest.getPageNum();
        int pageSize = userQueryRequest.getPageSize();
        Page<User> page = userService.page(new Page<>(pageNum, pageSize), userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(page.getPageNumber(),page.getPageSize(),page.getTotalRow());
        List<UserVO> userVOList = userService.getUserVOList(page.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
}
