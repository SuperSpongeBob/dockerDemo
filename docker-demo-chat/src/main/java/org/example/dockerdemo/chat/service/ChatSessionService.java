package org.example.dockerdemo.chat.service;

import org.example.dockerdemo.chat.entity.ChatSession;
import org.example.dockerdemo.chat.repository.ChatSessionRepository;
import org.example.dockerdemo.enums.DeleteFlag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 聊天会话服务
 * 负责会话的创建、查询和管理
 */
@Service
public class ChatSessionService {

    private static final int DEFAULT_TITLE_MAX_LENGTH = 20;

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageService messageService;

    public ChatSessionService(ChatSessionRepository sessionRepository,
                              ChatMessageService messageService) {
        this.sessionRepository = sessionRepository;
        this.messageService = messageService;
    }

    /**
     * 创建新会话
     * 使用第一条用户消息的前20个字符作为默认标题
     * 
     * @param firstMessage 第一条用户消息，用于生成标题
     * @return 创建的会话实体
     */
    @Transactional
    public ChatSession createSession(String firstMessage) {
        ChatSession session = new ChatSession();
        session.setTitle(generateTitle(firstMessage));
        session.setDeleteFlag(DeleteFlag.undeleted);
        return sessionRepository.save(session);
    }

    /**
     * 获取所有未删除的会话列表
     * 按更新时间倒序排列
     * 
     * @return 会话列表
     */
    public List<ChatSession> listSessions() {
        return sessionRepository.findByDeleteFlagOrderByUpdateTimeDesc(DeleteFlag.undeleted);
    }

    /**
     * 根据ID获取会话详情
     * 
     * @param sessionId 会话ID
     * @return 会话Optional
     */
    public Optional<ChatSession> getSession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return Optional.empty();
        }
        return sessionRepository.findByIdAndDeleteFlag(sessionId, DeleteFlag.undeleted);
    }

    /**
     * 逻辑删除会话及其所有消息
     * 
     * @param sessionId 会话ID
     */
    @Transactional
    public void deleteSession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("sessionId不能为空");
        }
        
        Optional<ChatSession> sessionOpt = sessionRepository.findByIdAndDeleteFlag(
                sessionId, DeleteFlag.undeleted);
        
        if (sessionOpt.isPresent()) {
            ChatSession session = sessionOpt.get();
            session.setDeleteFlag(DeleteFlag.deleted);
            sessionRepository.save(session);
            
            // 同时逻辑删除该会话的所有消息
            messageService.deleteMessagesBySessionId(sessionId);
        }
    }

    /**
     * 更新会话标题
     * 
     * @param sessionId 会话ID
     * @param title 新标题
     * @return 更新后的会话Optional
     */
    @Transactional
    public Optional<ChatSession> updateTitle(String sessionId, String title) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("sessionId不能为空");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("title不能为空");
        }
        
        Optional<ChatSession> sessionOpt = sessionRepository.findByIdAndDeleteFlag(
                sessionId, DeleteFlag.undeleted);
        
        if (sessionOpt.isPresent()) {
            ChatSession session = sessionOpt.get();
            session.setTitle(title.length() > 100 ? title.substring(0, 100) : title);
            return Optional.of(sessionRepository.save(session));
        }
        
        return Optional.empty();
    }

    /**
     * 根据第一条消息生成会话标题
     * 取前20个字符，如果消息为空则使用默认标题
     * 
     * @param firstMessage 第一条消息
     * @return 生成的标题
     */
    private String generateTitle(String firstMessage) {
        if (firstMessage == null || firstMessage.trim().isEmpty()) {
            return "新对话";
        }
        
        String trimmed = firstMessage.trim();
        if (trimmed.length() <= DEFAULT_TITLE_MAX_LENGTH) {
            return trimmed;
        }
        return trimmed.substring(0, DEFAULT_TITLE_MAX_LENGTH);
    }
}
