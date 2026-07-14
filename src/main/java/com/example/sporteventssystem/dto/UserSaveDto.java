package com.example.sporteventssystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserSaveDto {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    private String realName;
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private Long roleId;
}
