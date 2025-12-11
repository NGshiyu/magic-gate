package com.magicgate.link.utils;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * @Author yangyangSheep
 * @ClassName com.joinf.ai.web.utils.FluxUtils.java
 * @Description 流工具
 * @CreateTime 2025/5/27 11:13
 */
public class FluxUtils {

    /**
     * buildReturn
     * 添加一个Close用于返回完成的判断
     */
    public static Flux<ServerSentEvent<String>> buildReturn(Flux<ServerSentEvent<String>> map) {
        // 添加 ready 和 close 标志
        ServerSentEvent<String> closeEvent = ServerSentEvent.<String>builder().event("close").data("\"Done\"").build();
        // 返回完整流
        return Flux.concat(map, Flux.just(closeEvent));
    }
}
