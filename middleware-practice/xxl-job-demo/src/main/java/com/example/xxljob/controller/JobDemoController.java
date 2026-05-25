package com.example.xxljob.controller;

import com.example.common.model.ApiResponse;
import com.example.xxljob.service.ReportService;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobDemoController {

    private final ReportService reportService;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${demo.local-scheduled.enabled}")
    private boolean localScheduledEnabled;

    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> status() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("appName", appName);
        result.put("localScheduledEnabled", localScheduledEnabled);
        result.put("xxlJobHandlerNames", new String[]{"dailyReportJob", "shardingDemoJob"});
        return ApiResponse.success(result);
    }

    @GetMapping("/report-preview")
    public ApiResponse<Map<String, Object>> reportPreview() {
        return ApiResponse.success(reportService.generateDailyReport("manual-preview"));
    }
}
