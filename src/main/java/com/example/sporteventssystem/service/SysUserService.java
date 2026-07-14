package com.example.sporteventssystem.service;

import com.example.sporteventssystem.common.PageResult;
import com.example.sporteventssystem.dto.PageQueryDto;
import com.example.sporteventssystem.dto.UserSaveDto;
import com.example.sporteventssystem.vo.UserVo;

import java.util.List;

public interface SysUserService {

    PageResult<UserVo> page(PageQueryDto query);

    UserVo getById(Long id);

    void save(UserSaveDto dto);

    void delete(Long id);

    List<UserVo> listAll();
}
