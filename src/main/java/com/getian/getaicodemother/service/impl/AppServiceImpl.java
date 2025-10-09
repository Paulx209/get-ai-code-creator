package com.getian.getaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.getian.getaicodemother.core.AiCodeGeneratorFacade;
import com.getian.getaicodemother.core.builder.VueProjectBuilder;
import com.getian.getaicodemother.core.handler.StreamHandlerExecutor;
import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.exception.ThrowUtils;
import com.getian.getaicodemother.model.constant.AppConstant;
import com.getian.getaicodemother.model.constant.UserConstant;
import com.getian.getaicodemother.model.dto.app.AppAddRequest;
import com.getian.getaicodemother.model.dto.app.AppQueryRequest;
import com.getian.getaicodemother.model.dto.app.AppUpdateRequest;
import com.getian.getaicodemother.model.entity.User;
import com.getian.getaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;
import com.getian.getaicodemother.model.vo.app.AppVO;
import com.getian.getaicodemother.model.vo.user.UserVO;
import com.getian.getaicodemother.service.ChatHistoryService;
import com.getian.getaicodemother.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.getian.getaicodemother.model.entity.App;
import com.getian.getaicodemother.mapper.AppMapper;
import com.getian.getaicodemother.service.AppService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author sonicge
 */
@Service
@Log4j2
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{
    @Resource
    private UserService userService;
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;
    @Resource
    private ChatHistoryService chatHistoryService;
    @Resource
    private StreamHandlerExecutor streamHandlerExecutor;
    @Resource
    private VueProjectBuilder vueProjectBuilder;

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

    /**
     * 交流 -> 生成代码
     * @param appId
     * @param message
     * @param loginUser
     * @return
     */
    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        //1.校验参数
        ThrowUtils.throwIf(appId == null,ErrorCode.PARAMS_ERROR,"应用id不能为空");
        ThrowUtils.throwIf(message == null,ErrorCode.PARAMS_ERROR,"消息不能为空");
        //2.校验app是否存在
        App app = getById(appId);
        ThrowUtils.throwIf(app == null || app.getId() < 0,ErrorCode.PARAMS_ERROR,"应用不存在");
        //3.校验是否是创建者
        Long userId = loginUser.getId();
        Long createUserId = app.getUserId();
        ThrowUtils.throwIf(!createUserId.equals(userId),ErrorCode.NO_AUTH_ERROR,"无权限");
        //4.获取应用的代码生成类型
        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getCodeGenTypeEnum(codeGenType);
        ThrowUtils.throwIf(codeGenTypeEnum == null ,ErrorCode.PARAMS_ERROR,"应用类型错误");
        //5.保存用户会话消息
        chatHistoryService.addChatMessage(appId,message, ChatHistoryMessageTypeEnum.USER.getValue(),userId);
        //6.调用门面类，AI大模型生成消息并且返回内容流
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
        //7.调用handlerExecutor类，根据不同类型的代码生成类型，执行不同的处理逻辑！
        return streamHandlerExecutor.doExecute(codeStream,appId,loginUser,chatHistoryService,codeGenTypeEnum);
//        StringBuilder aiSb=new StringBuilder();
//        return codeStream.map(chunk -> {
//            aiSb.append(chunk);
//            return chunk;
//        }).doOnComplete(()->{
//           //7.保存AI回话消息
//            String aiMessage = aiSb.toString();
//            if(StrUtil.isNotEmpty(aiMessage)){
//                chatHistoryService.addChatMessage(appId,aiMessage, ChatHistoryMessageTypeEnum.AI.getValue(),userId);
//            }
//        }).doOnError(error -> {
//            //遇到错误也要保存
//            String errorText="AI大模型生成代码失败，请稍后重试"+error.getMessage();
//            chatHistoryService.addChatMessage(appId,errorText, ChatHistoryMessageTypeEnum.AI.getValue(),userId);
//        });
    }

    /**
     * 部署应用
     * @param appId
     * @param loginUser
     * @return
     */
    @Override
    public String deployApp(Long appId, User loginUser) {
        //1.校验参数
        ThrowUtils.throwIf(appId == null || appId<=0,ErrorCode.PARAMS_ERROR,"应用id不能为空");
        //2.查询应用信息
        App app = getById(appId);
        ThrowUtils.throwIf(app==null,ErrorCode.PARAMS_ERROR,"应用不存在");
        //3.校验是否是创建者
        Long createUserId = app.getUserId();
        ThrowUtils.throwIf(!createUserId.equals(loginUser.getId()),ErrorCode.NO_AUTH_ERROR,"该用户无权限部署");
        //4.检查当前deployKey是否已经存在  如果存在 -> 返回可访问的url
        String deployKey = app.getDeployKey();
        if(StrUtil.isNotEmpty(deployKey) && deployKey.length() ==6){
            log.info("应用已经部署过，deployKey:{}",deployKey);
            return AppConstant.CODE_DEPLOY_HOST + File.separator + deployKey + File.separator;
        }
        deployKey= RandomUtil.randomString(6);
        //5.获取代码生成类型，构建源目录路径
        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getCodeGenTypeEnum(codeGenType);
        ThrowUtils.throwIf(codeGenTypeEnum == null,ErrorCode.PARAMS_ERROR,"应用类型错误");
        String file_suffix=codeGenType+"_"+appId;
        String srcDirPath= AppConstant.CODE_OUTPUT_ROOT_DIR+ File.separator+file_suffix;
        //6.检查源目录路径是否存在
        File sourceDir = new File(srcDirPath);
        ThrowUtils.throwIf(!sourceDir.exists() || !sourceDir.isDirectory(),ErrorCode.SYSTEM_ERROR,"源目录不存在");
        //7.判断是否是vue项目
        if(codeGenTypeEnum == CodeGenTypeEnum.VUE_PROJECT){
            //Vue项目需要构建
            boolean finished = vueProjectBuilder.buildVueProject(srcDirPath);
            ThrowUtils.throwIf(!finished,ErrorCode.SYSTEM_ERROR,"构建Vue项目失败");
            //检查dist目录是否存在
            File distDir=new File(srcDirPath,"dist");
            if(!distDir.exists()){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"vue构建完成，但是dist目录生成失败");
            }
            sourceDir=distDir;
            log.info("Vue项目构建完成，dist目录路径：{}",distDir.getAbsolutePath());
        }

        //7.复制文件到部署目录
        String deployDir=AppConstant.CODE_DEPLOY_ROOT_DIR+File.separator+deployKey;
        try {
            FileUtil.copyContent(sourceDir,new File(deployDir),true);
        } catch (IORuntimeException e) {
            throw new RuntimeException(e);
        }
        //8.更新应用的deployKey 和 部署时间
        App updateApp=new App();
        updateApp.setId(appId);
        updateApp.setDeployedTime(LocalDateTime.now());
        updateApp.setDeployKey(deployKey);
        boolean flag = this.updateById(updateApp);
        ThrowUtils.throwIf(!flag,ErrorCode.SYSTEM_ERROR,"更新应用部署信息失败");
        //9.返回可访问的url
        return AppConstant.CODE_DEPLOY_HOST+"/"+deployKey+"/";
    }

    /**
     * 重写 根据Id删除应用
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        if (id == null) {
            return false;
        }
        //转换为String类型
        Long appId = Long.valueOf(id.toString());
        if (appId <= 0) {
            return false;
        }
        //先删除关联的对话应用
        try {
            chatHistoryService.deleteByAppId(appId);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除应用失败，删除应用对话失败");
        }
        return super.removeById(id);
    }
}
