package com.getian.getaicodemother.service;

import com.getian.getaicodemother.model.dto.app.AppAddRequest;
import com.getian.getaicodemother.model.dto.app.AppQueryRequest;
import com.getian.getaicodemother.model.dto.app.AppUpdateRequest;
import com.getian.getaicodemother.model.entity.User;
import com.getian.getaicodemother.model.vo.app.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.getian.getaicodemother.model.entity.App;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author sonicge
 */
public interface AppService extends IService<App> {

    /**
     * 新增应用
     * @param addRequest
     * @param request
     * @return
     */
    Long insertApp(AppAddRequest addRequest, HttpServletRequest request);

    /**
     * 更新应用信息
     * @param appUpdateRequest
     * @param request
     * @return
     */
    Boolean updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request);

    /**
     * 获取包装类
     * @param app
     * @return
     */
    AppVO getAppVO(App app);

    QueryWrapper getAppQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 批量获取AppVO包装类
     * @param appList
     * @return
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 交流 -> 生成代码
     * @param appId
     * @param message
     * @param loginUser
     * @return
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    /**
     * 部署应用
     * @param appId
     * @param loginUser
     * @return
     */
    String deployApp(Long appId,User loginUser);
}
