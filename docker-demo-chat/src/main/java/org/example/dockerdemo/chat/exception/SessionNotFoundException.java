package org.example.dockerdemo.chat.exception;

/**
 * 会话未找到异常
 * 当请求的会话不存在或已被删除时抛出
 */
public class SessionNotFoundException extends RuntimeException {

    private final String sessionId;

    public SessionNotFoundException(String sessionId) {
        super("会话不存在: " + sessionId);
        this.sessionId = sessionId;
    }

    public SessionNotFoundException(String sessionId, String message) {
        super(message);
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
