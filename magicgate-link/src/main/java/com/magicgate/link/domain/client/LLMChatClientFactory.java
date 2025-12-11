package com.magicgate.link.domain.client;

import com.magicgate.link.exception.LLMChatException;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @Author yangyangsheep
 * @Description client工厂
 * @CreateTime 2025/6/30 21:57
 */
@Component
public class LLMChatClientFactory {


    private final Collection<AbstractLLMChatClient> clients;

    public LLMChatClientFactory(Collection<AbstractLLMChatClient> clients) {
        this.clients = clients;
    }


    /**
     * 通过指定的模型获取相应的对话客户端Bean
     *
     * @param model 模型名称
     *
     * @return {@link AbstractLLMChatClient }
     */
    public AbstractLLMChatClient getClientByModel(String model) {
        for (AbstractLLMChatClient client : clients) {
            if (client.getSupportedModels().contains(model)) {
                return client;
            }
        }
        throw new LLMChatException("No Model Support Client");
    }

}