package org.example.dockerdemo.chat.dto;

import java.util.List;

/**
 * 聊天请求 DTO
 */
public class ChatRequest {
    /**
     * 会话ID，可选。如果提供则关联到已有会话，否则创建新会话
     */
    private String sessionId;
    private String message;
    private String systemPrompt;
    private String background;
    private List<ChatMessage> history;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public List<ChatMessage> getHistory() {
        return history;
    }

    public void setHistory(List<ChatMessage> history) {
        this.history = history;
    }
}
