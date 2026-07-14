package com.example.sporteventssystem.config;

import com.example.sporteventssystem.service.BorrowRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OverdueCheckScheduler {

    private final BorrowRecordService borrowRecordService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void checkOverdue() {
        borrowRecordService.checkOverdue();
    }
}
