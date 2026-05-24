package com.example.qdrant.service;

import io.qdrant.client.PointIdFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.ValueFactory;
import io.qdrant.client.WithPayloadSelectorFactory;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;

/** Qdrant FAQ 向量搜索服务 */
@Service
public class QdrantFaqService {

    @Value("${qdrant.collection:faq}")
    private String collection;

    @Resource
    private QdrantClient qdrantClient;

    /**
     * 插入 FAQ 向量。
     * 实际场景中，question 需要先通过 Embedding 模型（BGE/m3e）转为 768 维向量。
     * 这里为了演示，用模拟向量代替。
     */
    public Long upsertFaq(String question, String answer, String category) throws Exception {
        long id = System.currentTimeMillis();
        List<Float> vector = mockEmbedding(question);

        Map<String, JsonWithInt.Value> payload = new LinkedHashMap<>();
        payload.put("question", ValueFactory.value(question));
        payload.put("answer", ValueFactory.value(answer));
        payload.put("category", ValueFactory.value(category));

        Points.PointStruct point = Points.PointStruct.newBuilder()
                .setId(PointIdFactory.id(id))
                .setVectors(Points.Vectors.newBuilder()
                        .setVector(Points.Vector.newBuilder().addAllData(vector).build())
                        .build())
                .putAllPayload(payload)
                .build();

        qdrantClient.upsertAsync(collection, Collections.singletonList(point)).get();
        return id;
    }

    /** 向量相似度搜索（不带过滤） */
    public List<FaqResult> searchSimilar(String question, int limit) throws Exception {
        List<Float> queryVector = mockEmbedding(question);

        List<Points.ScoredPoint> points = qdrantClient.searchAsync(
                Points.SearchPoints.newBuilder()
                        .setCollectionName(collection)
                        .addAllVector(queryVector)
                        .setLimit(limit)
                        .setWithPayload(WithPayloadSelectorFactory.enable(true))
                        .build()
        ).get();

        return toFaqResults(points);
    }

    /** 向量搜索 + 分类过滤 */
    public List<FaqResult> searchWithFilter(String question, String category, int limit) throws Exception {
        List<Float> queryVector = mockEmbedding(question);

        List<Points.ScoredPoint> points = qdrantClient.searchAsync(
                Points.SearchPoints.newBuilder()
                        .setCollectionName(collection)
                        .addAllVector(queryVector)
                        .setLimit(limit)
                        .setFilter(Points.Filter.newBuilder()
                                .addMust(Points.Condition.newBuilder()
                                        .setField(Points.FieldCondition.newBuilder()
                                                .setKey("category")
                                                .setMatch(Points.Match.newBuilder()
                                                        .setKeyword(category).build())
                                                .build())
                                        .build())
                                .build())
                        .setWithPayload(WithPayloadSelectorFactory.enable(true))
                        .build()
        ).get();

        return toFaqResults(points);
    }

    /** 模拟 Embedding（实际替换为 BGE/m3e 模型调用） */
    private List<Float> mockEmbedding(String text) {
        Random r = new Random(text.hashCode());
        List<Float> vector = new ArrayList<>(768);
        for (int i = 0; i < 768; i++) {
            vector.add(r.nextFloat() * 2 - 1);
        }
        return vector;
    }

    private List<FaqResult> toFaqResults(List<Points.ScoredPoint> points) {
        List<FaqResult> results = new ArrayList<>();
        for (Points.ScoredPoint p : points) {
            FaqResult r = new FaqResult();
            r.setScore(p.getScore());
            Map<String, JsonWithInt.Value> payload = p.getPayloadMap();
            if (payload.containsKey("question")) r.setQuestion(payload.get("question").getStringValue());
            if (payload.containsKey("answer"))   r.setAnswer(payload.get("answer").getStringValue());
            if (payload.containsKey("category")) r.setCategory(payload.get("category").getStringValue());
            results.add(r);
        }
        return results;
    }

    public static class FaqResult {
        private double score;
        private String question;
        private String answer;
        private String category;
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }
}
