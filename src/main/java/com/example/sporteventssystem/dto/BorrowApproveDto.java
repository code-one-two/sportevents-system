package com.example.sporteventssystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowApproveDto {

    @NotNull(message = "借用记录ID不能为空")
    private Long id;

    @NotNull(message = "审批结果不能为空")
    private Boolean approved;

    private String approveRemark;
}
