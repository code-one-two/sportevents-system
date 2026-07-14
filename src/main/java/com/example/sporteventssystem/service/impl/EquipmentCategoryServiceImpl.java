package com.example.sporteventssystem.service.impl;

import com.example.sporteventssystem.common.exception.BusinessException;
import com.example.sporteventssystem.dto.CategorySaveDto;
import com.example.sporteventssystem.entity.EquipmentCategory;
import com.example.sporteventssystem.mapper.EquipmentCategoryMapper;
import com.example.sporteventssystem.service.EquipmentCategoryService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentCategoryServiceImpl implements EquipmentCategoryService {

    private final EquipmentCategoryMapper equipmentCategoryMapper;

    @Override
    public List<EquipmentCategory> listAll() {
        return equipmentCategoryMapper.selectListByQuery(
                QueryWrapper.create().orderBy("sort_order", true));
    }

    @Override
    public void save(CategorySaveDto dto) {
        if (dto.getId() == null) {
            EquipmentCategory category = new EquipmentCategory();
            copyDto(dto, category);
            equipmentCategoryMapper.insert(category);
        } else {
            EquipmentCategory category = equipmentCategoryMapper.selectOneById(dto.getId());
            if (category == null) {
                throw new BusinessException("分类不存在");
            }
            copyDto(dto, category);
            equipmentCategoryMapper.update(category);
        }
    }

    @Override
    public void delete(Long id) {
        equipmentCategoryMapper.deleteById(id);
    }

    private void copyDto(CategorySaveDto dto, EquipmentCategory category) {
        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
    }
}
