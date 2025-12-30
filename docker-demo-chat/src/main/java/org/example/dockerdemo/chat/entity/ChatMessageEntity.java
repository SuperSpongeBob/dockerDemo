package org.example.dockerdemo.chat.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dockerdemo.entity.base.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * 聊天消息实体类
 * 代表会话中的单条消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "chat_message", indexes = {
    @Index(name = "idx_session_id", columnList = "session_id"),
    @Index(name = "idx_create_time", columnList = "create_time"),
    @Index(name = "idx_session_seq", columnList = "session_id, sequence_num")
})
public class ChatMessageEntity extends BaseEntity {

    /**
     * 关联的会话ID
     */
    @Column(name = "session_id", length = 36, nullable = false)
    private String sessionId;

    /**
     * 角色: user/assistant/system
     */
    @Column(name = "role", length = 20, nullable = false)
    private String role;

    /**
     * 消息内容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * 消息在会话中的序号，用于确保排序稳定
     */
    @Column(name = "sequence_num")
    private Long sequenceNum;
}
