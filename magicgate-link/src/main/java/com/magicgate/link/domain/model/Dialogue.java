package com.magicgate.link.domain.model;

import com.magicgate.common.request.DialogueHistoryRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * @Author yangyangsheep
 * @Description 对话模型
 * @CreateTime 2025/6/29 15:31
 */
@Getter
@Setter
public class Dialogue {

    /**
     * <font color="red">对话模型设定参数 Start</font> </br>
     * 问题
     */
    private String question;

    /** 指定model */
    private String model = "qwen-plus";

    /** 指定一个系统提示词 */
    private String promptName = "AiTeacher";

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
}
