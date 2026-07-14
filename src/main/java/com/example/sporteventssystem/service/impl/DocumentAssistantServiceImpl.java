package com.example.sporteventssystem.service.impl;

import com.example.sporteventssystem.common.exception.BusinessException;
import com.example.sporteventssystem.config.DeepSeekProperties;
import com.example.sporteventssystem.dto.AiMessageDto;
import com.example.sporteventssystem.dto.DocumentAssistantRequest;
import com.example.sporteventssystem.service.DocumentAssistantService;
import com.example.sporteventssystem.vo.DocumentAssistantVo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentAssistantServiceImpl implements DocumentAssistantService {

    private static final String SYSTEM_PROMPT = """
            你是体育馆器材管理系统中的文档助手。请优先依据用户提供的文档内容回答，
            用清晰、准确的中文表达；如果文档没有包含答案，要明确说明依据不足，
            不要编造。可以协助总结、提取要点、润色、生成制度或表格化清单。
            """;

    private final DeepSeekProperties properties;

    @Override
    public DocumentAssistantVo ask(DocumentAssistantRequest request) {
        if (!StringUtils.hasText(properties.getApiKey())) {
            throw new BusinessException("DeepSeek API Key 未配置，请先配置 DEEPSEEK_API_KEY");
        }

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(message("system", buildSystemPrompt(request.getDocumentContent())));
        if (request.getHistory() != null) {
            request.getHistory().stream()
                    .filter(item -> item != null && StringUtils.hasText(item.getContent()))
                    .map(this::toMessage)
                    .forEach(messages::add);
        }
        messages.add(message("user", request.getQuestion().trim()));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", properties.getModel());
        body.put("messages", messages);
        body.put("stream", false);
        body.put("temperature", 0.3);

        try {
            String baseUrl = properties.getBaseUrl().replaceAll("/+$", "");
            JsonNode response = RestClient.create()
                    .post()
                    .uri(baseUrl + "/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

            String answer = response == null ? null
                    : response.path("choices").path(0).path("message").path("content").asText(null);
            if (!StringUtils.hasText(answer)) {
                throw new BusinessException("DeepSeek 未返回有效内容，请稍后重试");
            }
            String model = response.path("model").asText(properties.getModel());
            return new DocumentAssistantVo(answer, model);
        } catch (RestClientResponseException e) {
            log.error("DeepSeek API 调用失败，HTTP {}", e.getStatusCode().value());
            throw new BusinessException("DeepSeek 调用失败，请检查 API Key、模型配置或账户余额");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("DeepSeek API 调用异常", e);
            throw new BusinessException("DeepSeek 服务暂时不可用，请稍后重试");
        }
    }

    private String buildSystemPrompt(String documentContent) {
        if (!StringUtils.hasText(documentContent)) {
            return SYSTEM_PROMPT + "\n当前没有提供文档，请作为通用文档写作助手回答。";
        }
        return SYSTEM_PROMPT + "\n\n以下是需要参考的文档：\n---\n"
                + documentContent.trim() + "\n---";
    }

    private Map<String, String> toMessage(AiMessageDto item) {
        return message(item.getRole(), item.getContent());
    }

    private Map<String, String> message(String role, String content) {
        return Map.of("role", role, "content", content);
    }
}
