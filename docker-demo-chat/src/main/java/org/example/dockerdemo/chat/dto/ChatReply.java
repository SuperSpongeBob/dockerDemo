package org.example.dockerdemo.chat.dto;

/**
 * 聊天响应 DTO
 */
public class ChatReply {
    private String reply;
    private String sessionId;

    public ChatReply() {
    }

    public ChatReply(String reply) {
        this.reply = reply;
    }

    public ChatReply(String reply, String sessionId) {
        this.reply = reply;
        this.sessionId = sessionId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
