package com.example.sporteventssystem.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DocumentAssistantRequest {

    @Size(max = 50000, message = "文档内容不能超过50000个字符")
    private String documentContent;

    @NotBlank(message = "请输入问题")
    @Size(max = 4000, message = "问题不能超过4000个字符")
    private String question;

    @Valid
    @Size(max = 20, message = "最多携带20条历史消息")
    private List<AiMessageDto> history = new ArrayList<>();
}
