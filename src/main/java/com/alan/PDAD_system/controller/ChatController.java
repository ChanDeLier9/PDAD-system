package com.alan.PDAD_system.controller;

import com.alan.PDAD_system.dto.ChatContactDTO;
import com.alan.PDAD_system.dto.Result;
import com.alan.PDAD_system.dto.UnreadSummary;
import com.alan.PDAD_system.dto.UserSimpleDTO;
import com.alan.PDAD_system.entity.ChatMessage;
import com.alan.PDAD_system.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 发送消息
     */
    @PostMapping("/send")
    public Result<String> sendMessage(@RequestBody ChatMessage message) {
        try {
            chatService.sendMessage(message);
            return Result.success("消息发送成功！");
        } catch (Exception e) {
            return Result.error("消息发送失败：" + e.getMessage());
        }
    }

    /**
     * 获取聊天记录（分页可选）
     */
    @GetMapping("/history")
    public Result<List<ChatMessage>> getChatHistory(
            @RequestParam String user1,
            @RequestParam String user2,
            @RequestParam(defaultValue = "30") int limit) {
        try {
            List<ChatMessage> history = chatService.getChatHistory(user1, user2, limit);
            return Result.success(history);
        } catch (Exception e) {
            return Result.error("获取聊天记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户所有未读消息（按发送者分组）
     */
    @GetMapping("/{userId}/unread")
    public Result<List<UnreadSummary>> getUnreadByUser(@PathVariable String userId) {
        try {
            List<UnreadSummary> unread = chatService.getUnreadMessageSummary(userId);
            return Result.success(unread);
        } catch (Exception e) {
            return Result.error("获取未读消息失败：" + e.getMessage());
        }
    }

    /**
     * 标记来自某人的消息为已读
     */
    @PostMapping("/mark-read")
    public Result<String> markRead(@RequestBody Map<String, String> request) {
        String fromUserId = request.get("fromUserId");
        String toUserId = request.get("toUserId");

        if (fromUserId == null || toUserId == null) {
            return Result.error("参数缺失！");
        }

        try {
            chatService.markMessagesAsRead(fromUserId, toUserId);
            return Result.success("已标记为已读");
        } catch (Exception e) {
            return Result.error("标记失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户未读总数（用于头部红点）
     */
    @GetMapping("/{userId}/unread/count")
    public Result<Integer> getUnreadCount(@PathVariable String userId) {
        try {
            int total = chatService.getUnreadCount(userId);
            return Result.success(total);
        } catch (Exception e) {
            return Result.error("获取未读总数失败：" + e.getMessage());
        }
    }

    /**
     * 获取最近联系人列表（带未读数和最近一条消息）
     */
    @GetMapping("/{userId}/contacts")
    public Result<List<ChatContactDTO>> getRecentContacts(@PathVariable String userId) {
        try {
            List<ChatContactDTO> contacts = chatService.getRecentContacts(userId);
            System.out.println("获取到的联系人列表：" + contacts); // 打印 contacts 内容
            return Result.success(contacts);
        } catch (Exception e) {
            return Result.error("获取联系人列表失败：" + e.getMessage());
        }
    }
    /**
     * 精确查找聊天用户（用于发起聊天）
     */
    @GetMapping("/find-user")
    public Result<UserSimpleDTO> findUserByExactName(@RequestParam String role, @RequestParam String name) {
        UserSimpleDTO result = chatService.findUserByExactName(role, name);
        if (result != null) {
            return Result.success(result);
        } else {
            return Result.error("未找到该用户，请确认姓名是否正确");
        }
    }
}
