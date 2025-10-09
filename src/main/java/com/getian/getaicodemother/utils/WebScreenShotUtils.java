package com.getian.getaicodemother.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.UUID;

@Slf4j
public class WebScreenShotUtils {

    private static final WebDriver webDriver;

    static {
        final int DEFAULT_WIDTH = 1600;
        final int DEFAULT_HEIGHT = 900;
        webDriver = initChromeDriver(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @PreDestroy
    public void destroy() {
        webDriver.quit();
    }

    public static String saveWebPageScreenshot(String webUrl){
        if(StrUtil.isBlank(webUrl)){
            log.error("网页url不能为空");
            return null;
        }
        try{
            //创建临时目录 get-ai-code-mother/tmp/screenshots/uuid
            String rootPath=System.getProperty("user.dir")+File.separator+"tmp"+File.separator+"screenshots"
                    +File.separator + UUID.randomUUID().toString().substring(0,8);
            FileUtil.mkdir(rootPath);
            //图片后缀
            final String IMAGE_SUFFIX=".png";
            //原始截图文件路径  get-ai-code-mother/tmp/screenshots/uuid/xxxxx.png
            String imageOriginPath=rootPath+File.separator+ RandomUtil.randomNumbers(5)+IMAGE_SUFFIX;
            //访问网页
            webDriver.get(webUrl);
            //等待页面加载完成
            waitForPageLoad(webDriver);
            //截图
            byte[] screenshotAsBytes = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
            saveImage(screenshotAsBytes,imageOriginPath);
            log.info("原始图片保存成功，filePath:{}",imageOriginPath);
            //压缩图片
            final String COMPRESS_SUFFIX = "_compressed.jpg";
            String imageCompressPath=rootPath+File.separator+ RandomUtil.randomNumbers(5)+COMPRESS_SUFFIX;
            compressImage(imageOriginPath,imageCompressPath);
            log.info("压缩图片保存成功，filePath:{}",imageCompressPath);
            return imageCompressPath;
        }catch (Exception e){
            log.info("截图失败，webUrl:{}",webUrl);
            return null;
        }
    }

    /**
     * 初始化 Chrome 浏览器驱动
     */
    private static WebDriver initChromeDriver(int width, int height) {
        try {
            // 自动管理 ChromeDriver
            WebDriverManager.chromedriver().setup();
            // 配置 Chrome 选项
            ChromeOptions options = new ChromeOptions();
            // 无头模式
            options.addArguments("--headless");
            // 禁用GPU（在某些环境下避免问题）
            options.addArguments("--disable-gpu");
            // 禁用沙盒模式（Docker环境需要）
            options.addArguments("--no-sandbox");
            // 禁用开发者shm使用
            options.addArguments("--disable-dev-shm-usage");
            // 设置窗口大小
            options.addArguments(String.format("--window-size=%d,%d", width, height));
            // 禁用扩展
            options.addArguments("--disable-extensions");
            // 设置用户代理
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            // 创建驱动
            WebDriver driver = new ChromeDriver(options);
            // 设置页面加载超时
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
            // 设置隐式等待
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
            return driver;
        } catch (Exception e) {
            log.error("初始化 Chrome 浏览器失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化 Chrome 浏览器失败");
        }
    }

    /**
     * 写入文件流内容到指定路径中
     * @param imageBytes
     * @param filaPath
     */
    private static void saveImage(byte[] imageBytes,String filaPath){
        try {
            FileUtil.writeBytes(imageBytes,filaPath);
        } catch (IORuntimeException e) {
            log.info("保存图片失败，filePath:{}",filaPath);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"写入图片失败");
        }
    }

    /**
     * 压缩图片
     * @param originImagePath
     * @param targetImagePath
     */
    private static void compressImage(String originImagePath,String targetImagePath){
        final float quality=0.3f;
        try {
            ImgUtil.compress(
                    FileUtil.file(originImagePath),
                    FileUtil.file(targetImagePath),
                    quality);
        } catch (IORuntimeException e) {
            log.info("压缩图片失败，originImagePath:{},targetImagePath:{}",originImagePath,targetImagePath);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"压缩图片失败");
        }
    }


    /**
     * 等待页面加载完成
     */
    private static void waitForPageLoad(WebDriver driver) {
        try {
            // 创建等待页面加载对象
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            // 等待 document.readyState 为complete
            wait.until(webDriver ->
                    ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
                            .equals("complete")
            );
            // 额外等待一段时间，确保动态内容加载完成
            Thread.sleep(2000);
            log.info("页面加载完成");
        } catch (Exception e) {
            log.error("等待页面加载时出现异常，继续执行截图", e);
        }
    }
}
