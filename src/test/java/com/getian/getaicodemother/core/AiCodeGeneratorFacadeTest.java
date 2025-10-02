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

    @Test
    void generateAndSaveCode() {
        String message="请帮我生成一个用户登录页面";
        File file = aiCodeGeneratorFacade.generateAndSaveCode(message, CodeGenTypeEnum.HTML);
        System.out.println(file);
    }
    @Test
    void generateAndSaveCode2(){
        String message="请帮我生成一个用户登录页面";
        File file = aiCodeGeneratorFacade.generateAndSaveCode(message, CodeGenTypeEnum.MULTI_FILE);
        System.out.println(file);
    }
    @Test
    void generateAdnSaveCodeStream(){
        String message="请帮我生成一个用户中心页面";
        aiCodeGeneratorFacade.generateAndSaveCodeStream(message,CodeGenTypeEnum.HTML);
    }

    @Test
    void generateAdnSaveCodeStream2(){
        String message="请帮我生成一个用户中心页面，一共不超过100行代码";
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, CodeGenTypeEnum.MULTI_FILE);
        List<String> result = codeStream.collectList().block();
        //验证结果
        Assertions.assertNotNull(result);
    }

    @Test
    void testParseHtmlCode(){
        String message="请帮我生成一个相亲网站首页，一共不超过50行代码";
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, CodeGenTypeEnum.MULTI_FILE);
        List<String> result = codeStream.collectList().block();
        //验证结果
        Assertions.assertNotNull(result);
    }
}
