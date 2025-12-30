package org.example.dockerdemo.chat.repository;

import org.example.dockerdemo.chat.entity.ChatMessageEntity;
import org.example.dockerdemo.enums.DeleteFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 聊天消息Repository接口
 * 提供消息的数据访问操作
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, String> {

    /**
     * 按会话ID和删除标记查询消息列表，按序号正序排列
     * @param sessionId 会话ID
     * @param deleteFlag 删除标记
     * @return 消息列表
     */
    List<ChatMessageEntity> findBySessionIdAndDeleteFlagOrderBySequenceNumAsc(
            String sessionId, DeleteFlag deleteFlag);

    /**
     * 获取会话中最大的序号
     * @param sessionId 会话ID
     * @return 最大序号，如果没有消息则返回null
     */
    @Query("SELECT MAX(m.sequenceNum) FROM ChatMessageEntity m WHERE m.sessionId = :sessionId")
    Long findMaxSequenceNumBySessionId(@Param("sessionId") String sessionId);

    /**
     * 批量更新指定会话的所有消息的删除标记
     * @param sessionId 会话ID
     * @param deleteFlag 删除标记
     */
    @Modifying
    @Query("UPDATE ChatMessageEntity m SET m.deleteFlag = :deleteFlag WHERE m.sessionId = :sessionId")
    void updateDeleteFlagBySessionId(@Param("sessionId") String sessionId, 
                                      @Param("deleteFlag") DeleteFlag deleteFlag);
}
