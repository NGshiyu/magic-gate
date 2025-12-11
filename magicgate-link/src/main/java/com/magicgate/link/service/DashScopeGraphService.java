package com.magicgate.link.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * @Author yangyangsheep
 * @Description 灵积百炼对话服务
 * @CreateTime 2025/6/29 13:37
 */
public interface DashScopeGraphService {

    /**
     * 简单agent演示
     *
     * @return {@link String }
     */
    String simpleGraph();


    /**
     * 流式返回
     *
     * @return {@link Flux }<{@link ServerSentEvent }<{@link String }>>
     */
    Flux<ServerSentEvent<String>> simpleGraphFlux();

}
