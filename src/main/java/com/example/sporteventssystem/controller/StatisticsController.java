package com.example.sporteventssystem.controller;

import com.example.sporteventssystem.common.Result;
import com.example.sporteventssystem.service.StatisticsService;
import com.example.sporteventssystem.vo.StatisticsVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('statistics:view')")
    public Result<StatisticsVo> dashboard() {
        return Result.success(statisticsService.getDashboard());
    }
}
