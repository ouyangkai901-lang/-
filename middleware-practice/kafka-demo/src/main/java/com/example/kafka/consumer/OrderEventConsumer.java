package com.example.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 消费者 —— 模拟下游服务消费 Kafka 消息。
 * 实际场景：短信服务、积分服务、数据分析分别消费同一个 Topic。
 */
@Component
public class OrderEventConsumer {

    /** 消费 order-events，模拟短信通知 */
    @KafkaListener(topics = "order-events", groupId = "sms-service")
    public void handleSms(String message) {
        System.out.println("[短信服务] 收到订单消息: " + message);
        // 实际: smsService.send(phone, "订单已创建");
    }

    /** 消费 order-events，模拟积分发放（不同 Consumer Group，独立消费） */
    @KafkaListener(topics = "order-events", groupId = "points-service")
    public void handlePoints(String message) {
        System.out.println("[积分服务] 收到订单消息: " + message);
        // 实际: pointsService.addPoints(userId, amount * 10);
    }

    /** 消费 order-events，模拟数据分析 */
    @KafkaListener(topics = "order-events", groupId = "analytics-service")
    public void handleAnalytics(String message) {
        System.out.println("[分析服务] 收到订单消息: " + message);
        // 实际: analyticsService.track("order_created", event);
    }
}
