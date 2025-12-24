package org.example.dockerdemo.chat.exception;

/**
 * DeepSeek API 调用异常
 */
public class DeepSeekException extends RuntimeException {

    public DeepSeekException(String message) {
        super(message);
    }

    public DeepSeekException(String message, Throwable cause) {
        super(message, cause);
    }
}
