package com.example.sporteventssystem.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowRecordVo {

    private Long id;
    private String borrowNo;
    private Long userId;
    private String username;
    private String realName;
    private Long equipmentId;
    private String equipmentName;
    private Integer quantity;
    private String status;
    private String applyReason;
    private LocalDateTime expectedReturn;
    private LocalDateTime actualReturn;
    private String approverName;
    private LocalDateTime approveTime;
    private String approveRemark;
    private Integer overdueRemind;
    private LocalDateTime createTime;
}
