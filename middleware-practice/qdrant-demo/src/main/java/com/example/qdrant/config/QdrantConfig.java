package com.example.qdrant.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Qdrant 客户端配置 + 自动创建 Collection */
@Configuration
public class QdrantConfig {

    @Bean
    public QdrantClient qdrantClient() throws Exception {
        QdrantClient client = new QdrantClient(
                QdrantGrpcClient.newBuilder("localhost", 6334, false).build());

        // 尝试创建 Collection（如果已存在会报错，忽略）
        try {
            client.createCollectionAsync(
                    "faq",
                    VectorParams.newBuilder().setDistance(Distance.Cosine).setSize(768).build()
            ).get();
            System.out.println("[Qdrant] Collection 'faq' 创建成功");
        } catch (Exception e) {
            System.out.println("[Qdrant] Collection 'faq' 已存在，跳过创建");
        }
        return client;
    }
}
