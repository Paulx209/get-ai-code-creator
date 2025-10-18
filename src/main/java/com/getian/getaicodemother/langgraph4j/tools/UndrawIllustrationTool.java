package com.getian.getaicodemother.langgraph4j.tools;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.getian.getaicodemother.langgraph4j.state.ImageCategoryEnum;
import com.getian.getaicodemother.langgraph4j.state.ImageResource;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索插图图片工具 同样是调用api
 */
@Slf4j
@Component
public class UndrawIllustrationTool {
    private static final String UNDRAW_API_URL = "https://undraw.co/_next/data/9fXhtueLm0tySnSMDaVOx/search/%s.json?term=%s";

    @Tool("搜索插画图片，用于网站美化和展示")
    public List<ImageResource> searchIllustrations(@P("搜索关键词") String query) {
        List<ImageResource> imageList = new ArrayList<>();
        int searchCount = 12;
        String apiUrl = String.format(UNDRAW_API_URL, query, query);
        //调用api
        try (HttpResponse response = HttpRequest.get(apiUrl)
                .timeout(10000).execute()) {
            //1.如果返回值为空的话，直接return
            if (!response.isOk()) {
                return imageList;
            }
            //2.解析body，获取url属性，拼接到集合
            JSONObject body = JSONUtil.parseObj(response.body());
            JSONObject pageProps = body.getJSONObject("pageProps");
            if (pageProps == null) {
                return imageList;
            }
            JSONArray initialResults = pageProps.getJSONArray("initialResults");
            if (initialResults == null || initialResults.size() == 0) {
                return imageList;
            }
            int size = Math.min(initialResults.size(), searchCount);
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject = initialResults.getJSONObject(i);
                String title = jsonObject.getStr("title", "");
                String illustrationUrl = jsonObject.getStr("media", "");
                ImageResource imageResource = ImageResource.builder()
                        .url(illustrationUrl)
                        .category(ImageCategoryEnum.ILLUSTRATION)
                        .description(title)
                        .build();
                imageList.add(imageResource);
            }
        }catch (Exception e){
            log.error("搜索插画失败:{}",e.getMessage(),e);
        }
        return imageList;
    }
}
