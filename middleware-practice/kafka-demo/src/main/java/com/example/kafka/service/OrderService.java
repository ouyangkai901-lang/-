package com.example.kafka.service;

import com.example.common.model.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/** 订单服务 —— 写入 MySQL 后发 Kafka 消息 */
@Service
public class OrderService {

    private final KafkaTemplate<String, Object> kafka;

    public OrderService(KafkaTemplate<String, Object> kafka) {
        this.kafka = kafka;
    }

    public ApiResponse<String> createOrder(OrderRequest request) {
        // ① 模拟：保存订单到 MySQL（实际业务中写数据库）
        String orderId = "ORD_" + System.currentTimeMillis();

        // ② 构建消息
        OrderEvent event = OrderEvent.builder()
                .orderId(orderId)
                .userId(request.getUserId())
                .product(request.getProduct())
                .amount(request.getAmount())
                .timestamp(System.currentTimeMillis())
                .build();

        // ③ 发送到 Kafka（key = orderId，保证同一订单的消息到同一分区，有序）
        // Spring Kafka 3.x: send() 返回 CompletableFuture
        kafka.send("order-events", orderId, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("消息发送失败: " + orderId + ", " + ex.getMessage());
                    } else {
                        System.out.println("消息发送成功: " + orderId +
                                ", partition=" + result.getRecordMetadata().partition() +
                                ", offset=" + result.getRecordMetadata().offset());
                    }
                });

        return ApiResponse.success(orderId);
    }

    // ===== 内部类 =====

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class OrderRequest {
        private String userId;
        private String product;
        private double amount;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class OrderEvent {
        private String orderId;
        private String userId;
        private String product;
        private double amount;
        private long timestamp;
    }
}
