package com.magicgate.link.domain.client.customized;


import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson2.JSON;
import com.magicgate.common.utils.JobManager;
import com.magicgate.link.domain.client.AbstractLLMChatClient;
import com.magicgate.link.domain.client.LLMProviderProperties;
import com.magicgate.link.domain.model.Dialogue;
import com.magicgate.link.exception.LLMChatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;

/**
 * @Author yangyangsheep
 * @Description 灵积百炼对话客户端-skd调用方式
 * @CreateTime 2025/6/29 23:50
 */
@Component
public class DashScopeSdkChatClient extends AbstractLLMChatClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashScopeSdkChatClient.class);


    protected DashScopeSdkChatClient(LLMProviderProperties providerProperties) {
        super(providerProperties);
    }

    /**
     * 单轮对话文本生成
     *
     * @param dialogue 对话参数
     * @param advisors 可变参数的增强器列表
     *
     * @return {@link String } 生成的文本
     */
    @Override
    public String getChatClientByModelAndDoAnswer(Dialogue dialogue, Advisor... advisors) {
        LLMProviderProperties.ProviderConfig directConfig = getDirectConfig();
        //构建对话参数模型
        Generation gen = new Generation();
        String jobDescription = JobManager.getJobDescription(dialogue.getPromptName());
        Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content(jobDescription).build();
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(dialogue.getQuestion()).build();
        GenerationParam param = GenerationParam.builder().apiKey(directConfig.getApiKey())
                //可替换其他模型
                .model(dialogue.getModel())
                //组装输入
                .messages(Arrays.asList(systemMsg, userMsg))
                //格式化结果的类型
                .resultFormat(GenerationParam.ResultFormat.MESSAGE).build();
        try {
            GenerationResult call = gen.call(param);
            return JSON.toJSONString(call);
        } catch (NoApiKeyException | InputRequiredException e) {
            throw new LLMChatException("LLM Chat Error: " + e.getMessage());
        }
    }

    /**
     * 多轮对话
     *
     * @param dialogue 对话参数
     * @param advisors 可变参数的增强器列表
     *
     * @return {@link Flux }<{@link ServerSentEvent }<{@link String }>>
     */
    @Override
    public Flux<ServerSentEvent<String>> getChatClientByModelAndDoChat(Dialogue dialogue, Advisor... advisors) {
        LLMProviderProperties.ProviderConfig directConfig = getDirectConfig();
        Generation gen = new Generation();
        String jobDescription = JobManager.getJobDescription(dialogue.getPromptName());
        Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content(jobDescription).build();
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(dialogue.getQuestion()).build();
        GenerationParam param = GenerationParam.builder()
                .apiKey(directConfig.getApiKey())
                .model(dialogue.getModel())
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                // Qwen3模型通过enable_thinking参数控制思考过程（开源版默认True，商业版默认False）
                // 使用Qwen3开源版模型时，若未启用流式输出，请将下行取消注释，否则会报错
                //.enableThinking(false)
                .incrementalOutput(true)
                .build();
        try {
            //转换为标准的SSE流返回
            return Flux.from(gen.streamCall(param)).map(data -> {
                LOGGER.info("streamOutut:{}", JSON.toJSONString(data));
                return ServerSentEvent.<String>builder().data(data.getOutput().getChoices().get(0).getMessage().getContent()).build();
            });
        } catch (NoApiKeyException | InputRequiredException e) {
            throw new LLMChatException("LLM Chat Error: " + e.getMessage());
        }
    }

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
    @Override
    protected ChatClient getClient(Dialogue dialogue) {
        //SDK 的调用方式不适用于ChatClient
        return null;
    }

    /**
     * 获取Bean指定的配置项
     *
     * @return {@link LLMProviderProperties.ProviderConfig }
     */
    @Override
    protected LLMProviderProperties.ProviderConfig getDirectConfig() {
        return getProviderProperties().getProviders().getOrDefault("dashscope", new LLMProviderProperties.ProviderConfig());
    }

}
