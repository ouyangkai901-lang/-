package com.example.xxljob.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    public Map<String, Object> generateDailyReport(String triggerType) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("triggerType", triggerType);
        result.put("reportName", "daily-order-summary");
        result.put("generatedAt", LocalDateTime.now().toString());
        result.put("scannedOrders", 1280);
        result.put("abnormalOrders", 17);
        result.put("success", true);
        return result;
    }
}
