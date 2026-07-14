package com.example.sporteventssystem.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StatisticsVo {

    private long totalEquipment;
    private long totalBorrow;
    private long overdueCount;
    private long maintenanceCount;
    private List<Map<String, Object>> equipmentUsage;
    private List<Map<String, Object>> borrowTrend;
}
