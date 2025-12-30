-- 添加消息序号字段，用于确保消息排序稳定
ALTER TABLE chat_message ADD COLUMN sequence_num BIGINT COMMENT '消息在会话中的序号';

-- 为现有消息设置序号（按创建时间排序）
SET @row_number = 0;
SET @current_session = '';

UPDATE chat_message m
JOIN (
    SELECT id, session_id,
           @row_number := IF(@current_session = session_id, @row_number + 1, 1) AS seq,
           @current_session := session_id
    FROM chat_message
    ORDER BY session_id, create_time, id
) AS ranked ON m.id = ranked.id
SET m.sequence_num = ranked.seq;

-- 添加索引
CREATE INDEX idx_session_seq ON chat_message (session_id, sequence_num);
