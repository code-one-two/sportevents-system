package com.example.sporteventssystem.service;

import com.example.sporteventssystem.common.PageResult;
import com.example.sporteventssystem.dto.MaintenanceSaveDto;
import com.example.sporteventssystem.dto.PageQueryDto;
import com.example.sporteventssystem.vo.MaintenanceRecordVo;

public interface MaintenanceRecordService {

    PageResult<MaintenanceRecordVo> page(PageQueryDto query);

    void save(MaintenanceSaveDto dto);

    void start(Long id);

    void complete(Long id, MaintenanceSaveDto dto);

    void archive(Long id);
}
