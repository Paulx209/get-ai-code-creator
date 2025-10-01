package com.getian.getaicodemother.service;

import com.getian.getaicodemother.model.dto.user.UserQueryRequest;
import com.getian.getaicodemother.model.vo.user.LoginUserVO;
import com.getian.getaicodemother.model.vo.user.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.getian.getaicodemother.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author sonicge
 */
public interface UserService extends IService<User> {
    long userRegister(String userAccount, String password, String checkPassword, HttpServletRequest request);

    String getEncryptPassword(String password);

    LoginUserVO login(String userAccount, String userPassword, HttpServletRequest request);

    LoginUserVO getLoginUserVO(User user);


    User getCurrentLoginUser(HttpServletRequest request);

    Boolean logout(HttpServletRequest request);

    UserVO getUserVO(User user);

    List<UserVO> getUserVOList(List<User> userList);

    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);
}
