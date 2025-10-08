package com.getian.getaicodemother.core;

import com.getian.getaicodemother.ai.AiCodeGeneratorService;
import com.getian.getaicodemother.ai.AiCodeGeneratorServiceFactory;
import com.getian.getaicodemother.ai.model.HtmlCodeResult;
import com.getian.getaicodemother.ai.model.MultiFileCodeResult;
import com.getian.getaicodemother.core.parser.CodeParserExecutor;
import com.getian.getaicodemother.core.saver.CodeFileSaverExecutor;
import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

//门面类
@Service
@Slf4j
public class AiCodeGeneratorFacade {
    @Resource
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenType,Long appId) {
        if (codeGenType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型为空");
        }
        AiCodeGeneratorService aiCodeGeneratorService=aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId,codeGenType);
        return switch (codeGenType) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generatorHtmlCode(userMessage);
                yield  CodeFileSaverExecutor.executeFileSave(htmlCodeResult, CodeGenTypeEnum.HTML,appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generatorMultiFileCode(userMessage);
                yield  CodeFileSaverExecutor.executeFileSave(multiFileCodeResult, CodeGenTypeEnum.MULTI_FILE,appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型" + codeGenType.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 根据codeGenType调用不同的代码逻辑
     * @param userMessage
     * @param codeGenType
     * @return
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenType,Long appId) {
        if(codeGenType == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"生成类型为空");
        }
        AiCodeGeneratorService aiCodeGeneratorService=aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId,codeGenType);
        return switch (codeGenType){
            case HTML ->  {
                //先生成代码流
                Flux<String> stringFlux = aiCodeGeneratorService.generatorHtmlCodeStream(userMessage);
                //yield关键字是用于增强switch表达式的，用于从分支返回值的关键字！
                yield  processCodeStream(stringFlux,codeGenType,appId);
            }
            case MULTI_FILE -> {
                Flux<String> stringFlux = aiCodeGeneratorService.generatorMultiFileCodeStream(userMessage);
                yield processCodeStream(stringFlux,codeGenType,appId);
            }
            case VUE_PROJECT -> {
                Flux<String> stringFlux = aiCodeGeneratorService.generateVueProjectCodeStream(appId, userMessage);
                yield processCodeStream(stringFlux,codeGenType,appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型" + codeGenType.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 统一流式生成单HTMl代码 / Multi File代码 文件
     * @param codeStream
     * @param codeGenTypeEnum
     * @return
     */
    public Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenTypeEnum,Long appId){
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream.doOnNext(chunk -> {
            codeBuilder.append(chunk);
        }).doOnComplete(()->{
            try {
                String codeStr=codeBuilder.toString();
                Object parserRes = CodeParserExecutor.executorCodeParser(codeStr, codeGenTypeEnum);
                File saveDir = CodeFileSaverExecutor.executeFileSave(parserRes, codeGenTypeEnum,appId);
                log.info("生成代码文件成功，路径为:{}", saveDir.getAbsolutePath());
            } catch (Exception e) {
                log.info("生成代码文件失败，{}", e.getMessage());
            }
        });
    }

//
//    /**
//     * 流式生成单HTML代码文件
//     * @param userMessage
//     * @return
//     */
//    @Deprecated
//    public Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
//        Flux<String> resFlux = aiCodeGeneratorService.generatorHtmlCodeStream(userMessage);
//        StringBuilder codeBuilder = new StringBuilder();
//        return resFlux.doOnNext(chunk -> {
//            codeBuilder.append(chunk);
//        }).doOnComplete(() -> {
//            try {
//                //流式输出完成
//                String resString = codeBuilder.toString();
//                //开始解析
//                HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(resString);
//                File saveDir = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
//                log.info("生成代码文件成功，路径为:{}", saveDir.getAbsolutePath());
//            } catch (Exception e) {
//                log.info("生成代码文件失败，{}", e.getMessage());
//            }
//        });
//    }
//
//    /**
//     * 流式生成多代码文件
//     * @param userMessage
//     * @return
//     */
//    @Deprecated
//    public Flux<String> generateAndSaveMultiFileCodeStream(String userMessage){
//        Flux<String> stringFlux = aiCodeGeneratorService.generatorMultiFileCodeStream(userMessage);
//        StringBuilder codeBuilder = new StringBuilder();
//        return stringFlux.doOnNext(chunk -> {
//            codeBuilder.append(chunk);
//        }).doOnComplete(()->{
//            try {
//                String resString = codeBuilder.toString();
//                MultiFileCodeResult multiFileCodeResult = CodeParser.parseMultiFileCode(resString);
//                File file = CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
//                log.info("生成代码文件成功，路径为:{}", file.getAbsolutePath());
//            } catch (Exception e) {
//                log.info("生成代码文件失败，{}", e.getMessage());
//            }
//        });
//    }
//
//    private File generateAndSaveHtmlCode(String userMessage) {
//        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generatorHtmlCode(userMessage);
//        File file = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
//        return file;
//    }
//
//    private File generateAndSaveMultiFileCode(String userMessage) {
//        MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generatorMultiFileCode(userMessage);
//        File file = CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
//        return file;
//    }


}
