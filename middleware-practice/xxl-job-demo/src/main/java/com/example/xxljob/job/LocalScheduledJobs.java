package com.example.xxljob.job;

import com.example.xxljob.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "demo.local-scheduled", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class LocalScheduledJobs {

    private final ReportService reportService;

    @Scheduled(cron = "${demo.local-scheduled.cron}")
    public void localReportJob() {
        log.info("local @Scheduled job triggered, result={}",
                reportService.generateDailyReport("@Scheduled"));
    }
}
