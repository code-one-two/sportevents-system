package com.example.sporteventssystem.vo;

import lombok.Data;

import java.util.List;

@Data
public class LoginVo {

    private String token;
    private Long userId;
    private String username;
    private String realName;
    private List<String> roles;
    private List<String> permissions;
}
