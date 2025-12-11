package com.magicgate.common.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * @author yangyangsheep
 * @date 2025/06/29
 */
@Data
public class DialogueRequest {


    /**
     * <font color="red">对话模型设定参数 Start</font> </br>
     * 问题
     */
    @NotBlank
    private String question;

    /** 指定model */
    private String model;

    /** 指定一个系统提示词 */
    private String promptName;

    /**
     * 温度 （严谨与想象）-越高想象力越丰富
     */
    private Double temperature = 0.5;

    /**
     * 历史对话对象
     */
    private List<DialogueHistoryRequest> history;

    /**
     * 对话的消息输入，可以包含非文本信息
     */
    private List<Message> messages;

    /**
     * 会话id-- 如果有的话，就会自动去查询历史数据拼接到 messages 里面; 否则就是新会话 -- 对话记录持久化之后使用
     */
    private String conversationId;

    /**
     * 是否保存对话记录；默认 false, 控制持久化</br>
     * <font color="red">对话模型设定参数 End</font>
     */
    private boolean saveMessage = false;


    /**
     * <font color="red">知识库的设定参数 Start</font> </br>
     * 知识库检索的相似度阈值
     */
    private Double similarityThreshold = 0.5;
    /** 召回文档数量 */
    private Integer topK = 20;
    /** 是否召回重排 */
    private boolean rerank = true;
    /** 召回重排之后保留数量 */
    private Integer rerankKeepNum = 10;
    /**
     * 相似度搜索半径</br>
     * <font color="red">知识库的设定参数 End</font> </br>
     */
    private Float radius = 0.75F;

    /**
     * 是否进行互联网搜索增强回答
     */
    private boolean whetherConnectInternet = false;


}
