package com.getian.getaicodemother.langgraph4j.node;

import com.getian.getaicodemother.langgraph4j.state.ImageCategoryEnum;
import com.getian.getaicodemother.langgraph4j.state.ImageResource;
import com.getian.getaicodemother.langgraph4j.state.WorkflowContent;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class ImageControllerNode {

    private static final String url1="https://www.keaitupian.cn/cjpic/frombd/2/253/1659552792/3869332496.jpg";
    private static final String url2="https://www.keaitupian.cn/cjpic/frombd/2/253/1627843256/2520284768.jpg";

    public static AsyncNodeAction<MessagesState<String>> create(){
        return AsyncNodeAction.node_async(state ->  {
            //1.第一步，获取参数
            WorkflowContent context = WorkflowContent.getContext(state);
            log.info("执行节点,图片收集");
            //2.第二步，todo 图片收集逻辑
            //简单的假数据
            List<ImageResource> imageResourceList = Arrays.asList(
                    ImageResource.builder()
                            .category(ImageCategoryEnum.CONTENT)
                            .description("假数据图片1")
                            .url(url1)
                            .build(),
                    ImageResource.builder()
                            .category(ImageCategoryEnum.LOGO)
                            .description("假数据图片2")
                            .url(url2)
                            .build()
            );
            context.setCurrentStep("图片收集");
            context.setImageList(imageResourceList);
            context.setImageListStr(imageResourceList.toString());
            log.info("图片收集完成，共收集{}张图片",imageResourceList.size());
            //3.第三步，返回结果
            return WorkflowContent.saveContext(context);
        });
    }
}
