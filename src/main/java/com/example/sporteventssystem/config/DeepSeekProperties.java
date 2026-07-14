package com.example.sporteventssystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekProperties {

    private String baseUrl = "https://api.deepseek.com";
    private String apiKey;
    private String model = "deepseek-v4-flash";
}
