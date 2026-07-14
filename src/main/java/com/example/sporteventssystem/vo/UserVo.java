package com.example.sporteventssystem.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVo {

    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private Integer status;
    private List<String> roles;
    private LocalDateTime createTime;
}
