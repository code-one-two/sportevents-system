package com.example.sporteventssystem.service.impl;

import com.example.sporteventssystem.common.PageResult;
import com.example.sporteventssystem.common.enums.BorrowStatus;
import com.example.sporteventssystem.common.enums.EquipmentStatus;
import com.example.sporteventssystem.common.exception.BusinessException;
import com.example.sporteventssystem.dto.BorrowApplyDto;
import com.example.sporteventssystem.dto.BorrowApproveDto;
import com.example.sporteventssystem.dto.PageQueryDto;
import com.example.sporteventssystem.entity.BorrowRecord;
import com.example.sporteventssystem.entity.Equipment;
import com.example.sporteventssystem.entity.SysUser;
import com.example.sporteventssystem.mapper.BorrowRecordMapper;
import com.example.sporteventssystem.mapper.EquipmentMapper;
import com.example.sporteventssystem.mapper.SysUserMapper;
import com.example.sporteventssystem.security.SecurityUtils;
import com.example.sporteventssystem.service.BorrowRecordService;
import com.example.sporteventssystem.vo.BorrowRecordVo;
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
public class BorrowRecordServiceImpl implements BorrowRecordService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final EquipmentMapper equipmentMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public PageResult<BorrowRecordVo> page(PageQueryDto query) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.and("status = ?", query.getStatus());
        }
        if (!SecurityUtils.hasAuthority("borrow:manage")) {
            wrapper.and("user_id = ?", SecurityUtils.getCurrentUserId());
        } else if (query.getUserId() != null) {
            wrapper.and("user_id = ?", query.getUserId());
        }
        if (query.getEquipmentId() != null) {
            wrapper.and("equipment_id = ?", query.getEquipmentId());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and("borrow_no LIKE ?", "%" + query.getKeyword() + "%");
        }
        wrapper.orderBy("create_time", false);

        Page<BorrowRecord> page = borrowRecordMapper.paginate(query.getPageNum(), query.getPageSize(), wrapper);
        Map<Long, SysUser> userMap = sysUserMapper.selectAll().stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));
        Map<Long, Equipment> equipmentMap = equipmentMapper.selectAll().stream()
                .collect(Collectors.toMap(Equipment::getId, e -> e));

        List<BorrowRecordVo> records = page.getRecords().stream()
                .map(r -> toVo(r, userMap, equipmentMap))
                .collect(Collectors.toList());
        return PageResult.of(page.getTotalRow(), query.getPageNum(), query.getPageSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(BorrowApplyDto dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        Equipment equipment = equipmentMapper.selectOneById(dto.getEquipmentId());
        if (equipment == null) {
            throw new BusinessException("器材不存在");
        }
        if (!EquipmentStatus.NORMAL.name().equals(equipment.getStatus())
                && !EquipmentStatus.BORROWED.name().equals(equipment.getStatus())) {
            throw new BusinessException("该器材当前不可借用");
        }
        if (equipment.getAvailableQuantity() < dto.getQuantity()) {
            throw new BusinessException("库存不足");
        }

        BorrowRecord record = new BorrowRecord();
        record.setBorrowNo(generateBorrowNo());
        record.setUserId(userId);
        record.setEquipmentId(dto.getEquipmentId());
        record.setQuantity(dto.getQuantity());
        record.setStatus(BorrowStatus.PENDING.name());
        record.setApplyReason(dto.getApplyReason());
        record.setExpectedReturn(dto.getExpectedReturn());
        record.setOverdueRemind(0);
        borrowRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(BorrowApproveDto dto) {
        BorrowRecord record = borrowRecordMapper.selectOneById(dto.getId());
        if (record == null) {
            throw new BusinessException("借用记录不存在");
        }
        if (!BorrowStatus.PENDING.name().equals(record.getStatus())) {
            throw new BusinessException("该记录已审批");
        }

        record.setApproverId(SecurityUtils.getCurrentUserId());
        record.setApproveTime(LocalDateTime.now());
        record.setApproveRemark(dto.getApproveRemark());

        if (Boolean.TRUE.equals(dto.getApproved())) {
            Equipment equipment = equipmentMapper.selectOneById(record.getEquipmentId());
            if (equipment.getAvailableQuantity() < record.getQuantity()) {
                throw new BusinessException("库存不足，无法批准");
            }
            equipment.setAvailableQuantity(equipment.getAvailableQuantity() - record.getQuantity());
            if (equipment.getAvailableQuantity() == 0) {
                equipment.setStatus(EquipmentStatus.BORROWED.name());
            }
            equipmentMapper.update(equipment);
            record.setStatus(BorrowStatus.BORROWED.name());
        } else {
            record.setStatus(BorrowStatus.REJECTED.name());
        }
        borrowRecordMapper.update(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnEquipment(Long id) {
        BorrowRecord record = borrowRecordMapper.selectOneById(id);
        if (record == null) {
            throw new BusinessException("借用记录不存在");
        }
        if (!BorrowStatus.BORROWED.name().equals(record.getStatus())
                && !BorrowStatus.OVERDUE.name().equals(record.getStatus())) {
            throw new BusinessException("当前状态不可归还");
        }

        Equipment equipment = equipmentMapper.selectOneById(record.getEquipmentId());
        equipment.setAvailableQuantity(equipment.getAvailableQuantity() + record.getQuantity());
        equipment.setStatus(EquipmentStatus.NORMAL.name());
        equipmentMapper.update(equipment);

        record.setStatus(BorrowStatus.RETURNED.name());
        record.setActualReturn(LocalDateTime.now());
        borrowRecordMapper.update(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkOverdue() {
        List<BorrowRecord> overdueList = borrowRecordMapper.selectListByQuery(
                QueryWrapper.create()
                        .where("status = ?", BorrowStatus.BORROWED.name())
                        .and("expected_return < ?", LocalDateTime.now()));
        for (BorrowRecord record : overdueList) {
            record.setStatus(BorrowStatus.OVERDUE.name());
            record.setOverdueRemind(1);
            borrowRecordMapper.update(record);
        }
    }

    private String generateBorrowNo() {
        return "BR" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    private BorrowRecordVo toVo(BorrowRecord record, Map<Long, SysUser> userMap, Map<Long, Equipment> equipmentMap) {
        BorrowRecordVo vo = new BorrowRecordVo();
        vo.setId(record.getId());
        vo.setBorrowNo(record.getBorrowNo());
        vo.setUserId(record.getUserId());
        SysUser user = userMap.get(record.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setRealName(user.getRealName());
        }
        vo.setEquipmentId(record.getEquipmentId());
        Equipment equipment = equipmentMap.get(record.getEquipmentId());
        if (equipment != null) {
            vo.setEquipmentName(equipment.getEquipmentName());
        }
        vo.setQuantity(record.getQuantity());
        vo.setStatus(record.getStatus());
        vo.setApplyReason(record.getApplyReason());
        vo.setExpectedReturn(record.getExpectedReturn());
        vo.setActualReturn(record.getActualReturn());
        if (record.getApproverId() != null) {
            SysUser approver = userMap.get(record.getApproverId());
            if (approver != null) {
                vo.setApproverName(approver.getRealName());
            }
        }
        vo.setApproveTime(record.getApproveTime());
        vo.setApproveRemark(record.getApproveRemark());
        vo.setOverdueRemind(record.getOverdueRemind());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }
}
