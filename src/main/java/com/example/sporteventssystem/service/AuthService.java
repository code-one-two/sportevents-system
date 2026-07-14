package com.example.sporteventssystem.service;

import com.example.sporteventssystem.dto.LoginDto;
import com.example.sporteventssystem.vo.LoginVo;

public interface AuthService {

    LoginVo login(LoginDto dto);

    void logout();
}
