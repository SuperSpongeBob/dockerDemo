package org.example.dockerdemo.chat.dto;

import java.time.LocalDateTime;

/**
 * 消息DTO
 * 用于API响应中返回消息信息
 */
public class MessageDTO {
    private String id;
    private String role;
    private String content;
    private LocalDateTime createTime;

    public MessageDTO() {
    }

    public MessageDTO(String id, String role, String content, LocalDateTime createTime) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
