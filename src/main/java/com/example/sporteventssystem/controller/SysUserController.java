package com.example.sporteventssystem.controller;

import com.example.sporteventssystem.common.PageResult;
import com.example.sporteventssystem.common.Result;
import com.example.sporteventssystem.dto.PageQueryDto;
import com.example.sporteventssystem.dto.UserSaveDto;
import com.example.sporteventssystem.entity.SysRole;
import com.example.sporteventssystem.service.SysRoleService;
import com.example.sporteventssystem.service.SysUserService;
import com.example.sporteventssystem.vo.UserVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;

    @GetMapping
    @PreAuthorize("hasAuthority('user:manage')")
    public Result<PageResult<UserVo>> page(PageQueryDto query) {
        return Result.success(sysUserService.page(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:manage')")
    public Result<UserVo> getById(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:manage')")
    public Result<Void> save(@Valid @RequestBody UserSaveDto dto) {
        sysUserService.save(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:manage')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserSaveDto dto) {
        dto.setId(id);
        sysUserService.save(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:manage')")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return Result.success();
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('user:manage')")
    public Result<List<SysRole>> roles() {
        return Result.success(sysRoleService.listAll());
    }
}
