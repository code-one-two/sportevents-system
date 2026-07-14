package com.example.sporteventssystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaintenanceSaveDto {

    private Long id;

    @NotNull(message = "器材不能为空")
    private Long equipmentId;

    @NotBlank(message = "故障描述不能为空")
    private String faultDesc;

    private String repairDesc;
    private BigDecimal cost;
    private String status;
}
