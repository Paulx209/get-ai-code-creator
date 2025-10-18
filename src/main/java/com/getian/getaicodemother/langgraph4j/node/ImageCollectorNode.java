package com.getian.getaicodemother.langgraph4j.node;

import com.getian.getaicodemother.ai.ImageCollectionService;
import com.getian.getaicodemother.langgraph4j.state.WorkflowContent;
import com.getian.getaicodemother.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.util.List;

/**
 * 图片收集节点，整个流程中第一步
 */
@Slf4j
public class ImageCollectorNode {

    private static final String url1 = "https://www.keaitupian.cn/cjpic/frombd/2/253/1659552792/3869332496.jpg";
    private static final String url2 = "https://www.keaitupian.cn/cjpic/frombd/2/253/1627843256/2520284768.jpg";

    public static AsyncNodeAction<MessagesState<String>> create() {
        return AsyncNodeAction.node_async(state -> {
            //1.第一步，获取参数
            WorkflowContent context = WorkflowContent.getContext(state);
            String originalPrompt = context.getOriginalPrompt();
            log.info("执行节点,图片收集");
            //2.第二步，todo 图片收集逻辑
            List<String> imageStr = null;
            try {
                //调用AI图片收集服务
                ImageCollectionService imageCollectionService = SpringContextUtil.getBean(ImageCollectionService.class);
                imageStr = imageCollectionService.collectImages(originalPrompt);
            } catch (Exception e) {
                log.error("图片收集失败:{}",e.getMessage(),e);
            }
            context.setCurrentStep("图片收集");
            context.setImageListStr(imageStr.toString());
            log.info("图片收集完成，共收集{}张图片", imageStr.size());
            //3.第三步，返回结果
            return WorkflowContent.saveContext(context);
        });
    }

}
