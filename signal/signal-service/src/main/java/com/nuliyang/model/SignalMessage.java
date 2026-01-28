package com.nuliyang.model;

import lombok.Data;

@Data
public class SignalMessage {

    /**
     * 消息类型
     * LOGIN / CALL / ANSWER / CANDIDATE / HEARTBEAT
     */
    private String type;

    /**
     * 发送者 userId
     */
    private Long from;

    /**
     * 接收者 userId
     */
    private Long to;

    /**
     * 具体数据（offer / answer / candidate 等）
     */
    private Object data;
}
