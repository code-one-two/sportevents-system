package com.example.sporteventssystem.controller;

import com.example.sporteventssystem.common.Result;
import com.example.sporteventssystem.dto.CategorySaveDto;
import com.example.sporteventssystem.entity.EquipmentCategory;
import com.example.sporteventssystem.service.EquipmentCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class EquipmentCategoryController {

    private final EquipmentCategoryService equipmentCategoryService;

    @GetMapping
    public Result<List<EquipmentCategory>> list() {
        return Result.success(equipmentCategoryService.listAll());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('category:manage')")
    public Result<Void> save(@Valid @RequestBody CategorySaveDto dto) {
        equipmentCategoryService.save(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('category:manage')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CategorySaveDto dto) {
        dto.setId(id);
        equipmentCategoryService.save(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('category:manage')")
    public Result<Void> delete(@PathVariable Long id) {
        equipmentCategoryService.delete(id);
        return Result.success();
    }
}
