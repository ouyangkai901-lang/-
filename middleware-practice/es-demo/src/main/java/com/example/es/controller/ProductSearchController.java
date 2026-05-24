package com.example.es.controller;

import com.example.common.model.ApiResponse;
import com.example.es.model.Product;
import com.example.es.service.ProductSearchService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

/** 商品搜索 Controller */
@RestController
@RequestMapping("/api/products")
public class ProductSearchController {

    @Resource
    private ProductSearchService productSearchService;

    /** POST /api/products —— 索引商品 */
    @PostMapping
    public ApiResponse<String> index(@RequestBody Product product) throws Exception {
        return ApiResponse.success(productSearchService.index(product));
    }

    /** GET /api/products/search?keyword=手机&category=数码&minPrice=1000&maxPrice=5000&page=0&size=10 */
    @GetMapping("/search")
    public ApiResponse<Map<String, Object>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws Exception {
        return ApiResponse.success(
                productSearchService.search(keyword, category, minPrice, maxPrice, page, size));
    }

    /** GET /api/products/{id} */
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getById(@PathVariable String id) throws Exception {
        return ApiResponse.success(productSearchService.getById(id));
    }

    /** DELETE /api/products/{id} */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) throws Exception {
        productSearchService.delete(id);
        return ApiResponse.success(null);
    }
}
