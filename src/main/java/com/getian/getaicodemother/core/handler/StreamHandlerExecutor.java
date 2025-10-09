package com.getian.getaicodemother.core.handler;

import com.getian.getaicodemother.model.entity.User;
import com.getian.getaicodemother.model.enums.CodeGenTypeEnum;
import com.getian.getaicodemother.service.ChatHistoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class StreamHandlerExecutor {
    @Resource
    private JsonMessageStreamHandler jsonMessageStreamHandler;

    public Flux<String> doExecute(Flux<String> originFlux, Long appId, User loginUser, ChatHistoryService chatHistoryService, CodeGenTypeEnum codeGenTypeEnum){
        return switch(codeGenTypeEnum){
            case HTML, MULTI_FILE ->
                new SimpleTextStreamHandler().handleJsonMessage(originFlux,chatHistoryService,appId,loginUser);
            case VUE_PROJECT ->
                jsonMessageStreamHandler.handleJsonMessage(originFlux,chatHistoryService,appId,loginUser);
        };
    }
}
