package com.getian.getaicodemother.service.impl;

import cn.hutool.core.stream.StreamUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.exception.ThrowUtils;
import com.getian.getaicodemother.service.ProjectDownloadService;
import com.jfinal.template.expr.ast.Field;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;

@Service
@Slf4j
public class ProjectDownloadServiceImpl implements ProjectDownloadService {

    /**
     * 需要过滤的文件和目录名称
     */
    private static final Set<String> IGNORED_NAMES = Set.of(
            "node_modules",
            ".git",
            "dist",
            "build",
            ".DS_Store",
            ".env",
            "target",
            ".mvn",
            ".idea",
            ".vscode"
    );
    /**
     * 需要过滤的文件扩展名
     */
    private static final Set<String> IGNORED_EXTENSIONS = Set.of(
            ".log",
            ".tmp",
            ".cache"
    );

    /**
     *
     * @param projectRoot 目录路径 eg:C:\java-project\get-ai-code-mother\tmp\code_output\vue_project_333733011039666176
     * @param fullPath 文件完整路径 eg:C:\java-project\get-ai-code-mother\tmp\code_output\vue_project_333733011039666176\index.html
     * @return fullPath是否允许被下载
     */
    private boolean isPathAllowed(Path projectRoot,Path fullPath){
        //获取相对路径
        Path relativize = projectRoot.relativize(fullPath);
        //检查路径中的每一部分 对relativize进行遍历，a/b/c/d  遍历的每个结果就是 a b c d
        for(Path path:relativize){
            String pathName = path.toString();
            //如果路径名称在忽略列表中，则返回false
            if(IGNORED_NAMES.contains(pathName)){
                return false;
            }
            //检查文件扩展名
            if(IGNORED_EXTENSIONS.stream().anyMatch(pathName::endsWith)){
                return false;
            }
        }
        return true;
    }

    /**
     * 下载项目文件作为zip压缩包
     * @param projectPath
     * @param downloadFileName
     * @param response
     */
    @Override
    public void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response) {
        //基础校验
        ThrowUtils.throwIf(StrUtil.hasBlank(projectPath,downloadFileName), ErrorCode.PARAMS_ERROR,"项目路径和下载文件名不能为空");
        File projectDir =new File(projectPath);
        ThrowUtils.throwIf(!projectDir.exists() || !projectDir.isDirectory(),ErrorCode.PARAMS_ERROR,"项目目录不存在");
        log.info("开始打包下载项目,项目路径:{} -> {}.zip",projectPath,downloadFileName);
        //设置HTTP响应头
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip"); //用来告诉客户端，返回的内容是zip压缩文件
        response.addHeader("Content-Disposition",String.format("attachment;filename=\"%s.zip\"",downloadFileName));//将相应内容作为附件下载
        //定义文件过滤器
        try {
            FileFilter fileFilter =file -> isPathAllowed(projectDir.toPath(),file.toPath());
            //ZipUtil不会打包所有的文件，而是会根据fileFilter过滤规则对文件过滤！
            ZipUtil.zip(response.getOutputStream(), StandardCharsets.UTF_8,false, fileFilter, projectDir);
            log.info("打包下载项目完成,项目路径:{} -> {}.zip",projectPath,downloadFileName);
        } catch (IOException e) {
            log.error("项目打包下载异常",e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"项目打包下载异常");
        }
    }
}
