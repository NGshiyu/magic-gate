package com.magicgate.link.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author yangyangsheep
 * @Description 大模型调用异常类
 * @CreateTime 2025/6/30 22:40
 */
@Getter
@NoArgsConstructor
public class LLMChatException extends RuntimeException {


    public LLMChatException(String message) {
        super("LLM Chat Execute Error: " + message);
    }

    public LLMChatException(Throwable cause) {
        super(cause);
    }

    public LLMChatException(String message, Throwable cause) {
        super("LLM Chat Execute Error: " + message, cause);
    }
}
