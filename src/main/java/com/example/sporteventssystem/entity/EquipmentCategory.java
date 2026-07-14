package com.example.sporteventssystem.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table("equipment_category")
public class EquipmentCategory {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String categoryName;
    private String description;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
