package com.example.sporteventssystem.service;

import com.example.sporteventssystem.dto.CategorySaveDto;
import com.example.sporteventssystem.entity.EquipmentCategory;

import java.util.List;

public interface EquipmentCategoryService {

    List<EquipmentCategory> listAll();

    void save(CategorySaveDto dto);

    void delete(Long id);
}
