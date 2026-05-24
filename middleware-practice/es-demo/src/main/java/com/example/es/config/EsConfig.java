package com.example.es.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Elasticsearch 8.x Java Client 配置 */
@Configuration
public class EsConfig {

    @Bean
    public ElasticsearchClient esClient() {
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http")
        ).build();
        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
        );
        return new ElasticsearchClient(transport);
    }
}
