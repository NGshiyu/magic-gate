package com.magicgate.link.domain.client.customized;

import com.alibaba.cloud.ai.advisor.DocumentRetrievalAdvisor;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import com.magicgate.common.utils.JobManager;
import com.magicgate.link.domain.advisor.SafeGuardAdvisor;
import com.magicgate.link.domain.client.AbstractLLMChatClient;
import com.magicgate.link.domain.client.LLMProviderProperties;
import com.magicgate.link.domain.model.Dialogue;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

/**
 * @Author yangyangsheep
 * @ClassName DashScopeOpenAiChatClient.java
 * @Description 千问的OpenAi兼容调用方式
 * @CreateTime 2025/6/30 23:48
 */

@Component
@Getter
public class DashScopeOpenAiChatClient extends AbstractLLMChatClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashScopeOpenAiChatClient.class);


    protected DashScopeOpenAiChatClient(LLMProviderProperties providerProperties) {
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
        ChatClient client = getClient(dialogue);
        String jobDescription = JobManager.getJobDescription(dialogue.getPromptName());
        //CallResponse是一个Spring Ai通用的返回模型
        return client.prompt(jobDescription)
                .advisors(SafeGuardAdvisor.builder().sensitiveWords(List.of("Fool")).build()) //这里可以使用增强处理，阶段大模型的返回并做一些对应的操作，比如用户输入敏感词的检测，可以在这里完成
                .user(dialogue.getQuestion())
                .call().content();
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
        ChatClient client = getClient(dialogue);
        String jobDescription = JobManager.getJobDescription(dialogue.getPromptName());
        //CallResponse是一个Spring Ai通用的返回模型
        ChatClient.ChatClientRequestSpec doChat = client.prompt(jobDescription)
                .advisors(SafeGuardAdvisor.builder().sensitiveWords(List.of("Fool")).build()) //这里可以使用增强处理，阶段大模型的返回并做一些对应的操作，比如用户输入敏感词的检测，可以在这里完成
                .user(dialogue.getQuestion());
        //以上为获取对话结果处理，以下为结果的自定义处理转换为标准的SSE流返回
        //Tips!!!!!!!!!!!!!!!!!!!!!!!!!!!! content()是直接获取输出的文本，也就是对话的流文本，如果需要处理其他内容，可以获取chatClientResponse之后操作
        return doChat.stream().content()
                //缓冲分批处理（推荐，平衡性能与流式特性）
                // 每收集 10个数据块 或 每过 100毫秒，就把收集到的数据块打包成一个 List<String> 发往下游，根据实际情况调整这两个参数
                .bufferTimeout(10, Duration.ofMillis(100))
                .map(list -> String.join("", list))
                .map(res -> ServerSentEvent.<String>builder().data(res).build());
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
        LLMProviderProperties.ProviderConfig directConfig = getDirectConfig();
        //构建LLM接入鉴权参数以及地址
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(directConfig.getApiKey())
                .baseUrl(directConfig.getBaseUrl())
                .workSpaceId("llm-xxxxx")
                .build();


        DocumentRetriever retriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder().withIndexName("demo测试企业知识库").build());

        DashScopeChatModel dashScopeChatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(
                        //大模型的调用参数
                        DashScopeChatOptions.builder()
                                .withModel(dialogue.getModel())  // 使用视觉模型
                                //.withMultiModel(true)             // 启用多模态
                                //.withVlHighResolutionImages(true) // 启用高分辨率图片处理
                                .withTemperature(dialogue.getTemperature())
                                .build()
                )
                .build();
        //在这里可以直接做知识库的注入，知识库的管理可以放在另一个模块去处理
        return ChatClient.builder(dashScopeChatModel).defaultAdvisors(new DocumentRetrievalAdvisor(retriever)).build();
    }

    /**
     * 获取Bean指定的配置项
     *
     * @return {@link LLMProviderProperties.ProviderConfig }
     */
    @Override
    protected LLMProviderProperties.ProviderConfig getDirectConfig() {
        return getProviderProperties().getProviders().getOrDefault("dashscope-openai", new LLMProviderProperties.ProviderConfig());
    }
}


