package com.magicgate.link.service;

import com.magicgate.common.request.DialogueRequest;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * @Author yangyangsheep
 * @Description 灵积百炼对话服务
 * @CreateTime 2025/6/29 13:37
 */
public interface DashScopeChatService {

    /**
     * 单论对话获取结果信息
     *
     * @param request 对话参数
     *
     * @return {@link String }
     */
    String singleAnswer(DialogueRequest request);


    /**
     * 多轮流式对话接口
     *
     * @param request 对话参数
     *
     * @return {@link Flux }
     */
    Flux<ServerSentEvent<String>> chat(DialogueRequest request);
}
