package com.getian.getaicodemother.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.exception.ThrowUtils;
import com.getian.getaicodemother.manager.CosManager;
import com.getian.getaicodemother.service.ScreenshotService;
import com.getian.getaicodemother.utils.WebScreenShotUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class ScreenshotServiceImpl implements ScreenshotService {
    /**
     * 腾讯云cos存储桶 client
     */
    @Resource
    private CosManager cosManager;



    @Override
    public String generateAndUploadScreenshot(String url) {
        //1.校验参数
        if (StrUtil.isBlank(url)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "url不能为空");
        }
        log.info("开始生成网页截图，url:{}",url);
        //2.生成截图
        String filePath = WebScreenShotUtils.saveWebPageScreenshot(url);
        ThrowUtils.throwIf(StrUtil.isBlank(filePath), ErrorCode.SYSTEM_ERROR, "网页截图生成失败");
        try {
            //3.判断文件是否存在
            File file = new File(filePath);
            if (!file.exists()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "截图文件不存在");
            }
            String fileName = UUID.randomUUID().toString().substring(0,8)+"_compressed.jpg";
            //4.生成cosKey,上传到对象存储
            String cosKey = generateCosKey(fileName);
            String savedFilePath = cosManager.uploadFile(cosKey, file);
            System.out.println("存储的文件地址为: "+savedFilePath);
            return savedFilePath;
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        } finally {
            cleanupLocalFile(filePath);
        }
    }

    private String generateCosKey(String fileName) {
        String dataPath= LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return StrUtil.format("/screenshots/{}/{}",dataPath,fileName);
    }

    private void cleanupLocalFile(String localFilePath){
        File localFile=new File(localFilePath);
        if(localFile.exists()){
            File parentDir=localFile.getParentFile();
            FileUtil.del(parentDir);
            log.info("清理本地截图文件成功，路径：{}",parentDir.getAbsolutePath());
        }
    }
}
