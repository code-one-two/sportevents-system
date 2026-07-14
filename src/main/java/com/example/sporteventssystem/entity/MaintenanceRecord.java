package com.example.sporteventssystem.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table("maintenance_record")
public class MaintenanceRecord {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String maintenanceNo;
    private Long equipmentId;
    private Long reporterId;
    private Long handlerId;
    private String status;
    private String faultDesc;
    private String repairDesc;
    private BigDecimal cost;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private LocalDateTime archiveTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
