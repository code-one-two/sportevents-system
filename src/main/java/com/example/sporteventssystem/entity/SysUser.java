package com.example.sporteventssystem.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table("sys_user")
public class SysUser {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
