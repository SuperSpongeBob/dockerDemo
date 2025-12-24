package org.example.dockerdemo.chat.controller;

import org.example.dockerdemo.chat.dto.ChatReply;
import org.example.dockerdemo.chat.dto.ChatRequest;
import org.example.dockerdemo.chat.exception.DeepSeekException;
import org.example.dockerdemo.chat.service.DeepSeekService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ChatController(DeepSeekService deepSeekService) {
        this.deepSeekService = deepSeekService;
    }

    @PostMapping
    public ResponseEntity<ChatReply> chat(@RequestBody ChatRequest request) {
        try {
            String reply = deepSeekService.chat(request);
            return ResponseEntity.ok(new ChatReply(reply));
        } catch (DeepSeekException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, ex.getMessage());
        }
    }
}
