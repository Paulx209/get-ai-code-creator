package com.getian.getaicodemother.langgraph4j.tools;

import com.getian.getaicodemother.langgraph4j.state.ImageResource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UndrawIllustrationToolTest {

    @Test
    void searchIllustrations() {
        UndrawIllustrationTool tool=new UndrawIllustrationTool();
        List<ImageResource> imageResourceList = tool.searchIllustrations("rain");
        for(ImageResource imageResource:imageResourceList){
            System.out.println(imageResource.getUrl());
        }
    }
}
