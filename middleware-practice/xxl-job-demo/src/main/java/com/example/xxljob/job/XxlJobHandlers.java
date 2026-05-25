package com.example.xxljob.job;

import com.example.xxljob.service.ReportService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class XxlJobHandlers {

    private final ReportService reportService;

    @XxlJob("dailyReportJob")
    public void dailyReportJob() {
        String param = XxlJobHelper.getJobParam();
        log.info("xxl-job dailyReportJob triggered, param={}", param);
        XxlJobHelper.log("start dailyReportJob, param={0}", param);

        var result = reportService.generateDailyReport("XXL-JOB");

        XxlJobHelper.log("dailyReportJob finished, result={0}", result);
    }

    @XxlJob("shardingDemoJob")
    public void shardingDemoJob() {
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        log.info("xxl-job shardingDemoJob triggered, shardIndex={}, shardTotal={}", shardIndex, shardTotal);
        XxlJobHelper.log("processing shard {0}/{1}", shardIndex, shardTotal);
        XxlJobHelper.log("mock batch processing done for shard {0}", shardIndex);
    }
}
