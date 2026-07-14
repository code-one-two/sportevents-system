package com.example.sporteventssystem.controller;

import com.example.sporteventssystem.common.PageResult;
import com.example.sporteventssystem.common.Result;
import com.example.sporteventssystem.dto.MaintenanceSaveDto;
import com.example.sporteventssystem.dto.PageQueryDto;
import com.example.sporteventssystem.service.MaintenanceRecordService;
import com.example.sporteventssystem.vo.MaintenanceRecordVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maintenances")
@RequiredArgsConstructor
public class MaintenanceRecordController {

    private final MaintenanceRecordService maintenanceRecordService;

    @GetMapping
    @PreAuthorize("hasAuthority('maintenance:manage')")
    public Result<PageResult<MaintenanceRecordVo>> page(PageQueryDto query) {
        return Result.success(maintenanceRecordService.page(query));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('maintenance:manage')")
    public Result<Void> save(@Valid @RequestBody MaintenanceSaveDto dto) {
        maintenanceRecordService.save(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('maintenance:manage')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MaintenanceSaveDto dto) {
        dto.setId(id);
        maintenanceRecordService.save(dto);
        return Result.success();
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAuthority('maintenance:manage')")
    public Result<Void> start(@PathVariable Long id) {
        maintenanceRecordService.start(id);
        return Result.success();
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('maintenance:manage')")
    public Result<Void> complete(@PathVariable Long id, @RequestBody MaintenanceSaveDto dto) {
        maintenanceRecordService.complete(id, dto);
        return Result.success();
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasAuthority('maintenance:manage')")
    public Result<Void> archive(@PathVariable Long id) {
        maintenanceRecordService.archive(id);
        return Result.success();
    }
}
