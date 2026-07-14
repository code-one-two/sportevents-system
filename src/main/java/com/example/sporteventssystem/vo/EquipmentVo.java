package com.example.sporteventssystem.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EquipmentVo {

    private Long id;
    private String equipmentCode;
    private String equipmentName;
    private Long categoryId;
    private String categoryName;
    private String status;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private String location;
    private String description;
    private LocalDateTime createTime;
}
