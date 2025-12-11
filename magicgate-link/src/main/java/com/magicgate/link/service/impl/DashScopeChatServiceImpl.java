package com.magicgate.link.service.impl;

import com.magicgate.common.request.DialogueRequest;
import com.magicgate.common.utils.BeanCopyUtils;
import com.magicgate.link.domain.client.AbstractLLMChatClient;
import com.magicgate.link.domain.client.LLMChatClientFactory;
import com.magicgate.link.domain.model.Dialogue;
import com.magicgate.link.service.DashScopeChatService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @Author yangyangsheep
 * @Description 灵积百炼对话服务
 * @CreateTime 2025/6/29 13:39
 */
@Service
public class DashScopeChatServiceImpl implements DashScopeChatService {

    @Autowired
    LLMChatClientFactory llmChatClientFactory;

    /**
     * 单论对话获取结果信息
     *
     * @param request 对话参数
     *
     * @return {@link String }
     */
    @Override
    public String singleAnswer(DialogueRequest request) {
        Dialogue dialogue = new Dialogue();
        //RAG调用注入知识
        BeanUtils.copyProperties(request, dialogue, BeanCopyUtils.getNullAndBlankPropertyNames(request));
        AbstractLLMChatClient clientByModel = llmChatClientFactory.getClientByModel(dialogue.getModel());
        return clientByModel.getChatClientByModelAndDoAnswer(dialogue);
    }

    /**
     * 多轮流式对话接口
     *
     * @param request 对话参数
     *
     * @return {@link Flux }
     */
    @Override
    public Flux<ServerSentEvent<String>> chat(DialogueRequest request) {
        Dialogue dialogue = new Dialogue();
        //RAG调用注入知识
        BeanUtils.copyProperties(request, dialogue, BeanCopyUtils.getNullAndBlankPropertyNames(request));
        AbstractLLMChatClient clientByModel = llmChatClientFactory.getClientByModel(dialogue.getModel());
        return clientByModel.getChatClientByModelAndDoChat(dialogue);
    }
}
