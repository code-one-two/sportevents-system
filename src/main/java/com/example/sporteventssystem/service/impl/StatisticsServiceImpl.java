package com.example.sporteventssystem.service.impl;

import com.example.sporteventssystem.common.enums.BorrowStatus;
import com.example.sporteventssystem.common.enums.MaintenanceStatus;
import com.example.sporteventssystem.entity.BorrowRecord;
import com.example.sporteventssystem.entity.Equipment;
import com.example.sporteventssystem.mapper.BorrowRecordMapper;
import com.example.sporteventssystem.mapper.EquipmentMapper;
import com.example.sporteventssystem.mapper.MaintenanceRecordMapper;
import com.example.sporteventssystem.service.StatisticsService;
import com.example.sporteventssystem.vo.StatisticsVo;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final EquipmentMapper equipmentMapper;
    private final BorrowRecordMapper borrowRecordMapper;
    private final MaintenanceRecordMapper maintenanceRecordMapper;

    @Override
    public StatisticsVo getDashboard() {
        StatisticsVo vo = new StatisticsVo();
        vo.setTotalEquipment(equipmentMapper.selectCountByQuery(QueryWrapper.create()));
        vo.setTotalBorrow(borrowRecordMapper.selectCountByQuery(QueryWrapper.create()));
        vo.setOverdueCount(borrowRecordMapper.selectCountByQuery(
                QueryWrapper.create().where("status = ?", BorrowStatus.OVERDUE.name())));
        vo.setMaintenanceCount(maintenanceRecordMapper.selectCountByQuery(
                QueryWrapper.create().where("status != ?", MaintenanceStatus.ARCHIVED.name())));

        List<Equipment> equipments = equipmentMapper.selectAll();
        List<Map<String, Object>> usage = new ArrayList<>();
        for (Equipment e : equipments) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", e.getEquipmentName());
            int used = e.getTotalQuantity() - e.getAvailableQuantity();
            double rate = e.getTotalQuantity() == 0 ? 0 :
                    (double) used / e.getTotalQuantity() * 100;
            item.put("usageRate", Math.round(rate * 100.0) / 100.0);
            item.put("borrowCount", used);
            usage.add(item);
        }
        vo.setEquipmentUsage(usage);

        List<BorrowRecord> records = borrowRecordMapper.selectAll();
        Map<String, Long> trendMap = new HashMap<>();
        for (BorrowRecord r : records) {
            if (r.getCreateTime() != null) {
                String month = r.getCreateTime().getMonthValue() + "月";
                trendMap.merge(month, 1L, Long::sum);
            }
        }
        List<Map<String, Object>> trend = new ArrayList<>();
        trendMap.forEach((k, v) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("month", k);
            item.put("count", v);
            trend.add(item);
        });
        vo.setBorrowTrend(trend);
        return vo;
    }
}
