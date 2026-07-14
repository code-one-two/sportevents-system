package com.example.sporteventssystem.service.impl;

import com.example.sporteventssystem.common.PageResult;
import com.example.sporteventssystem.common.enums.EquipmentStatus;
import com.example.sporteventssystem.common.exception.BusinessException;
import com.example.sporteventssystem.dto.EquipmentSaveDto;
import com.example.sporteventssystem.dto.PageQueryDto;
import com.example.sporteventssystem.entity.Equipment;
import com.example.sporteventssystem.entity.EquipmentCategory;
import com.example.sporteventssystem.mapper.EquipmentCategoryMapper;
import com.example.sporteventssystem.mapper.EquipmentMapper;
import com.example.sporteventssystem.service.EquipmentService;
import com.example.sporteventssystem.vo.EquipmentVo;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentMapper equipmentMapper;
    private final EquipmentCategoryMapper equipmentCategoryMapper;

    @Override
    public PageResult<EquipmentVo> page(PageQueryDto query) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = "%" + query.getKeyword() + "%";
            wrapper.and("(equipment_code LIKE ? OR equipment_name LIKE ?)", keyword, keyword);
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.and("status = ?", query.getStatus());
        }
        if (query.getCategoryId() != null) {
            wrapper.and("category_id = ?", query.getCategoryId());
        }
        wrapper.orderBy("create_time", false);

        Page<Equipment> page = equipmentMapper.paginate(query.getPageNum(), query.getPageSize(), wrapper);
        Map<Long, String> categoryMap = equipmentCategoryMapper.selectAll().stream()
                .collect(Collectors.toMap(EquipmentCategory::getId, EquipmentCategory::getCategoryName));
        List<EquipmentVo> records = page.getRecords().stream()
                .map(e -> toVo(e, categoryMap.get(e.getCategoryId())))
                .collect(Collectors.toList());
        return PageResult.of(page.getTotalRow(), query.getPageNum(), query.getPageSize(), records);
    }

    @Override
    public EquipmentVo getById(Long id) {
        Equipment equipment = equipmentMapper.selectOneById(id);
        if (equipment == null) {
            throw new BusinessException("器材不存在");
        }
        EquipmentCategory category = equipmentCategoryMapper.selectOneById(equipment.getCategoryId());
        return toVo(equipment, category != null ? category.getCategoryName() : null);
    }

    @Override
    public void save(EquipmentSaveDto dto) {
        if (dto.getId() == null) {
            long count = equipmentMapper.selectCountByQuery(
                    QueryWrapper.create().where("equipment_code = ?", dto.getEquipmentCode()));
            if (count > 0) {
                throw new BusinessException("器材编码已存在");
            }
            Equipment equipment = new Equipment();
            copyDto(dto, equipment);
            equipment.setStatus(StringUtils.hasText(dto.getStatus()) ? dto.getStatus() : EquipmentStatus.NORMAL.name());
            equipment.setAvailableQuantity(dto.getTotalQuantity());
            equipmentMapper.insert(equipment);
        } else {
            Equipment equipment = equipmentMapper.selectOneById(dto.getId());
            if (equipment == null) {
                throw new BusinessException("器材不存在");
            }
            int borrowed = equipment.getTotalQuantity() - equipment.getAvailableQuantity();
            copyDto(dto, equipment);
            if (dto.getTotalQuantity() < borrowed) {
                throw new BusinessException("总数量不能小于已借出数量");
            }
            equipment.setAvailableQuantity(dto.getTotalQuantity() - borrowed);
            if (StringUtils.hasText(dto.getStatus())) {
                equipment.setStatus(dto.getStatus());
            }
            equipmentMapper.update(equipment);
        }
    }

    @Override
    public void delete(Long id) {
        equipmentMapper.deleteById(id);
    }

    private void copyDto(EquipmentSaveDto dto, Equipment equipment) {
        equipment.setEquipmentCode(dto.getEquipmentCode());
        equipment.setEquipmentName(dto.getEquipmentName());
        equipment.setCategoryId(dto.getCategoryId());
        equipment.setTotalQuantity(dto.getTotalQuantity());
        equipment.setLocation(dto.getLocation());
        equipment.setDescription(dto.getDescription());
    }

    private EquipmentVo toVo(Equipment equipment, String categoryName) {
        EquipmentVo vo = new EquipmentVo();
        vo.setId(equipment.getId());
        vo.setEquipmentCode(equipment.getEquipmentCode());
        vo.setEquipmentName(equipment.getEquipmentName());
        vo.setCategoryId(equipment.getCategoryId());
        vo.setCategoryName(categoryName);
        vo.setStatus(equipment.getStatus());
        vo.setTotalQuantity(equipment.getTotalQuantity());
        vo.setAvailableQuantity(equipment.getAvailableQuantity());
        vo.setLocation(equipment.getLocation());
        vo.setDescription(equipment.getDescription());
        vo.setCreateTime(equipment.getCreateTime());
        return vo;
    }
}
