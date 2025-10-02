package com.getian.getaicodemother.ai;


import com.getian.getaicodemother.ai.model.HtmlCodeResult;
import com.getian.getaicodemother.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class AiCodeGeneratorServiceTest {
    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void generatorHtmlCode() {
        HtmlCodeResult res = aiCodeGeneratorService.generatorHtmlCode("请你给我生成一个留言板，代码不超过50行");
        Assertions.assertNotNull(res);
    }

    @Test
    void generatorMultiFileCode() {
        MultiFileCodeResult res = aiCodeGeneratorService.generatorMultiFileCode("请你给我生成一个留言板，代码不超过50行");
        System.out.println(res.getHtmlCode());
        System.out.println(res.getCssCode());
        Assertions.assertNotNull(res);
    }

}
