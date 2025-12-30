package org.example.dockerdemo.chat.controller;

import org.example.dockerdemo.chat.dto.MessageDTO;
import org.example.dockerdemo.chat.dto.SessionDTO;
import org.example.dockerdemo.chat.entity.ChatMessageEntity;
import org.example.dockerdemo.chat.entity.ChatSession;
import org.example.dockerdemo.chat.exception.SessionNotFoundException;
import org.example.dockerdemo.chat.service.ChatMessageService;
import org.example.dockerdemo.chat.service.ChatSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 聊天会话控制器
 * 提供会话管理的REST API接口
 */
@RestController
@RequestMapping("/api/chat/sessions")
@CrossOrigin
public class ChatSessionController {

    private final ChatSessionService sessionService;
    private final ChatMessageService messageService;

    public ChatSessionController(ChatSessionService sessionService,
                                  ChatMessageService messageService) {
        this.sessionService = sessionService;
        this.messageService = messageService;
    }

    /**
     * 创建新会话
     * POST /api/chat/sessions
     * 
     * @param request 请求体，包含firstMessage字段
     * @return 创建的会话信息
     */
    @PostMapping
    public ResponseEntity<SessionDTO> createSession(@RequestBody(required = false) Map<String, String> request) {
        String firstMessage = request != null ? request.get("firstMessage") : null;
        ChatSession session = sessionService.createSession(firstMessage);
        return ResponseEntity.status(HttpStatus.CREATED).body(toSessionDTO(session));
    }

    /**
     * 获取会话列表
     * GET /api/chat/sessions
     * 
     * @return 会话列表（不包含消息）
     */
    @GetMapping
    public ResponseEntity<List<SessionDTO>> listSessions() {
        List<ChatSession> sessions = sessionService.listSessions();
        List<SessionDTO> dtos = sessions.stream()
                .map(this::toSessionDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    /**
     * 获取会话详情（包含消息）
     * GET /api/chat/sessions/{id}
     * 
     * @param id 会话ID
     * @return 会话详情及其消息列表
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionDTO> getSession(@PathVariable String id) {
        ChatSession session = sessionService.getSession(id)
                .orElseThrow(() -> new SessionNotFoundException(id));
        
        List<ChatMessageEntity> messages = messageService.getMessagesBySessionId(id);
        SessionDTO dto = toSessionDTOWithMessages(session, messages);
        return ResponseEntity.ok(dto);
    }

    /**
     * 更新会话标题
     * PUT /api/chat/sessions/{id}
     * 
     * @param id 会话ID
     * @param request 请求体，包含title字段
     * @return 更新后的会话信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<SessionDTO> updateTitle(@PathVariable String id,
                                                   @RequestBody Map<String, String> request) {
        String title = request.get("title");
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("title不能为空");
        }
        
        ChatSession session = sessionService.updateTitle(id, title)
                .orElseThrow(() -> new SessionNotFoundException(id));
        return ResponseEntity.ok(toSessionDTO(session));
    }

    /**
     * 删除会话
     * DELETE /api/chat/sessions/{id}
     * 
     * @param id 会话ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable String id) {
        // 先检查会话是否存在
        sessionService.getSession(id)
                .orElseThrow(() -> new SessionNotFoundException(id));
        
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 将ChatSession实体转换为SessionDTO（不含消息）
     */
    private SessionDTO toSessionDTO(ChatSession session) {
        return new SessionDTO(
                session.getId(),
                session.getTitle(),
                session.getCreateTime(),
                session.getUpdateTime()
        );
    }

    /**
     * 将ChatSession实体转换为SessionDTO（含消息）
     */
    private SessionDTO toSessionDTOWithMessages(ChatSession session, List<ChatMessageEntity> messages) {
        List<MessageDTO> messageDTOs = messages.stream()
                .map(this::toMessageDTO)
                .collect(Collectors.toList());
        
        return new SessionDTO(
                session.getId(),
                session.getTitle(),
                session.getCreateTime(),
                session.getUpdateTime(),
                messageDTOs
        );
    }

    /**
     * 将ChatMessageEntity实体转换为MessageDTO
     */
    private MessageDTO toMessageDTO(ChatMessageEntity message) {
        return new MessageDTO(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreateTime()
        );
    }
}
