package com.example.sporteventssystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategorySaveDto {

    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    private String description;
    private Integer sortOrder;
}
