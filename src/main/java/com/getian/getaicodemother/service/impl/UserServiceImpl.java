package com.getian.getaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.model.constant.UserConstant;
import com.getian.getaicodemother.model.dto.user.UserQueryRequest;
import com.getian.getaicodemother.model.enums.UserRoleEnum;
import com.getian.getaicodemother.model.vo.user.LoginUserVO;
import com.getian.getaicodemother.model.vo.user.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.getian.getaicodemother.model.entity.User;
import com.getian.getaicodemother.mapper.UserMapper;
import com.getian.getaicodemother.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 服务层实现。
 *
 * @author sonicge
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public long userRegister(String userAccount, String password, String checkPassword,HttpServletRequest request) {
        //1.参数校验
        if (StrUtil.hasBlank(userAccount, password, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4 || userAccount.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不符合要求");
        }
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过短");
        }
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        //2.检查账号是否重复
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }
        //3.加密
        String encryptPassword = getEncryptPassword(password);
        //4.插入数据库
        User user = User.builder()
                .userAccount(userAccount)
                .userPassword(encryptPassword)
                .userName("无名")
                .userRole(UserRoleEnum.USER.getValue())
                .build();
        boolean res = this.save(user);
        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        }
        return user.getId();
    }

    public String getEncryptPassword(String password) {
        //定义salt盐值
        final String SALT = "sonicge";
        return DigestUtils.md5DigestAsHex((SALT + password).getBytes());
    }

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @return
     */
    @Override
    public LoginUserVO login(String userAccount, String userPassword, HttpServletRequest request) {
        //1.参数校验
        if(StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length()<4 || userAccount.length()>16){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号错误");
        }
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
        }
        //2.查询账号密码是否正确
        String encryptPassword = getEncryptPassword(userPassword);
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("userAccount",userAccount).eq("userPassword",encryptPassword);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        if (user ==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码错误");
        }
        //3.记录当前用户登录信息
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,user);
        //4.获取LoginUserVO
        LoginUserVO loginUserVO = this.getLoginUserVO(user);
        return loginUserVO;
    }

    /**
     * 获取LoginUserVO
     * @param user
     * @return
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if(user==null){
            return null;
        }
        LoginUserVO loginUserVO=new LoginUserVO();
        BeanUtil.copyProperties(user,loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @Override
    public User getCurrentLoginUser(HttpServletRequest request) {
        //先判断是否登录
        User loginUser = (User)request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(loginUser == null || loginUser.getId() ==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户未登录");
        }
        //从数据库查询
        Long userId=loginUser.getId();
        User user = this.getById(userId);
        if(user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户不存在");
        }
        return user;
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @Override
    public Boolean logout(HttpServletRequest request) {
        User loginUser = (User)request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (loginUser == null || loginUser.getId() ==null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户未登录");
        }
        //移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserVO getUserVO(User user) {
        if(user == null){
            return null;
        }
        UserVO userVO=new UserVO();
        BeanUtil.copyProperties(user,userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if(CollUtil.isEmpty(userList)){
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            return new QueryWrapper();
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        return QueryWrapper.create()
                .eq("id",id)
                .eq("userRole",userRole)
                .like("userName",userName)
                .like("userAccount",userAccount)
                .like("userProfile",userProfile)
                .orderBy(sortField,"ascend".equals(sortOrder));
    }
}
