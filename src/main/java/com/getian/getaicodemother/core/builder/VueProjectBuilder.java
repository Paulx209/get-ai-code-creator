package com.getian.getaicodemother.core.builder;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class VueProjectBuilder {

    /**
     * 异步构建vue项目
     * @param projectPath
     */
    public void buildVueProjectAsync(String projectPath) {
        Thread.ofVirtual().name("vue-builder-" + System.currentTimeMillis()).start(() -> {
            try {
                buildVueProject(projectPath);
            } catch (Exception e) {
                log.error("构建Vue项目失败: {}", projectPath, e);
            }
        });
    }

    /**
     * 构建vue项目
     *
     * @param projectPath Vue项目路径
     * @return
     */
    public boolean buildVueProject(String projectPath) {
        //判断项目目录是否存在
        File projectDir = new File(projectPath);
        if (!projectDir.exists()) {
            log.error("项目目录不存在: {}", projectPath);
            return false;
        }
        //检查package.json是否存在
        File file = new File(projectDir, "package.json");
        if (!file.exists()) {
            log.error("package.json不存在: {}", projectPath);
            return false;
        }
        log.info("开始构建Vue项目：{}", projectPath);
        //执行npm install
        boolean finished = execNpmInstallCmd(projectDir);
        if (!finished) {
            log.error("npm命令执行失败");
            return false;
        }
        //执行npm build
        finished = execNpmBuild(projectDir);
        if (!finished) {
            log.error("npm build命令执行失败");
            return false;
        }
        //验证 dist 目录是否生成
        File distDir = new File(projectDir, "dist");
        if (!distDir.exists()) {
            log.error("dist目录不存在: {}", distDir.getAbsolutePath());
            return false;
        }
        log.info("Vue项目构建完成: {}", projectPath);
        return true;
    }

    private boolean execNpmInstallCmd(File projectDir) {
        String finalCommand = StrUtil.format("%s install", buildCommand("cmd"));
        boolean finished = executeCommand(projectDir, finalCommand, 120);//2分钟超时
        return finished;
    }

    private boolean execNpmBuild(File projectDir) {
        String finalCommand = StrUtil.format("%s run build", buildCommand("npm"));
        boolean finished = executeCommand(projectDir, finalCommand, 120);//2分钟超时
        return finished;
    }

    /**
     * 执行命令
     *
     * @param workingDir
     * @param command
     * @param timeoutSeconds
     * @return
     */
    private boolean executeCommand(File workingDir, String command, int timeoutSeconds) {
        try {
            log.info("在目录 {} 中执行命令: {}", workingDir.getAbsolutePath(), command);
            Process process = RuntimeUtil.exec(null, workingDir, command.split("\\s+"));
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                log.error("命令执行超时: {}", command);
                process.destroyForcibly();
                return false;
            }
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                log.info("命令执行成功: {}", command);
                return true;
            } else {
                log.error("命令执行失败: {}", command);
                return false;
            }
        } catch (InterruptedException e) {
            log.info("命令执行失败: {}, 错误信息：{}", command, e.getMessage());
            return false;
        }
    }

    private String buildCommand(String command) {
        if (isWindows()) {
            return command + ".cmd";
        }
        return command;
    }

    public boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().equals("windows");
    }
}
