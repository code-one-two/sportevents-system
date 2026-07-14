package com.example.sporteventssystem.service;

import com.example.sporteventssystem.common.PageResult;
import com.example.sporteventssystem.dto.BorrowApplyDto;
import com.example.sporteventssystem.dto.BorrowApproveDto;
import com.example.sporteventssystem.dto.PageQueryDto;
import com.example.sporteventssystem.vo.BorrowRecordVo;

public interface BorrowRecordService {

    PageResult<BorrowRecordVo> page(PageQueryDto query);

    void apply(BorrowApplyDto dto);

    void approve(BorrowApproveDto dto);

    void returnEquipment(Long id);

    void checkOverdue();
}
