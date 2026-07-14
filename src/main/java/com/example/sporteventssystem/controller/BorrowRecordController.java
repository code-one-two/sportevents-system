package com.example.sporteventssystem.controller;

import com.example.sporteventssystem.common.PageResult;
import com.example.sporteventssystem.common.Result;
import com.example.sporteventssystem.dto.BorrowApplyDto;
import com.example.sporteventssystem.dto.BorrowApproveDto;
import com.example.sporteventssystem.dto.PageQueryDto;
import com.example.sporteventssystem.service.BorrowRecordService;
import com.example.sporteventssystem.vo.BorrowRecordVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrows")
@RequiredArgsConstructor
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    @GetMapping
    public Result<PageResult<BorrowRecordVo>> page(PageQueryDto query) {
        return Result.success(borrowRecordService.page(query));
    }

    @PostMapping("/apply")
    @PreAuthorize("hasAuthority('borrow:apply')")
    public Result<Void> apply(@Valid @RequestBody BorrowApplyDto dto) {
        borrowRecordService.apply(dto);
        return Result.success();
    }

    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('borrow:manage')")
    public Result<Void> approve(@Valid @RequestBody BorrowApproveDto dto) {
        borrowRecordService.approve(dto);
        return Result.success();
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasAuthority('borrow:manage')")
    public Result<Void> returnEquipment(@PathVariable Long id) {
        borrowRecordService.returnEquipment(id);
        return Result.success();
    }

    @PostMapping("/check-overdue")
    @PreAuthorize("hasAuthority('borrow:manage')")
    public Result<Void> checkOverdue() {
        borrowRecordService.checkOverdue();
        return Result.success();
    }
}
