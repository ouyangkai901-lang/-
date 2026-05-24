package com.example.kafka.controller;

import com.example.common.model.ApiResponse;
import com.example.kafka.service.OrderService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/** 订单 Controller —— 触发 Kafka 消息发送 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Resource
    private OrderService orderService;

    /** POST /api/orders → 创建订单 + 发 Kafka 消息 */
    @PostMapping
    public ApiResponse<String> createOrder(@RequestBody OrderService.OrderRequest request) {
        return orderService.createOrder(request);
    }
}
