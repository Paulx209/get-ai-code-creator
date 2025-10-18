package com.getian.getaicodemother.langgraph4j.tools;

import com.getian.getaicodemother.langgraph4j.state.ImageResource;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
class ImageSearchToolTest {
    @Resource
    private ImageSearchTool imageSearchTool;

    @Test
    void searchContentImages() {
        String searchKey = "澳门风景照";
        List<ImageResource> imageResourceList = imageSearchTool.searchContentImages(searchKey);
        for(ImageResource imageResource:imageResourceList){
            System.out.println(imageResource.getUrl());
        }
    }
}
