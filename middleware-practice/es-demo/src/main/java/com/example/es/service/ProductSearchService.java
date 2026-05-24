package com.example.es.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.es.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

/** ES 商品搜索服务 — 使用 Elasticsearch 8.x Java Client */
@Service
public class ProductSearchService {

    private static final String INDEX_NAME = "products";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Resource
    private ElasticsearchClient esClient;

    /** 索引一条商品 */
    public String index(Product product) throws Exception {
        String id = product.getId() != null ? product.getId() : UUID.randomUUID().toString();
        product.setId(id);
        product.setCreated(System.currentTimeMillis());

        esClient.index(i -> i.index(INDEX_NAME).id(id).document(product));
        return id;
    }

    /** 批量索引 */
    public int batchIndex(List<Product> products) throws Exception {
        for (Product p : products) {
            index(p);
        }
        return products.size();
    }

    /** 搜索商品 —— 分词匹配 + 范围过滤 + 排序 + 分页 */
    public Map<String, Object> search(String keyword, String category,
                                       Double minPrice, Double maxPrice,
                                       int page, int size) throws Exception {

        SearchResponse<Product> response = esClient.search(s -> s
                .index(INDEX_NAME)
                .query(q -> q.bool(b -> {
                    if (keyword != null && !keyword.isEmpty()) {
                        b.must(m -> m.match(t -> t.field("title").query(keyword)));
                    }
                    if (category != null && !category.isEmpty()) {
                        b.filter(f -> f.term(t -> t.field("category").value(category)));
                    }
                    if (minPrice != null || maxPrice != null) {
                        b.filter(f -> f.range(r -> r.number(n -> {
                            n.field("price");
                            if (minPrice != null) n.gte(minPrice);
                            if (maxPrice != null) n.lte(maxPrice);
                            return n;
                        })));
                    }
                    return b;
                }))
                .sort(srt -> srt.field(f -> f.field("price").order(SortOrder.Asc)))
                .from(page * size)
                .size(size)
        , Product.class);

        List<Map<String, Object>> products = new ArrayList<>();
        for (Hit<Product> hit : response.hits().hits()) {
            Map<String, Object> map = MAPPER.convertValue(hit.source(), Map.class);
            map.put("_id", hit.id());
            map.put("_score", hit.score() != null ? hit.score() : 0.0);
            products.add(map);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", response.hits().total().value());
        result.put("products", products);
        return result;
    }

    /** 按 ID 查询 */
    public Map<String, Object> getById(String id) throws Exception {
        GetResponse<Product> response = esClient.get(g -> g.index(INDEX_NAME).id(id), Product.class);
        if (response.found() && response.source() != null) {
            return MAPPER.convertValue(response.source(), Map.class);
        }
        return null;
    }

    /** 删除 */
    public void delete(String id) throws Exception {
        esClient.delete(d -> d.index(INDEX_NAME).id(id));
    }
}
