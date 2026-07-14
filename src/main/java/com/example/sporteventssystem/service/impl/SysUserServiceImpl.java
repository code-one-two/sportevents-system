package com.example.sporteventssystem.service.impl;

import com.example.sporteventssystem.common.PageResult;
import com.example.sporteventssystem.common.exception.BusinessException;
import com.example.sporteventssystem.dto.PageQueryDto;
import com.example.sporteventssystem.dto.UserSaveDto;
import com.example.sporteventssystem.entity.SysRole;
import com.example.sporteventssystem.entity.SysUser;
import com.example.sporteventssystem.entity.SysUserRole;
import com.example.sporteventssystem.mapper.SysRoleMapper;
import com.example.sporteventssystem.mapper.SysUserMapper;
import com.example.sporteventssystem.mapper.SysUserRoleMapper;
import com.example.sporteventssystem.service.SysUserService;
import com.example.sporteventssystem.vo.UserVo;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<UserVo> page(PageQueryDto query) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = "%" + query.getKeyword() + "%";
            wrapper.and("(username LIKE ? OR real_name LIKE ?)", keyword, keyword);
        }
        wrapper.orderBy("create_time", false);
        Page<SysUser> page = sysUserMapper.paginate(query.getPageNum(), query.getPageSize(), wrapper);
        List<UserVo> records = page.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        return PageResult.of(page.getTotalRow(), query.getPageNum(), query.getPageSize(), records);
    }

    @Override
    public UserVo getById(Long id) {
        SysUser user = sysUserMapper.selectOneById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return toVo(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UserSaveDto dto) {
        if (dto.getId() == null) {
            SysUser exist = sysUserMapper.selectByUsername(dto.getUsername());
            if (exist != null) {
                throw new BusinessException("用户名已存在");
            }
            SysUser user = new SysUser();
            copyDtoToEntity(dto, user);
            user.setPassword(passwordEncoder.encode(
                    StringUtils.hasText(dto.getPassword()) ? dto.getPassword() : "123123"));
            sysUserMapper.insert(user);
            saveUserRole(user.getId(), dto.getRoleId());
        } else {
            SysUser user = sysUserMapper.selectOneById(dto.getId());
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            copyDtoToEntity(dto, user);
            if (StringUtils.hasText(dto.getPassword())) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            sysUserMapper.update(user);
            sysUserRoleMapper.deleteByQuery(QueryWrapper.create().where("user_id = ?", dto.getId()));
            saveUserRole(user.getId(), dto.getRoleId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        sysUserMapper.deleteById(id);
        sysUserRoleMapper.deleteByQuery(QueryWrapper.create().where("user_id = ?", id));
    }

    @Override
    public List<UserVo> listAll() {
        return sysUserMapper.selectAll().stream().map(this::toVo).collect(Collectors.toList());
    }

    private void saveUserRole(Long userId, Long roleId) {
        if (roleId != null) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            sysUserRoleMapper.insert(ur);
        }
    }

    private void copyDtoToEntity(UserSaveDto dto, SysUser user) {
        user.setUsername(dto.getUsername());
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus());
    }

    private UserVo toVo(SysUser user) {
        UserVo vo = new UserVo();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(user.getId());
        vo.setRoles(roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
        return vo;
    }
}
