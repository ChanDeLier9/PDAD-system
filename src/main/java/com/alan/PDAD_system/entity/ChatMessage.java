package com.alan.PDAD_system.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private Long id; // 主键

    private String fromUserId; // 发送者用户ID
    private String toUserId;   // 接收者用户ID

    private String messageText; // 消息文本内容
    private LocalDateTime timestamp; // 发送时间

    private Boolean isRead; // 是否已读（true/false）

    private String messageType; // 消息类型，例如 text/image/file
}
