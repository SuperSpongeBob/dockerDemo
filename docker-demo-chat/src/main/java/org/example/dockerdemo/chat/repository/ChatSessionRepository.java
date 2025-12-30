package org.example.dockerdemo.chat.repository;

import org.example.dockerdemo.chat.entity.ChatSession;
import org.example.dockerdemo.enums.DeleteFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 聊天会话Repository接口
 * 提供会话的数据访问操作
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, String> {

    /**
     * 按删除标记查询会话列表，按更新时间倒序排列
     * @param deleteFlag 删除标记
     * @return 会话列表
     */
    List<ChatSession> findByDeleteFlagOrderByUpdateTimeDesc(DeleteFlag deleteFlag);

    /**
     * 按ID和删除标记查询会话
     * @param id 会话ID
     * @param deleteFlag 删除标记
     * @return 会话Optional
     */
    Optional<ChatSession> findByIdAndDeleteFlag(String id, DeleteFlag deleteFlag);
}
