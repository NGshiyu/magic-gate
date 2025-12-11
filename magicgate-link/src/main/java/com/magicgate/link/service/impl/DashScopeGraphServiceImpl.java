package com.magicgate.link.service.impl;

import com.magicgate.link.service.DashScopeGraphService;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @Author yangyangsheep
 * @Description graph service
 * @CreateTime 2025/7/11 17:18
 */
@Service
public class DashScopeGraphServiceImpl implements DashScopeGraphService {
    /**
     * 简单agent演示
     *
     * @return {@link String }
     */
    @Override
    public String simpleGraph() {
        return "";
    }

    /**
     * 流式返回
     *
     * @return {@link Flux }<{@link ServerSentEvent }<{@link String }>>
     */
    @Override
    public Flux<ServerSentEvent<String>> simpleGraphFlux() {
        return null;
    }
}
