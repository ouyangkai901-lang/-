# XXL-JOB Demo

这个模块用来演示两件事：

1. 业务代码如何作为 `XXL-JOB Executor` 接入调度中心。
2. 同一份报表逻辑如何分别通过 `@Scheduled` 和 `@XxlJob` 触发。

## 目录说明

- `config/XxlJobConfig.java`：注册 `XxlJobSpringExecutor`
- `job/XxlJobHandlers.java`：XXL-JOB 任务示例
- `job/LocalScheduledJobs.java`：本地 `@Scheduled` 对照示例
- `controller/JobDemoController.java`：查看 demo 状态和预览任务结果

## 本地启动

### 1. 仅演示 XXL-JOB Executor

```bash
mvn -pl xxl-job-demo spring-boot:run
```

### 2. 同时打开本地 `@Scheduled` 对照任务

```bash
mvn -pl xxl-job-demo spring-boot:run -Dspring-boot.run.profiles=local-scheduled
```

## 访问接口

- `GET /api/jobs/status`
- `GET /api/jobs/report-preview`

## 说明

- `dailyReportJob`：普通调度任务示例
- `shardingDemoJob`：分片任务示例
- 默认 `@Scheduled` 关闭，避免你在对比 XXL-JOB 时出现双重触发干扰
