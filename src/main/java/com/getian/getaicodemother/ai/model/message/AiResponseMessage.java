package com.getian.getaicodemother.ai.model.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * AI响应信息
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AiResponseMessage  extends StreamMessage {

    private String content;

    public AiResponseMessage(String content){
        super(StreamMessageTypeEnum.AI_RESPONSE.getValue());
        this.content=content;
    }
}
