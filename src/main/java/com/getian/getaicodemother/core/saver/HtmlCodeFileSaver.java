package com.getian.getaicodemother.core.saver;

import cn.hutool.core.util.StrUtil;
import com.getian.getaicodemother.ai.model.HtmlCodeResult;
import com.getian.getaicodemother.exception.BusinessException;
import com.getian.getaicodemother.exception.ErrorCode;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;


public class HtmlCodeFileSaver extends CodeFileSaverTemplate<HtmlCodeResult>{
    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        //TODO:可以额外补充!
        if(StrUtil.isEmpty(result.getHtmlCode())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"生成内容为空");
        }
    }

    @Override
    protected void saveFile(String basicFilePath, HtmlCodeResult result) {
        writeToFile(basicFilePath,"index.html", result.getHtmlCode());
    }

    @Override
    protected CodeGenTypeEnum getBizType() {
        return CodeGenTypeEnum.HTML;
    }
}
