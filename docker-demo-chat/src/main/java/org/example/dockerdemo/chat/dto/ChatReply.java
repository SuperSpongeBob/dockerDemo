package org.example.dockerdemo.chat.dto;

/**
 * 聊天响应 DTO
 */
public class ChatReply {
    private String reply;

    public ChatReply() {
    }

    public ChatReply(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
