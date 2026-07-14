package com.example.sporteventssystem.service.impl;

import com.example.sporteventssystem.entity.SysRole;
import com.example.sporteventssystem.mapper.SysRoleMapper;
import com.example.sporteventssystem.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;

    @Override
    public List<SysRole> listAll() {
        return sysRoleMapper.selectAll();
    }
}
