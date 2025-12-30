package org.example.dockerdemo.chat.service;

import org.example.dockerdemo.chat.config.DeepSeekProperties;
import org.example.dockerdemo.chat.dto.ChatMessage;
import org.example.dockerdemo.chat.dto.ChatRequest;
import org.example.dockerdemo.chat.entity.ChatMessageEntity;
import org.example.dockerdemo.chat.exception.DeepSeekException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek API 服务
 */
@Service
public class DeepSeekService {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekService.class);
    private static final String DEEPSEEK_URL = "https://api.deepseek.com/chat/completions";
    
    /**
     * 默认加载的历史消息数量限制，用于控制token消耗
     */
    private static final int DEFAULT_HISTORY_LIMIT = 20;

    private final RestTemplate restTemplate;
    private final DeepSeekProperties properties;
    private final ChatMessageService messageService;

    public DeepSeekService(RestTemplate restTemplate, DeepSeekProperties properties,
                           ChatMessageService messageService) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.messageService = messageService;
    }

    public String chat(ChatRequest request) {
        validateRequest(request);
        HttpHeaders headers = buildHeaders();
        List<Map<String, String>> messages = buildMessages(request);

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "deepseek-chat");
        payload.put("stream", false);
        payload.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        Map<?, ?> response = callDeepSeekApi(entity);
        return extractReply(response);
    }

    private void validateRequest(ChatRequest request) {
        if (request == null || !StringUtils.hasText(request.getMessage())) {
            throw new DeepSeekException("message 不能为空");
        }
        if (!StringUtils.hasText(properties.getApi().getKey())) {
            throw new DeepSeekException("未配置 DeepSeek API Key");
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(properties.getApi().getKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private List<Map<String, String>> buildMessages(ChatRequest request) {
        List<Map<String, String>> messages = new ArrayList<>();

        String systemPrompt = StringUtils.hasText(request.getSystemPrompt())
                ? request.getSystemPrompt()
                : properties.getPrompt().getSystem();
        if (StringUtils.hasText(systemPrompt)) {
            messages.add(createMessage("system", systemPrompt));
        }

        String background = StringUtils.hasText(request.getBackground())
                ? request.getBackground()
                : properties.getPrompt().getBackground();
        if (StringUtils.hasText(background)) {
            messages.add(createMessage("system", "背景信息：" + background));
        }

        // 优先从数据库加载历史消息（如果提供了sessionId）
        if (StringUtils.hasText(request.getSessionId())) {
            List<ChatMessageEntity> historyFromDb = loadHistoryFromDatabase(request.getSessionId());
            for (ChatMessageEntity msg : historyFromDb) {
                messages.add(createMessage(normalizeRole(msg.getRole()), msg.getContent()));
            }
        } else if (request.getHistory() != null) {
            // 如果没有sessionId，则使用请求中传入的history
            for (ChatMessage msg : request.getHistory()) {
                if (msg != null && StringUtils.hasText(msg.getContent())) {
                    messages.add(createMessage(normalizeRole(msg.getRole()), msg.getContent()));
                }
            }
        }

        messages.add(createMessage("user", request.getMessage()));
        return messages;
    }

    /**
     * 从数据库加载会话的历史消息
     * 限制加载最近N条消息以控制token消耗
     * 
     * @param sessionId 会话ID
     * @return 历史消息列表，按时间正序排列
     */
    public List<ChatMessageEntity> loadHistoryFromDatabase(String sessionId) {
        return loadHistoryFromDatabase(sessionId, DEFAULT_HISTORY_LIMIT);
    }

    /**
     * 从数据库加载会话的历史消息
     * 
     * @param sessionId 会话ID
     * @param limit 最大消息数量
     * @return 历史消息列表，按时间正序排列
     */
    public List<ChatMessageEntity> loadHistoryFromDatabase(String sessionId, int limit) {
        if (!StringUtils.hasText(sessionId)) {
            return new ArrayList<>();
        }
        try {
            return messageService.getRecentMessages(sessionId, limit);
        } catch (Exception e) {
            log.warn("加载历史消息失败，sessionId={}, error={}", sessionId, e.getMessage());
            return new ArrayList<>();
        }
    }

    private Map<String, String> createMessage(String role, String content) {
        Map<String, String> msg = new HashMap<>(2);
        msg.put("role", role);
        msg.put("content", content);
        return msg;
    }

    private String normalizeRole(String role) {
        if (!StringUtils.hasText(role)) return "user";
        switch (role) {
            case "assistant":
            case "system":
            case "user":
                return role;
            default:
                return "user";
        }
    }

    private Map<?, ?> callDeepSeekApi(HttpEntity<Map<String, Object>> entity) {
        try {
            return restTemplate.postForObject(DEEPSEEK_URL, entity, Map.class);
        } catch (RestClientResponseException ex) {
            log.warn("DeepSeek 调用失败，status={} body={}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            throw new DeepSeekException("DeepSeek 调用失败：" + ex.getRawStatusCode() + " - " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            log.error("DeepSeek 调用异常", ex);
            throw new DeepSeekException("DeepSeek 调用异常：" + ex.getMessage());
        }
    }

    private String extractReply(Map<?, ?> response) {
        if (response == null) throw new DeepSeekException("DeepSeek 返回为空");
        Object choicesObj = response.get("choices");
        if (!(choicesObj instanceof List)) throw new DeepSeekException("DeepSeek 返回异常：缺少 choices");
        List<?> choices = (List<?>) choicesObj;
        if (choices.isEmpty()) throw new DeepSeekException("DeepSeek 返回为空");
        Object first = choices.get(0);
        if (!(first instanceof Map)) throw new DeepSeekException("DeepSeek 返回异常：choices 结构错误");
        Object messageObj = ((Map<?, ?>) first).get("message");
        if (!(messageObj instanceof Map)) throw new DeepSeekException("DeepSeek 返回异常：缺少 message");
        Object content = ((Map<?, ?>) messageObj).get("content");
        if (content == null) throw new DeepSeekException("DeepSeek 返回异常：缺少 content");
        return content.toString();
    }
}
