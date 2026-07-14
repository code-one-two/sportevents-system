package com.example.sporteventssystem.controller;

import com.example.sporteventssystem.common.Result;
import com.example.sporteventssystem.dto.DocumentAssistantRequest;
import com.example.sporteventssystem.service.DocumentAssistantService;
import com.example.sporteventssystem.vo.DocumentAssistantVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/document-assistant")
@RequiredArgsConstructor
public class DocumentAssistantController {

    private final DocumentAssistantService documentAssistantService;

    @PostMapping
    public Result<DocumentAssistantVo> ask(@Valid @RequestBody DocumentAssistantRequest request) {
        return Result.success(documentAssistantService.ask(request));
    }
}
