package com.example.sporteventssystem.service.impl;

import com.example.sporteventssystem.common.PageResult;
import com.example.sporteventssystem.common.enums.EquipmentStatus;
import com.example.sporteventssystem.common.enums.MaintenanceStatus;
import com.example.sporteventssystem.common.exception.BusinessException;
import com.example.sporteventssystem.dto.MaintenanceSaveDto;
import com.example.sporteventssystem.dto.PageQueryDto;
import com.example.sporteventssystem.entity.Equipment;
import com.example.sporteventssystem.entity.MaintenanceRecord;
import com.example.sporteventssystem.entity.SysUser;
import com.example.sporteventssystem.mapper.EquipmentMapper;
import com.example.sporteventssystem.mapper.MaintenanceRecordMapper;
import com.example.sporteventssystem.mapper.SysUserMapper;
import com.example.sporteventssystem.security.SecurityUtils;
import com.example.sporteventssystem.service.MaintenanceRecordService;
import com.example.sporteventssystem.vo.MaintenanceRecordVo;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceRecordServiceImpl implements MaintenanceRecordService {

    private final MaintenanceRecordMapper maintenanceRecordMapper;
    private final EquipmentMapper equipmentMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public PageResult<MaintenanceRecordVo> page(PageQueryDto query) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.and("status = ?", query.getStatus());
        }
        if (query.getEquipmentId() != null) {
            wrapper.and("equipment_id = ?", query.getEquipmentId());
        }
        wrapper.orderBy("create_time", false);

        Page<MaintenanceRecord> page = maintenanceRecordMapper.paginate(query.getPageNum(), query.getPageSize(), wrapper);
        Map<Long, SysUser> userMap = sysUserMapper.selectAll().stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));
        Map<Long, Equipment> equipmentMap = equipmentMapper.selectAll().stream()
                .collect(Collectors.toMap(Equipment::getId, e -> e));

        List<MaintenanceRecordVo> records = page.getRecords().stream()
                .map(r -> toVo(r, userMap, equipmentMap))
                .collect(Collectors.toList());
        return PageResult.of(page.getTotalRow(), query.getPageNum(), query.getPageSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(MaintenanceSaveDto dto) {
        if (dto.getId() == null) {
            Equipment equipment = equipmentMapper.selectOneById(dto.getEquipmentId());
            if (equipment == null) {
                throw new BusinessException("器材不存在");
            }
            MaintenanceRecord record = new MaintenanceRecord();
            record.setMaintenanceNo(generateNo());
            record.setEquipmentId(dto.getEquipmentId());
            record.setReporterId(SecurityUtils.getCurrentUserId());
            record.setStatus(MaintenanceStatus.PENDING.name());
            record.setFaultDesc(dto.getFaultDesc());
            record.setCost(dto.getCost());
            maintenanceRecordMapper.insert(record);

            equipment.setStatus(EquipmentStatus.REPAIR.name());
            equipmentMapper.update(equipment);
        } else {
            MaintenanceRecord record = maintenanceRecordMapper.selectOneById(dto.getId());
            if (record == null) {
                throw new BusinessException("维修记录不存在");
            }
            record.setFaultDesc(dto.getFaultDesc());
            record.setRepairDesc(dto.getRepairDesc());
            record.setCost(dto.getCost());
            maintenanceRecordMapper.update(record);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(Long id) {
        MaintenanceRecord record = maintenanceRecordMapper.selectOneById(id);
        if (record == null) {
            throw new BusinessException("维修记录不存在");
        }
        if (!MaintenanceStatus.PENDING.name().equals(record.getStatus())) {
            throw new BusinessException("只有待处理的维修记录可以开始");
        }
        record.setStatus(MaintenanceStatus.IN_PROGRESS.name());
        record.setHandlerId(SecurityUtils.getCurrentUserId());
        record.setStartTime(LocalDateTime.now());
        maintenanceRecordMapper.update(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id, MaintenanceSaveDto dto) {
        MaintenanceRecord record = maintenanceRecordMapper.selectOneById(id);
        if (record == null) {
            throw new BusinessException("维修记录不存在");
        }
        if (!MaintenanceStatus.IN_PROGRESS.name().equals(record.getStatus())) {
            throw new BusinessException("只有维修中的记录可以完成");
        }
        record.setStatus(MaintenanceStatus.COMPLETED.name());
        record.setRepairDesc(dto.getRepairDesc());
        record.setCost(dto.getCost());
        record.setFinishTime(LocalDateTime.now());
        maintenanceRecordMapper.update(record);

        Equipment equipment = equipmentMapper.selectOneById(record.getEquipmentId());
        equipment.setStatus(EquipmentStatus.NORMAL.name());
        equipmentMapper.update(equipment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archive(Long id) {
        MaintenanceRecord record = maintenanceRecordMapper.selectOneById(id);
        if (record == null) {
            throw new BusinessException("维修记录不存在");
        }
        if (!MaintenanceStatus.COMPLETED.name().equals(record.getStatus())) {
            throw new BusinessException("仅已完成记录可归档");
        }
        record.setStatus(MaintenanceStatus.ARCHIVED.name());
        record.setArchiveTime(LocalDateTime.now());
        maintenanceRecordMapper.update(record);
    }

    private String generateNo() {
        return "MR" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    private MaintenanceRecordVo toVo(MaintenanceRecord record, Map<Long, SysUser> userMap,
                                     Map<Long, Equipment> equipmentMap) {
        MaintenanceRecordVo vo = new MaintenanceRecordVo();
        vo.setId(record.getId());
        vo.setMaintenanceNo(record.getMaintenanceNo());
        vo.setEquipmentId(record.getEquipmentId());
        Equipment equipment = equipmentMap.get(record.getEquipmentId());
        if (equipment != null) {
            vo.setEquipmentName(equipment.getEquipmentName());
        }
        vo.setReporterId(record.getReporterId());
        SysUser reporter = userMap.get(record.getReporterId());
        if (reporter != null) {
            vo.setReporterName(reporter.getRealName());
        }
        vo.setHandlerId(record.getHandlerId());
        if (record.getHandlerId() != null) {
            SysUser handler = userMap.get(record.getHandlerId());
            if (handler != null) {
                vo.setHandlerName(handler.getRealName());
            }
        }
        vo.setStatus(record.getStatus());
        vo.setFaultDesc(record.getFaultDesc());
        vo.setRepairDesc(record.getRepairDesc());
        vo.setCost(record.getCost());
        vo.setStartTime(record.getStartTime());
        vo.setFinishTime(record.getFinishTime());
        vo.setArchiveTime(record.getArchiveTime());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }
}
