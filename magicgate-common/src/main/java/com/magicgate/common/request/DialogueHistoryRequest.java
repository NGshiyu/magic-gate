package com.magicgate.common.request;

import lombok.Data;

/**
 * @Author yangyangsheep
 * @Description 历史对话记录信息
 * @CreateTime 2025/6/29 15:49
 */
@Data
public class DialogueHistoryRequest {

    /** 历史对话角色 */
    private String role;

    /** 内历史对话内容 */
    private String content;
}
