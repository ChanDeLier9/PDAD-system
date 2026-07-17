package com.alan.PDAD_system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatContactDTO {
    private String userId;
    private String username;
    private String lastMessage;
    private LocalDateTime timestamp;
    private int unreadCount;
}
