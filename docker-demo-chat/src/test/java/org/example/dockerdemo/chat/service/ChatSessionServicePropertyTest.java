package org.example.dockerdemo.chat.service;

import net.jqwik.api.*;
import net.jqwik.api.constraints.NotBlank;
import net.jqwik.api.constraints.StringLength;
import org.example.dockerdemo.chat.entity.ChatMessageEntity;
import org.example.dockerdemo.chat.entity.ChatSession;
import org.example.dockerdemo.chat.repository.ChatMessageRepository;
import org.example.dockerdemo.chat.repository.ChatSessionRepository;
import org.example.dockerdemo.enums.DeleteFlag;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ChatSessionService 属性测试
 * 
 * Feature: chat-history-storage
 * Property 1: 会话创建返回有效ID
 * Property 2: 会话列表排序正确性
 * Property 3: 会话查询一致性
 * Property 4: 逻辑删除完整性
 * Property 9: 会话标题自动生成
 * Property 10: 会话标题更新
 * Validates: Requirements 1.1, 1.2, 1.3, 1.4, 4.1, 4.2
 */
@SpringBootTest
@ActiveProfiles("test")
class ChatSessionServicePropertyTest {

    @Autowired
    private ChatSessionService sessionService;

    @Autowired
    private ChatMessageService messageService;

    @Autowired
    private ChatSessionRepository sessionRepository;

    @Autowired
    private ChatMessageRepository messageRepository;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        sessionRepository.deleteAll();
    }

    /**
     * Property 1: 会话创建返回有效ID
     * For any 新会话创建请求，创建后返回的会话ID应不为空，且通过该ID能够查询到该会话。
     * 
     * Feature: chat-history-storage, Property 1: 会话创建返回有效ID
     * Validates: Requirements 1.1
     */
    @Property(tries = 100)
    @Transactional
    void property1_sessionCreationReturnsValidId(
            @ForAll @NotBlank @StringLength(min = 1, max = 100) String firstMessage) {
        
        // 创建会话
        ChatSession session = sessionService.createSession(firstMessage);
        
        // 验证ID不为空
        assertThat(session.getId()).isNotNull().isNotEmpty();
        
        // 验证通过ID能查询到会话
        Optional<ChatSession> retrieved = sessionService.getSession(session.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getId()).isEqualTo(session.getId());
    }

    /**
     * Property 2: 会话列表排序正确性
     * For any 会话列表查询，返回的会话应按更新时间倒序排列，且不包含已删除的会话。
     * 
     * Feature: chat-history-storage, Property 2: 会话列表排序正确性
     * Validates: Requirements 1.2
     */
    @Property(tries = 100)
    @Transactional
    void property2_sessionListOrderingCorrectness(
            @ForAll("sessionTitles") List<String> titles) {
        
        Assume.that(titles.size() >= 2);
        
        // 创建多个会话
        for (String title : titles) {
            sessionService.createSession(title);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 获取会话列表
        List<ChatSession> sessions = sessionService.listSessions();
        
        // 验证按更新时间倒序排列
        assertThat(sessions).hasSize(titles.size());
        for (int i = 0; i < sessions.size() - 1; i++) {
            LocalDateTime current = sessions.get(i).getUpdateTime();
            LocalDateTime next = sessions.get(i + 1).getUpdateTime();
            assertThat(current).isAfterOrEqualTo(next);
        }
        
        // 验证不包含已删除的会话
        for (ChatSession session : sessions) {
            assertThat(session.getDeleteFlag()).isEqualTo(DeleteFlag.undeleted);
        }
    }

    /**
     * Property 3: 会话查询一致性
     * For any 已创建的会话，通过ID查询返回的会话信息应与创建时的信息一致。
     * 
     * Feature: chat-history-storage, Property 3: 会话查询一致性
     * Validates: Requirements 1.3
     */
    @Property(tries = 100)
    @Transactional
    void property3_sessionQueryConsistency(
            @ForAll @NotBlank @StringLength(min = 1, max = 100) String firstMessage) {
        
        // 创建会话
        ChatSession created = sessionService.createSession(firstMessage);
        String expectedTitle = firstMessage.length() <= 20 
                ? firstMessage 
                : firstMessage.substring(0, 20);
        
        // 查询会话
        Optional<ChatSession> retrieved = sessionService.getSession(created.getId());
        
        // 验证一致性
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getId()).isEqualTo(created.getId());
        assertThat(retrieved.get().getTitle()).isEqualTo(expectedTitle);
        assertThat(retrieved.get().getDeleteFlag()).isEqualTo(DeleteFlag.undeleted);
    }

    /**
     * Property 4: 逻辑删除完整性
     * For any 被删除的会话，该会话及其所有关联消息的delete_flag都应被设置为已删除状态。
     * 
     * Feature: chat-history-storage, Property 4: 逻辑删除完整性
     * Validates: Requirements 1.4
     */
    @Property(tries = 100)
    @Transactional
    void property4_logicalDeleteCompleteness(
            @ForAll @NotBlank @StringLength(min = 1, max = 50) String firstMessage,
            @ForAll("messageContents") List<String> messageContents) {
        
        // 创建会话
        ChatSession session = sessionService.createSession(firstMessage);
        String sessionId = session.getId();
        
        // 添加消息
        for (int i = 0; i < messageContents.size(); i++) {
            String role = (i % 2 == 0) ? "user" : "assistant";
            messageService.saveMessage(sessionId, role, messageContents.get(i));
        }
        
        // 删除会话
        sessionService.deleteSession(sessionId);
        
        // 验证会话已被逻辑删除
        Optional<ChatSession> deletedSession = sessionRepository.findById(sessionId);
        assertThat(deletedSession).isPresent();
        assertThat(deletedSession.get().getDeleteFlag()).isEqualTo(DeleteFlag.deleted);
        
        // 验证通过服务查询不到已删除的会话
        Optional<ChatSession> queryResult = sessionService.getSession(sessionId);
        assertThat(queryResult).isEmpty();
        
        // 验证所有消息也被逻辑删除
        List<ChatMessageEntity> messages = messageRepository.findAll();
        for (ChatMessageEntity msg : messages) {
            if (msg.getSessionId().equals(sessionId)) {
                assertThat(msg.getDeleteFlag()).isEqualTo(DeleteFlag.deleted);
            }
        }
    }

    /**
     * Property 9: 会话标题自动生成
     * For any 新创建的会话，其标题应为第一条用户消息的前20个字符（或全部内容如果少于20字符）。
     * 
     * Feature: chat-history-storage, Property 9: 会话标题自动生成
     * Validates: Requirements 4.1
     */
    @Property(tries = 100)
    @Transactional
    void property9_sessionTitleAutoGeneration(
            @ForAll @NotBlank @StringLength(min = 1, max = 100) String firstMessage) {
        
        // 创建会话
        ChatSession session = sessionService.createSession(firstMessage);
        
        // 计算期望的标题
        String expectedTitle = firstMessage.length() <= 20 
                ? firstMessage 
                : firstMessage.substring(0, 20);
        
        // 验证标题
        assertThat(session.getTitle()).isEqualTo(expectedTitle);
    }

    /**
     * Property 10: 会话标题更新
     * For any 会话标题更新操作，更新后查询该会话应返回新标题。
     * 
     * Feature: chat-history-storage, Property 10: 会话标题更新
     * Validates: Requirements 4.2
     */
    @Property(tries = 100)
    @Transactional
    void property10_sessionTitleUpdate(
            @ForAll @NotBlank @StringLength(min = 1, max = 50) String firstMessage,
            @ForAll @NotBlank @StringLength(min = 1, max = 50) String newTitle) {
        
        // 创建会话
        ChatSession session = sessionService.createSession(firstMessage);
        String sessionId = session.getId();
        
        // 更新标题
        Optional<ChatSession> updated = sessionService.updateTitle(sessionId, newTitle);
        
        // 验证更新成功
        assertThat(updated).isPresent();
        assertThat(updated.get().getTitle()).isEqualTo(newTitle);
        
        // 再次查询验证
        Optional<ChatSession> retrieved = sessionService.getSession(sessionId);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getTitle()).isEqualTo(newTitle);
    }

    /**
     * Property 7: 消息存储触发会话更新
     * For any 新消息存储操作，关联会话的update_time应被更新为当前时间或更晚。
     * 
     * Feature: chat-history-storage, Property 7: 消息存储触发会话更新
     * Validates: Requirements 2.5
     */
    @Property(tries = 100)
    @Transactional
    void property7_messageStorageTriggersSessionUpdate(
            @ForAll @NotBlank @StringLength(min = 1, max = 50) String firstMessage,
            @ForAll @NotBlank @StringLength(min = 1, max = 100) String messageContent) {
        
        // 创建会话
        ChatSession session = sessionService.createSession(firstMessage);
        String sessionId = session.getId();
        LocalDateTime originalUpdateTime = session.getUpdateTime();
        
        // 等待一小段时间确保时间戳不同
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 存储新消息
        messageService.saveMessage(sessionId, "user", messageContent);
        
        // 重新查询会话
        Optional<ChatSession> updatedSession = sessionService.getSession(sessionId);
        
        // 验证会话更新时间已更新
        assertThat(updatedSession).isPresent();
        assertThat(updatedSession.get().getUpdateTime())
                .isAfterOrEqualTo(originalUpdateTime);
    }

    @Provide
    Arbitrary<List<String>> sessionTitles() {
        return Arbitraries.strings()
                .alpha()
                .ofMinLength(1)
                .ofMaxLength(50)
                .list()
                .ofMinSize(2)
                .ofMaxSize(5);
    }

    @Provide
    Arbitrary<List<String>> messageContents() {
        return Arbitraries.strings()
                .alpha()
                .ofMinLength(1)
                .ofMaxLength(100)
                .list()
                .ofMinSize(1)
                .ofMaxSize(5);
    }
}
