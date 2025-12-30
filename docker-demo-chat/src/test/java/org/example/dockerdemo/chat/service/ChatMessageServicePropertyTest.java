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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ChatMessageService 属性测试
 * 
 * Feature: chat-history-storage
 * Property 5: 消息存储与检索一致性
 * Property 6: 消息排序正确性
 * Property 8: 历史消息加载正确性
 * Validates: Requirements 2.1, 2.2, 2.3, 3.1, 3.2
 */
@SpringBootTest
@ActiveProfiles("test")
class ChatMessageServicePropertyTest {

    @Autowired
    private ChatMessageService messageService;

    @Autowired
    private ChatSessionRepository sessionRepository;

    @Autowired
    private ChatMessageRepository messageRepository;

    @Autowired
    private DeepSeekService deepSeekService;

    private String testSessionId;

    @BeforeEach
    void setUp() {
        // 清理数据
        messageRepository.deleteAll();
        sessionRepository.deleteAll();
        
        // 创建测试会话
        ChatSession session = new ChatSession();
        session.setTitle("Test Session");
        session.setDeleteFlag(DeleteFlag.undeleted);
        ChatSession savedSession = sessionRepository.save(session);
        testSessionId = savedSession.getId();
    }

    /**
     * Property 5: 消息存储与检索一致性
     * For any 存储的消息，通过会话ID查询返回的消息内容、角色应与存储时一致。
     * 
     * Feature: chat-history-storage, Property 5: 消息存储与检索一致性
     * Validates: Requirements 2.1, 2.2
     */
    @Property(tries = 100)
    @Transactional
    void property5_messageStorageAndRetrievalConsistency(
            @ForAll("validRoles") String role,
            @ForAll @NotBlank @StringLength(min = 1, max = 500) String content) {
        
        // 存储消息
        ChatMessageEntity savedMessage = messageService.saveMessage(testSessionId, role, content);
        
        // 检索消息
        List<ChatMessageEntity> messages = messageService.getMessagesBySessionId(testSessionId);
        
        // 验证一致性
        assertThat(messages).isNotEmpty();
        
        ChatMessageEntity retrievedMessage = messages.stream()
                .filter(m -> m.getId().equals(savedMessage.getId()))
                .findFirst()
                .orElse(null);
        
        assertThat(retrievedMessage).isNotNull();
        assertThat(retrievedMessage.getRole()).isEqualTo(role);
        assertThat(retrievedMessage.getContent()).isEqualTo(content);
        assertThat(retrievedMessage.getSessionId()).isEqualTo(testSessionId);
    }

    /**
     * Property 6: 消息排序正确性
     * For any 会话的消息列表查询，返回的消息应按创建时间正序排列。
     * 
     * Feature: chat-history-storage, Property 6: 消息排序正确性
     * Validates: Requirements 2.3
     */
    @Property(tries = 100)
    @Transactional
    void property6_messageOrderingCorrectness(
            @ForAll("messageContents") List<String> contents) {
        
        Assume.that(contents.size() >= 2);
        
        // 按顺序存储多条消息
        for (int i = 0; i < contents.size(); i++) {
            String role = (i % 2 == 0) ? "user" : "assistant";
            messageService.saveMessage(testSessionId, role, contents.get(i));
            
            // 添加小延迟确保时间戳不同
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 检索消息
        List<ChatMessageEntity> messages = messageService.getMessagesBySessionId(testSessionId);
        
        // 验证排序正确性 - 按创建时间正序
        assertThat(messages).hasSize(contents.size());
        
        for (int i = 0; i < messages.size() - 1; i++) {
            ChatMessageEntity current = messages.get(i);
            ChatMessageEntity next = messages.get(i + 1);
            
            assertThat(current.getCreateTime())
                    .isBeforeOrEqualTo(next.getCreateTime());
        }
        
        // 验证内容顺序与存储顺序一致
        for (int i = 0; i < contents.size(); i++) {
            assertThat(messages.get(i).getContent()).isEqualTo(contents.get(i));
        }
    }

    @Provide
    Arbitrary<String> validRoles() {
        return Arbitraries.of("user", "assistant", "system");
    }

    @Provide
    Arbitrary<List<String>> messageContents() {
        return Arbitraries.strings()
                .alpha()
                .ofMinLength(1)
                .ofMaxLength(100)
                .list()
                .ofMinSize(2)
                .ofMaxSize(10);
    }

    /**
     * Property 8: 历史消息加载正确性
     * For any 带sessionId的聊天请求，DeepSeek调用时应包含该会话的历史消息作为上下文。
     * 
     * Feature: chat-history-storage, Property 8: 历史消息加载正确性
     * Validates: Requirements 3.1, 3.2
     */
    @Property(tries = 100)
    @Transactional
    void property8_historyMessageLoadingCorrectness(
            @ForAll("messageContents") List<String> contents) {
        
        Assume.that(contents.size() >= 2);
        
        // 按顺序存储多条消息
        for (int i = 0; i < contents.size(); i++) {
            String role = (i % 2 == 0) ? "user" : "assistant";
            messageService.saveMessage(testSessionId, role, contents.get(i));
            
            // 添加小延迟确保时间戳不同
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 通过DeepSeekService加载历史消息
        List<ChatMessageEntity> loadedHistory = deepSeekService.loadHistoryFromDatabase(testSessionId);
        
        // 验证加载的历史消息数量正确
        assertThat(loadedHistory).hasSize(contents.size());
        
        // 验证消息按时间正序排列
        for (int i = 0; i < loadedHistory.size() - 1; i++) {
            ChatMessageEntity current = loadedHistory.get(i);
            ChatMessageEntity next = loadedHistory.get(i + 1);
            assertThat(current.getCreateTime()).isBeforeOrEqualTo(next.getCreateTime());
        }
        
        // 验证消息内容与存储时一致
        for (int i = 0; i < contents.size(); i++) {
            assertThat(loadedHistory.get(i).getContent()).isEqualTo(contents.get(i));
        }
    }

    /**
     * Property 8 补充: 历史消息加载数量限制
     * For any 会话历史消息过多时，应只加载最近的N条消息以控制token消耗。
     * 
     * Feature: chat-history-storage, Property 8: 历史消息加载正确性
     * Validates: Requirements 3.3
     */
    @Property(tries = 50)
    @Transactional
    void property8_historyMessageLoadingWithLimit(
            @ForAll("largeMessageContents") List<String> contents) {
        
        int limit = 5;
        Assume.that(contents.size() > limit);
        
        // 存储多条消息
        for (int i = 0; i < contents.size(); i++) {
            String role = (i % 2 == 0) ? "user" : "assistant";
            messageService.saveMessage(testSessionId, role, contents.get(i));
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 通过DeepSeekService加载限制数量的历史消息
        List<ChatMessageEntity> loadedHistory = deepSeekService.loadHistoryFromDatabase(testSessionId, limit);
        
        // 验证加载的消息数量不超过限制
        assertThat(loadedHistory).hasSize(limit);
        
        // 验证加载的是最近的N条消息
        List<String> expectedContents = contents.subList(contents.size() - limit, contents.size());
        for (int i = 0; i < limit; i++) {
            assertThat(loadedHistory.get(i).getContent()).isEqualTo(expectedContents.get(i));
        }
    }

    @Provide
    Arbitrary<List<String>> largeMessageContents() {
        return Arbitraries.strings()
                .alpha()
                .ofMinLength(1)
                .ofMaxLength(50)
                .list()
                .ofMinSize(8)
                .ofMaxSize(15);
    }
}
