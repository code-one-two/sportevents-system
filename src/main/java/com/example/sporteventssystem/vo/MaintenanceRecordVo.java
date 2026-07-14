package com.example.sporteventssystem.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MaintenanceRecordVo {

    private Long id;
    private String maintenanceNo;
    private Long equipmentId;
    private String equipmentName;
    private Long reporterId;
    private String reporterName;
    private Long handlerId;
    private String handlerName;
    private String status;
    private String faultDesc;
    private String repairDesc;
    private BigDecimal cost;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private LocalDateTime archiveTime;
    private LocalDateTime createTime;
}
