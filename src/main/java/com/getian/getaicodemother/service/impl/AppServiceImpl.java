package com.getian.getaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.exception.ThrowUtils;
import com.getian.getaicodemother.model.constant.UserConstant;
import com.getian.getaicodemother.model.dto.app.AppAddRequest;
import com.getian.getaicodemother.model.dto.app.AppQueryRequest;
import com.getian.getaicodemother.model.dto.app.AppUpdateRequest;
import com.getian.getaicodemother.model.entity.User;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;
import com.getian.getaicodemother.model.vo.app.AppVO;
import com.getian.getaicodemother.model.vo.user.UserVO;
import com.getian.getaicodemother.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.getian.getaicodemother.model.entity.App;
import com.getian.getaicodemother.mapper.AppMapper;
import com.getian.getaicodemother.service.AppService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author sonicge
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{
    @Resource
    private UserService userService;
    /**
     * 新增应用
     * @param addRequest
     * @param request
     * @return
     */
    @Override
    public Long insertApp(AppAddRequest addRequest, HttpServletRequest request) {
        User loginUser=(User)request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(loginUser==null, ErrorCode.NOT_LOGIN_ERROR, "请先登录");
        App app=new App();
        app.setInitPrompt(addRequest.getPrompt());
        app.setUserId(loginUser.getId());
        app.setAppName(app.getInitPrompt().substring(0,Math.min(app.getInitPrompt().length(),10)));
        app.setCodeGenType(CodeGenTypeEnum.MULTI_FILE.getValue());

        boolean saveApp = save(app);
        ThrowUtils.throwIf(!saveApp, ErrorCode.SYSTEM_ERROR, "新增应用失败");
        return app.getId();
    }

    /**
     * 更新应用信息
     * @param appUpdateRequest
     * @param request
     * @return
     */
    @Override
    public Boolean updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request) {
        User loginUser = userService.getCurrentLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "请先登录");
        App app = getById(appUpdateRequest.getAppId());
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "应用不存在");
        Long createUserId = app.getUserId();
        ThrowUtils.throwIf(!createUserId.equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限");
        App newApp=new App();
        newApp.setId(app.getId());
        newApp.setAppName(appUpdateRequest.getAppName());
        newApp.setUpdateTime(LocalDateTime.now());
        boolean flag = updateById(newApp);
        ThrowUtils.throwIf(!flag, ErrorCode.SYSTEM_ERROR, "更新应用失败");
        return flag;
    }

    /**
     * 获取appVO
     * @param app
     * @return
     */
    @Override
    public AppVO getAppVO(App app) {
        if(app ==null ){
            return null;
        }
        AppVO appVO=new AppVO();
        BeanUtil.copyProperties(app,appVO);
        //app属性可能不全，需要查询完整信息
        Long appId = app.getId();
        App entireApp = getById(appId);
        Long userId = entireApp.getUserId();
        if(userId !=null){
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    /**
     * 获取appQueryWrapper
     * @param appQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getAppQueryWrapper(AppQueryRequest appQueryRequest) {
        if(appQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();

        return QueryWrapper.create()
                .eq("id",id)
                .like("appName",appName)
                .like("cover",cover)
                .like("initPrompt",initPrompt)
                .eq("codeGenType",codeGenType)
                .eq("deployKey",deployKey)
                .eq("priority",priority)
                .eq("userId",userId)
                .orderBy(sortField, "ascend".equals(sortOrder));

    }

    /**
     * 获取appVOList  AppVO中需要传递UserVO
     * @param appList
     * @return
     */
    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if(CollUtil.isEmpty(appList)){
            return new ArrayList<>();
        }
        //1.获取appList的所有userId
        List<Long> userIds = appList.stream().map(App::getUserId).collect(Collectors.toList());
        //2.根据所有userId获取userVO,以Map<Long,UserVO>的形式存储
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, userService::getUserVO));
        //3.遍历每一个App，获取对应的AppVO,然后将对应的userVO赋值。
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO=userVOMap.get(appVO.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }
}
