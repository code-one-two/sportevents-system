package com.example.sporteventssystem.config;

import com.example.sporteventssystem.entity.SysRole;
import com.example.sporteventssystem.entity.SysUser;
import com.example.sporteventssystem.entity.SysUserRole;
import com.example.sporteventssystem.mapper.SysRoleMapper;
import com.example.sporteventssystem.mapper.SysUserMapper;
import com.example.sporteventssystem.mapper.SysUserRoleMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initUser("theRenter", "默认租户", 3L);
        initUser("admin", "系统管理员", 1L);
    }

    private void initUser(String username, String realName, Long roleId) {
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            user = new SysUser();
            user.setUsername(username);
            user.setRealName(realName);
            user.setPassword(passwordEncoder.encode("123123"));
            user.setStatus(1);
            sysUserMapper.insert(user);
            log.info("初始化用户: {}", username);
        }

        long count = sysUserRoleMapper.selectCountByQuery(
                QueryWrapper.create().where("user_id = ?", user.getId()));
        if (count == 0) {
            SysRole role = sysRoleMapper.selectOneById(roleId);
            if (role != null) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(roleId);
                sysUserRoleMapper.insert(ur);
            }
        }
    }
}
