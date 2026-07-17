package com.alan.PDAD_system.service.impl;

import com.alan.PDAD_system.dto.ChatContactDTO;
import com.alan.PDAD_system.dto.UnreadSummary;
import com.alan.PDAD_system.dto.UserSimpleDTO;
import com.alan.PDAD_system.entity.ChatMessage;
import com.alan.PDAD_system.mapper.ChatMessageMapper;
import com.alan.PDAD_system.mapper.DoctorMapper;
import com.alan.PDAD_system.mapper.PatientMapper;
import com.alan.PDAD_system.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatMessageMapper chatMessageMapper;

    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;

@Autowired
    public ChatServiceImpl(ChatMessageMapper chatMessageMapper, DoctorMapper doctorMapper, PatientMapper patientMapper) {
        this.chatMessageMapper = chatMessageMapper;
        this.doctorMapper = doctorMapper;
        this.patientMapper = patientMapper;
    }

    /**
     * 保存聊天消息到数据库
     */
    @Override
    public void sendMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());      // 设置当前时间
        message.setIsRead(false);                       // 初始为未读
        if (message.getMessageType() == null) {
            message.setMessageType("text");             // 默认类型为 text
        }
        chatMessageMapper.insertMessage(message);       // 调用 Mapper 插入
    }

    /**
     * 获取两用户间的聊天记录（时间倒序 + 限制数量）
     */
    @Override
    public List<ChatMessage> getChatHistory(String user1, String user2, int limit) {
        return chatMessageMapper.getChatHistory(user1, user2, limit);
    }

    /**
     * 获取某用户收到的未读消息摘要（每个发送方一条）
     */
    @Override
    public List<UnreadSummary> getUnreadMessageSummary(String userId) {
        return chatMessageMapper.getUnreadSummaryByReceiver(userId);
    }

    /**
     * 将 fromUserId → toUserId 的消息全部标记为已读
     */
    @Override
    public void markMessagesAsRead(String fromUserId, String toUserId) {
        chatMessageMapper.markMessagesAsRead(fromUserId, toUserId);
    }

    /**
     * 统计该用户收到的总未读消息数
     */
    @Override
    public int getUnreadCount(String userId) {
        return chatMessageMapper.countUnreadMessages(userId);
    }

    /**
     * 获取最近联系人（包含最近一条消息 + 未读数）
     */
    @Override
    public List<ChatContactDTO> getRecentContacts(String userId) {
        return chatMessageMapper.getRecentContacts(userId);
    }

    @Override
    public UserSimpleDTO findUserByExactName(String role, String name) {
        if ("doctor".equalsIgnoreCase(role)) {
            return doctorMapper.findDoctorByExactName(name);
        } else if ("patient".equalsIgnoreCase(role)) {
            return patientMapper.findPatientByExactName(name);
        }
        return null;
    }
}
