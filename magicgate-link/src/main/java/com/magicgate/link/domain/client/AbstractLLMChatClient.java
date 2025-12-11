package com.magicgate.link.domain.client;

import com.magicgate.link.domain.model.Dialogue;
import lombok.Getter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @Author yangyangsheep
 * @Description 对话模型工厂抽象类
 * @CreateTime 2025/6/29 23:48
 */
@Getter
@Component
public abstract class AbstractLLMChatClient {


    private final LLMProviderProperties providerProperties;

    protected AbstractLLMChatClient(LLMProviderProperties providerProperties) {this.providerProperties = providerProperties;}


    /**
     * 单轮对话文本生成
     *
     * @param dialogue 对话参数
     * @param advisors 可变参数的增强器列表
     *
     * @return {@link String } 生成的文本
     */
    public abstract String getChatClientByModelAndDoAnswer(Dialogue dialogue, Advisor... advisors);

    /**
     * 多轮对话
     *
     * @param dialogue 对话参数
     * @param advisors 可变参数的增强器列表
     *
     * @return {@link Flux }<{@link ServerSentEvent }<{@link String }>>
     */
    public abstract Flux<ServerSentEvent<String>> getChatClientByModelAndDoChat(Dialogue dialogue, Advisor... advisors);

    /**
     * 获取当前的对话客户端</br>
     * spring ai 可以适配</br>
     * -azure-openai</br>
     * -core</br>
     * -openai</br>
     * -retry</br>
     * -zhipuai</br>
     *
     * @param dialogue 对话参数
     *
     * @return {@link ChatClient } 标准Spring Ai 对话客户端
     */
    protected abstract ChatClient getClient(Dialogue dialogue);


    /**
     * 获取Bean指定的配置项
     *
     * @return {@link LLMProviderProperties.ProviderConfig }
     */
    protected abstract LLMProviderProperties.ProviderConfig getDirectConfig();

    /**
     * 获取支持的模型
     *
     * @return {@link List }<{@link String }>
     */
    public List<String> getSupportedModels() {
        LLMProviderProperties.ProviderConfig directConfig = getDirectConfig();
        return directConfig.getModels();
    }

}
