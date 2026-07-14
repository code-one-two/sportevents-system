package com.example.sporteventssystem.service.impl;

import com.example.sporteventssystem.dto.LoginDto;
import com.example.sporteventssystem.entity.SysRole;
import com.example.sporteventssystem.entity.SysUser;
import com.example.sporteventssystem.mapper.SysPermissionMapper;
import com.example.sporteventssystem.mapper.SysRoleMapper;
import com.example.sporteventssystem.mapper.SysUserMapper;
import com.example.sporteventssystem.security.JwtTokenProvider;
import com.example.sporteventssystem.service.AuthService;
import com.example.sporteventssystem.vo.LoginVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public LoginVo login(LoginDto dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        SysUser user = sysUserMapper.selectByUsername(authentication.getName());
        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(user.getId());
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).toList();
        List<String> permissions = sysPermissionMapper.selectPermissionCodesByUserId(user.getId());
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), roleCodes, permissions);

        LoginVo vo = new LoginVo();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRoles(roleCodes);
        vo.setPermissions(permissions);
        return vo;
    }

    @Override
    public void logout() {
        // JWT 无状态，前端清除 token 即可
    }
}
