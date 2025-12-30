package org.example.dockerdemo.chat.controller;

import org.example.dockerdemo.chat.dto.ChatReply;
import org.example.dockerdemo.chat.dto.ChatRequest;
import org.example.dockerdemo.chat.entity.ChatSession;
import org.example.dockerdemo.chat.exception.DeepSeekException;
import org.example.dockerdemo.chat.service.ChatMessageService;
import org.example.dockerdemo.chat.service.ChatSessionService;
import org.example.dockerdemo.chat.service.DeepSeekService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * DeepSeek 聊天接口
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatController {

    private final DeepSeekService deepSeekService;
    private final ChatSessionService sessionService;
    private final ChatMessageService messageService;

    public ChatController(DeepSeekService deepSeekService,
                          ChatSessionService sessionService,
                          ChatMessageService messageService) {
        this.deepSeekService = deepSeekService;
        this.sessionService = sessionService;
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<ChatReply> chat(@RequestBody ChatRequest request) {
        try {
            String sessionId = request.getSessionId();
            
            // 如果没有sessionId，自动创建新会话
            if (!StringUtils.hasText(sessionId)) {
                ChatSession newSession = sessionService.createSession(request.getMessage());
                sessionId = newSession.getId();
                request.setSessionId(sessionId);
            } else {
                // 验证会话是否存在，如果不存在则创建新会话
                if (!sessionService.getSession(sessionId).isPresent()) {
                    ChatSession newSession = sessionService.createSession(request.getMessage());
                    sessionId = newSession.getId();
                    request.setSessionId(sessionId);
                }
                // 会话存在时，sessionId保持不变，继续使用该会话
            }
            
            // 先调用DeepSeek API获取回复（此时从数据库加载历史消息作为上下文）
            String reply = deepSeekService.chat(request);
            
            // API调用成功后，保存用户消息和AI回复
            messageService.saveMessage(sessionId, "user", request.getMessage());
            messageService.saveMessage(sessionId, "assistant", reply);
            
            return ResponseEntity.ok(new ChatReply(reply, sessionId));
        } catch (DeepSeekException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, ex.getMessage());
        }
    }
}
