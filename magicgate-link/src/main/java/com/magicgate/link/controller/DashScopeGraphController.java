package com.magicgate.link.controller;

//import com.alibaba.dashscope.aigc.generation.Generation;
//import com.alibaba.dashscope.aigc.generation.GenerationParam;
//import com.alibaba.dashscope.aigc.generation.GenerationResult;
//import com.alibaba.dashscope.common.Message;
//import com.alibaba.dashscope.common.Role;
//import com.alibaba.dashscope.exception.ApiException;
//import com.alibaba.dashscope.exception.InputRequiredException;
//import com.alibaba.dashscope.exception.NoApiKeyException;

import com.magicgate.link.service.DashScopeGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @Author yangyangsheep
 * @Description 百炼对话Controller
 * @CreateTime 2025/6/29 13:37
 */
@RestController
@RequestMapping("/graph")
public class DashScopeGraphController {

    @Autowired
    DashScopeGraphService dashScopeGraphService;


    /**
     * 一次性返回简单演示
     *
     * @return {@link ResponseEntity }<{@link String }>
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> chat() {
        return ResponseEntity.ok(dashScopeGraphService.simpleGraph());
    }

    /**
     * 一次性返回简单演示
     *
     * @return {@link ResponseEntity }<{@link String }>
     */
    @PostMapping(path = "/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> flux() {
        return dashScopeGraphService.simpleGraphFlux();
    }

}
