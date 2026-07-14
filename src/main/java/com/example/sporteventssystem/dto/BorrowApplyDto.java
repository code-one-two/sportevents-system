package com.example.sporteventssystem.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowApplyDto {

    @NotNull(message = "器材不能为空")
    private Long equipmentId;

    @NotNull(message = "借用数量不能为空")
    @Min(value = 1, message = "借用数量至少为1")
    private Integer quantity;

    private String applyReason;

    @NotNull(message = "预计归还时间不能为空")
    @Future(message = "预计归还时间必须晚于当前时间")
    private LocalDateTime expectedReturn;
}
