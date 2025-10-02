package com.getian.getaicodemother.core.parser;

import com.getian.getaicodemother.ai.model.HtmlCodeResult;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlCodeParser implements CodeParser<HtmlCodeResult>{
    private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    @Override
    public HtmlCodeResult parseCode(String code) {
        Matcher matcher = HTML_CODE_PATTERN.matcher(code);
        HtmlCodeResult result=new HtmlCodeResult();
        String htmlCode=null;
        if(matcher.find()){
            htmlCode=matcher.group(1);
        }
        if(htmlCode == null){
            result.setHtmlCode(htmlCode.trim());
        }else{
            result.setHtmlCode(htmlCode);
        }
        return result;
    }
}
