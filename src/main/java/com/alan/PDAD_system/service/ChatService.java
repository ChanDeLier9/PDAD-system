package com.alan.PDAD_system.service;

import com.alan.PDAD_system.dto.*;
import com.alan.PDAD_system.dto.UnreadSummary;
import com.alan.PDAD_system.entity.ChatMessage;

import java.util.List;

public interface ChatService {

    void sendMessage(ChatMessage message);

    List<ChatMessage> getChatHistory(String user1, String user2, int limit);

    List<UnreadSummary> getUnreadMessageSummary(String userId);

    void markMessagesAsRead(String fromUserId, String toUserId);

    int getUnreadCount(String userId);


    UserSimpleDTO findUserByExactName(String role, String name);
    List<ChatContactDTO> getRecentContacts(String userId);
}
