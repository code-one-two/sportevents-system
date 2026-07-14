package com.example.sporteventssystem.service;

import com.example.sporteventssystem.dto.DocumentAssistantRequest;
import com.example.sporteventssystem.vo.DocumentAssistantVo;

public interface DocumentAssistantService {

    DocumentAssistantVo ask(DocumentAssistantRequest request);
}
