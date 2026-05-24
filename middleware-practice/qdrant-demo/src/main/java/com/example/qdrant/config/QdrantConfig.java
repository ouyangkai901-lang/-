package com.example.qdrant.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Qdrant 客户端配置 + 自动创建 Collection */
@Configuration
public class QdrantConfig {

    @Value("${qdrant.host:localhost}")
    private String host;

    @Value("${qdrant.grpc.port:6334}")
    private int grpcPort;

    @Value("${qdrant.collection:faq}")
    private String collection;

    @Bean
    public QdrantClient qdrantClient() throws Exception {
        QdrantClient client = new QdrantClient(
                QdrantGrpcClient.newBuilder(host, grpcPort, false).build());

        // Create the collection only when it does not already exist,
        // so startup logs stay clean on repeated runs.
        boolean exists = client.collectionExistsAsync(collection).get();
        if (!exists) {
            client.createCollectionAsync(
                    collection,
                    VectorParams.newBuilder().setDistance(Distance.Cosine).setSize(768).build()
            ).get();
            System.out.println("[Qdrant] Collection '" + collection + "' 创建成功");
        } else {
            System.out.println("[Qdrant] Collection '" + collection + "' 已存在，跳过创建");
        }
        return client;
    }
}
