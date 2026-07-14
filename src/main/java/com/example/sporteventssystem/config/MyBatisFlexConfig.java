package com.example.sporteventssystem.config;

import com.mybatisflex.core.audit.AuditManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.sporteventssystem.mapper")
public class MyBatisFlexConfig {

    public MyBatisFlexConfig() {
        AuditManager.setAuditEnable(false);
    }
}
