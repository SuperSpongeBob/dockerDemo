package org.example.dockerdemo.chat.service;

import org.example.dockerdemo.chat.entity.ChatMessageEntity;
import org.example.dockerdemo.chat.entity.ChatSession;
import org.example.dockerdemo.chat.repository.ChatMessageRepository;
import org.example.dockerdemo.chat.repository.ChatSessionRepository;
import org.example.dockerdemo.enums.DeleteFlag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天消息服务
 * 负责消息的存储和检索
 */
@Service
public class ChatMessageService {

    private final ChatMessageRepository messageRepository;
    private final ChatSessionRepository sessionRepository;

    public ChatMessageService(ChatMessageRepository messageRepository,
                              ChatSessionRepository sessionRepository) {
        this.messageRepository = messageRepository;
        this.sessionRepository = sessionRepository;
    }

    /**
     * 保存消息到数据库
     * 同时更新关联会话的更新时间
     * 
     * @param sessionId 会话ID
     * @param role 角色 (user/assistant/system)
     * @param content 消息内容
     * @return 保存后的消息实体
     */
    @Transactional
    public ChatMessageEntity saveMessage(String sessionId, String role, String content) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("sessionId不能为空");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("role不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("content不能为空");
        }

        // 获取当前会话的最大序号
        Long maxSeq = messageRepository.findMaxSequenceNumBySessionId(sessionId);
        long nextSeq = (maxSeq == null) ? 1L : maxSeq + 1L;

        ChatMessageEntity message = new ChatMessageEntity();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setSequenceNum(nextSeq);
        message.setDeleteFlag(DeleteFlag.undeleted);
        
        ChatMessageEntity savedMessage = messageRepository.save(message);

        // 更新会话的更新时间
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setUpdateTime(LocalDateTime.now());
            sessionRepository.save(session);
        });

        return savedMessage;
    }

    /**
     * 根据会话ID查询所有消息
     * 按序号正序排列，确保消息顺序稳定
     * 
     * @param sessionId 会话ID
     * @return 消息列表
     */
    public List<ChatMessageEntity> getMessagesBySessionId(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("sessionId不能为空");
        }
        return messageRepository.findBySessionIdAndDeleteFlagOrderBySequenceNumAsc(
                sessionId, DeleteFlag.undeleted);
    }

    /**
     * 获取会话的最近N条消息
     * 用于控制上下文token消耗
     * 
     * @param sessionId 会话ID
     * @param limit 最大消息数量
     * @return 最近的消息列表
     */
    public List<ChatMessageEntity> getRecentMessages(String sessionId, int limit) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("sessionId不能为空");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("limit必须大于0");
        }
        
        List<ChatMessageEntity> allMessages = getMessagesBySessionId(sessionId);
        
        if (allMessages.size() <= limit) {
            return allMessages;
        }
        
        // 返回最近的N条消息
        return allMessages.subList(allMessages.size() - limit, allMessages.size());
    }

    /**
     * 逻辑删除指定会话的所有消息
     * 
     * @param sessionId 会话ID
     */
    @Transactional
    public void deleteMessagesBySessionId(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("sessionId不能为空");
        }
        messageRepository.updateDeleteFlagBySessionId(sessionId, DeleteFlag.deleted);
    }
}
