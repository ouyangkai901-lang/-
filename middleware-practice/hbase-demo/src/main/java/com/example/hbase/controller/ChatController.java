package com.example.hbase.controller;

import com.example.common.model.ApiResponse;
import com.example.hbase.service.HBaseChatService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/** HBase 对话 Controller */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Resource
    private HBaseChatService chatService;

    /** POST /api/chat —— 保存一条对话 */
    @PostMapping
    public ApiResponse<String> saveMessage(@RequestBody ChatRequest request) throws Exception {
        String rowKey = chatService.saveMessage(request.getUserId(), request.getQuestion(), request.getAnswer());
        return ApiResponse.success(rowKey);
    }

    /** GET /api/chat/history?userId=xxx&limit=20 —— 查用户历史对话 */
    @GetMapping("/history")
    public ApiResponse<List<HBaseChatService.ChatMessage>> getHistory(
            @RequestParam String userId,
            @RequestParam(defaultValue = "20") int limit) throws Exception {
        return ApiResponse.success(chatService.getChatHistory(userId, limit));
    }

    /** GET /api/chat/{rowKey} —— 按 RowKey 查单条 */
    @GetMapping("/{rowKey}")
    public ApiResponse<String> getByRowKey(@PathVariable String rowKey) throws Exception {
        return ApiResponse.success(chatService.getByRowKey(rowKey));
    }

    /** 请求体 */
    public static class ChatRequest {
        private String userId;
        private String question;
        private String answer;
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }
}
