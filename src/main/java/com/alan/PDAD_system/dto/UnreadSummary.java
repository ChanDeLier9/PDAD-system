package com.alan.PDAD_system.dto;

import lombok.Data;

@Data
public class UnreadSummary {
    private String fromUserId;
    private Long unreadCount;
    private String latestMessage;
}

