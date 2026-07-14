package com.example.sporteventssystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EquipmentSaveDto {

    private Long id;

    @NotBlank(message = "器材编码不能为空")
    private String equipmentCode;

    @NotBlank(message = "器材名称不能为空")
    private String equipmentName;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private String status;

    @NotNull(message = "总数量不能为空")
    @Min(value = 0, message = "总数量不能小于0")
    private Integer totalQuantity;

    private String location;
    private String description;
}
