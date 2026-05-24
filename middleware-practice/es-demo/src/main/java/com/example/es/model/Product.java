package com.example.es.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** ES 商品文档 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Product {
    private String id;
    private String title;
    private String description;
    private String category;
    private double price;
    private int stock;
    private long created;
}
