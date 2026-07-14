package com.example.sporteventssystem.dto;

import lombok.Data;

@Data
public class PageQueryDto {

    private long pageNum = 1;
    private long pageSize = 10;
    private String keyword;
    private String status;
    private Long categoryId;
    private Long userId;
    private Long equipmentId;
}
