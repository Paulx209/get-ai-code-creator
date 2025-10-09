package com.getian.getaicodemother.manager;

import com.getian.getaicodemother.configuration.CosConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class CosManager {
    @Resource
    private COSClient cosClient;

    @Resource
    private CosConfig cosConfig;

    /**
     * 上传对象
     * @param key 文件的key
     * @param file 文件
     * @return
     */
    private PutObjectResult putObject(String key, File file){
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucket(), key, file);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        return putObjectResult;
    }

    public String uploadFile(String key,File file){
        //上传文件
        PutObjectResult putObjectResult = putObject(key, file);
        if(putObjectResult != null){
            //构建访问Url
            String url=String.format("%s%s",cosConfig.getHost(),key);
            log.info("上传文件成功，url:{}",url);
            return url;
        }else{
            log.error("上传文件失败");
            return null;
        }
    }
}
