package com.example.sporteventssystem.service;

import com.example.sporteventssystem.common.PageResult;
import com.example.sporteventssystem.dto.EquipmentSaveDto;
import com.example.sporteventssystem.dto.PageQueryDto;
import com.example.sporteventssystem.vo.EquipmentVo;

public interface EquipmentService {

    PageResult<EquipmentVo> page(PageQueryDto query);

    EquipmentVo getById(Long id);

    void save(EquipmentSaveDto dto);

    void delete(Long id);
}
