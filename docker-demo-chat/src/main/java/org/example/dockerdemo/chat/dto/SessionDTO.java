package org.example.dockerdemo.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会话DTO
 * 用于API响应中返回会话信息
 */
public class SessionDTO {
    private String id;
    private String title;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<MessageDTO> messages;

    public SessionDTO() {
    }

    public SessionDTO(String id, String title, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.title = title;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public SessionDTO(String id, String title, LocalDateTime createTime, LocalDateTime updateTime, List<MessageDTO> messages) {
        this.id = id;
        this.title = title;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.messages = messages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}
