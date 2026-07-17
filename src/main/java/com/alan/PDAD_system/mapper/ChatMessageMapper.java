package com.alan.PDAD_system.mapper;

import com.alan.PDAD_system.dto.ChatContactDTO;
import com.alan.PDAD_system.dto.UnreadSummary;
import com.alan.PDAD_system.entity.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    /**
     * 插入一条消息记录
     */
    @Insert("""
        INSERT INTO chat_message (from_user_id, to_user_id, message_text, timestamp, is_read, message_type)
        VALUES (#{fromUserId}, #{toUserId}, #{messageText}, #{timestamp}, #{isRead}, #{messageType})
    """)
    void insertMessage(ChatMessage message);

    /**查询两个用户间的聊天记录（时间倒序）*/
    @Select("""
        SELECT * FROM chat_message
        WHERE (from_user_id = #{user1} AND to_user_id = #{user2})
           OR (from_user_id = #{user2} AND to_user_id = #{user1})
        ORDER BY timestamp DESC
        LIMIT #{limit}
    """)
    List<ChatMessage> getChatHistory(@Param("user1") String user1, @Param("user2") String user2, @Param("limit") int limit);

    /**获取用户未读消息分组摘要（每个发送方 + 最后一条消息）*/
    @Select("""
        SELECT from_user_id AS fromUserId,
               COUNT(*) AS unreadCount,
               MAX(message_text) AS latestMessage
        FROM chat_message
        WHERE to_user_id = #{userId} AND is_read = false
        GROUP BY from_user_id
    """)
    List<UnreadSummary> getUnreadSummaryByReceiver(@Param("userId") String userId);

    /**
     * 标记对话为已读
     */
    @Update("""
        UPDATE chat_message
        SET is_read = true
        WHERE from_user_id = #{fromUserId} AND to_user_id = #{toUserId}
          AND is_read = false
    """)
    void markMessagesAsRead(@Param("fromUserId") String fromUserId, @Param("toUserId") String toUserId);

    /**
     * 获取某用户总未读数
     */
    @Select("""
        SELECT COUNT(*) FROM chat_message
        WHERE to_user_id = #{userId} AND is_read = false
    """)
    int countUnreadMessages(@Param("userId") String userId);

    /**
     * 获取最近联系人 + 最后一条消息 + 未读数
     * 可用 MySQL 中的子查询/窗口函数实现（此处略去具体SQL）
     */
    @Select("""

            SELECT DISTINCT
    sub.userId,
    sub.username,
    sub.lastMessage,
    sub.timestamp,
    sub.unreadCount
FROM (
    SELECT
        CASE
            WHEN m.from_user_id = #{userId} THEN m.to_user_id
            ELSE m.from_user_id
        END AS userId,
        COALESCE(d.doctorName, p.patientName) AS username,
        m.message_text AS lastMessage,
        m.timestamp,
        SUM(CASE\s
                WHEN m.to_user_id = #{userId} AND m.is_read = false\s
                THEN 1\s
                ELSE 0\s
            END) OVER (PARTITION BY\s
                CASE\s
                    WHEN m.from_user_id = #{userId} THEN m.to_user_id
                    ELSE m.from_user_id
                END
            ) AS unreadCount,
        ROW_NUMBER() OVER (
            PARTITION BY\s
                CASE\s
                    WHEN m.from_user_id = #{userId} THEN m.to_user_id
                    ELSE m.from_user_id
                END\s
            ORDER BY m.timestamp DESC
        ) AS rn
    FROM chat_message m
    LEFT JOIN doctor d ON d.doctorId =\s
        CASE\s
            WHEN m.from_user_id = #{userId} THEN m.to_user_id\s
            ELSE m.from_user_id\s
        END
    LEFT JOIN patient p ON p.patientId =\s
        CASE\s
            WHEN m.from_user_id = #{userId} THEN m.to_user_id\s
            ELSE m.from_user_id\s
        END
    WHERE m.from_user_id = #{userId} OR m.to_user_id = #{userId}
) sub
WHERE sub.rn = 1
ORDER BY sub.timestamp DESC
""")
    List<ChatContactDTO> getRecentContacts(@Param("userId") String userId);


}
