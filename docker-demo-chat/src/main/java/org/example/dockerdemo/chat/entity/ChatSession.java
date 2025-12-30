package org.example.dockerdemo.chat.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dockerdemo.entity.base.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 聊天会话实体类
 * 代表一次完整的对话上下文
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "chat_session")
public class ChatSession extends BaseEntity {

    /**
     * 会话标题
     */
    @Column(name = "title", length = 100)
    private String title;
}
