package com.getian.getaicodemother.core;

import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

@SpringBootTest
class AiCodeGeneratorFacadeTest {
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    private Long appId=331377589884493824L;
    @Test
    void generateAndSaveCode() {
        String message="请帮我生成一个用户登录页面";
        File file = aiCodeGeneratorFacade.generateAndSaveCode(message, CodeGenTypeEnum.HTML,appId);
        System.out.println(file);
    }
    @Test
    void generateAndSaveCode2(){
        String message="请帮我生成一个用户登录页面";
        File file = aiCodeGeneratorFacade.generateAndSaveCode(message, CodeGenTypeEnum.MULTI_FILE,appId);
        System.out.println(file);
    }
    @Test
    void generateAdnSaveCodeStream(){
        String message="请帮我生成一个用户中心页面";
        aiCodeGeneratorFacade.generateAndSaveCodeStream(message,CodeGenTypeEnum.HTML,appId);
    }

    @Test
    void generateAdnSaveCodeStream2(){
        String message="请帮我生成一个用户中心页面，一共不超过100行代码";
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, CodeGenTypeEnum.MULTI_FILE,appId);
        List<String> result = codeStream.collectList().block();
        //验证结果
        Assertions.assertNotNull(result);
    }

    @Test
    void testParseHtmlCode(){
        String message="帮我生成一个宇宙星舰风格的网页，不超过50行代码";
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, CodeGenTypeEnum.MULTI_FILE,appId);
        List<String> result = codeStream.collectList().block();
        //验证结果
        Assertions.assertNotNull(result);
    }

    @Test
    void testVueProjectGenerateCode(){
        String message="帮我生成一个vue项目，内容是一个用户登录页面，代码总量不超过100行";
        Flux<String> stringFlux = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, CodeGenTypeEnum.VUE_PROJECT, appId);
        List<String> result = stringFlux.collectList().block();
        //验证结果
        Assertions.assertNotNull(result);
        String completeContent=String.join("",result);
        System.out.println(completeContent);
        Assertions.assertNotNull(completeContent);
    }
}
