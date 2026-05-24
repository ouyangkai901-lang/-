package com.example.qdrant.controller;

import com.example.common.model.ApiResponse;
import com.example.qdrant.service.QdrantFaqService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/** Qdrant FAQ 搜索 Controller */
@RestController
@RequestMapping("/api/faq")
public class FaqController {

    @Resource
    private QdrantFaqService faqService;

    /** POST /api/faq —— 插入 FAQ */
    @PostMapping
    public ApiResponse<Long> upsert(@RequestBody FaqRequest req) throws Exception {
        return ApiResponse.success(faqService.upsertFaq(req.getQuestion(), req.getAnswer(), req.getCategory()));
    }

    /** GET /api/faq/search?q=快递怎么退&limit=5 —— 语义搜索 */
    @GetMapping("/search")
    public ApiResponse<List<QdrantFaqService.FaqResult>> search(
            @RequestParam("q") String question,
            @RequestParam(defaultValue = "5") int limit) throws Exception {
        return ApiResponse.success(faqService.searchSimilar(question, limit));
    }

    /** GET /api/faq/search?q=快递&category=物流 —— 语义搜索 + 分类过滤 */
    @GetMapping("/search-filtered")
    public ApiResponse<List<QdrantFaqService.FaqResult>> searchFiltered(
            @RequestParam("q") String question,
            @RequestParam String category,
            @RequestParam(defaultValue = "5") int limit) throws Exception {
        return ApiResponse.success(faqService.searchWithFilter(question, category, limit));
    }

    public static class FaqRequest {
        private String question;
        private String answer;
        private String category;
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }
}
