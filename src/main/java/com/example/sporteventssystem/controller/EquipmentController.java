package com.example.sporteventssystem.controller;

import com.example.sporteventssystem.common.PageResult;
import com.example.sporteventssystem.common.Result;
import com.example.sporteventssystem.dto.EquipmentSaveDto;
import com.example.sporteventssystem.dto.PageQueryDto;
import com.example.sporteventssystem.service.EquipmentService;
import com.example.sporteventssystem.vo.EquipmentVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    public Result<PageResult<EquipmentVo>> page(PageQueryDto query) {
        return Result.success(equipmentService.page(query));
    }

    @GetMapping("/{id}")
    public Result<EquipmentVo> getById(@PathVariable Long id) {
        return Result.success(equipmentService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('equipment:manage')")
    public Result<Void> save(@Valid @RequestBody EquipmentSaveDto dto) {
        equipmentService.save(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('equipment:manage')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody EquipmentSaveDto dto) {
        dto.setId(id);
        equipmentService.save(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('equipment:manage')")
    public Result<Void> delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return Result.success();
    }
}
