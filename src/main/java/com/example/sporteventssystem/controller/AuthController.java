package com.example.sporteventssystem.controller;

import com.example.sporteventssystem.common.Result;
import com.example.sporteventssystem.dto.LoginDto;
import com.example.sporteventssystem.service.AuthService;
import com.example.sporteventssystem.vo.LoginVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginVo> login(@Valid @RequestBody LoginDto dto) {
        return Result.success(authService.login(dto));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }
}
