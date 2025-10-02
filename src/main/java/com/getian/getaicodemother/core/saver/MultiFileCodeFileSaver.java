package com.getian.getaicodemother.core.saver;

import com.getian.getaicodemother.ai.model.MultiFileCodeResult;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;

public class MultiFileCodeFileSaver extends CodeFileSaverTemplate<MultiFileCodeResult>{
    @Override
    protected void validateInput(MultiFileCodeResult result) {
        super.validateInput(result);
        //TODO:可以额外补充!
    }

    @Override
    protected void saveFile(String basicFilePath, MultiFileCodeResult result) {
        writeToFile(basicFilePath,"index.html", result.getHtmlCode());
        writeToFile(basicFilePath,"script.js",result.getJsCode());
        writeToFile(basicFilePath,"style.css",result.getCssCode());
    }

    @Override
    protected CodeGenTypeEnum getBizType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }
}
