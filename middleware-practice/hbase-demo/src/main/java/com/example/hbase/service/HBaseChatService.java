package com.example.hbase.service;

import lombok.Builder;
import lombok.Data;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/** 智能客服对话记录 HBase 存储服务 */
@Service
public class HBaseChatService {

    private static final String TABLE_NAME = "conversation";
    private static final byte[] CF = Bytes.toBytes("cf");
    private static final byte[] COL_USER = Bytes.toBytes("user_id");
    private static final byte[] COL_QUESTION = Bytes.toBytes("question");
    private static final byte[] COL_ANSWER = Bytes.toBytes("answer");
    private static final byte[] COL_TIMESTAMP = Bytes.toBytes("ts");

    @Resource
    private Connection connection;

    /** 用 MD5 前 4 位做散列前缀，防热点 */
    private String buildRowKey(String userId) {
        String hash = md5Prefix(userId);
        long reverseTs = Long.MAX_VALUE - System.currentTimeMillis();
        return hash + "_" + userId + "_" + String.format("%019d", reverseTs);
    }

    private String md5Prefix(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                sb.append(String.format("%02x", digest[i] & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            return "00000000";
        }
    }

    /** 保存一条对话 */
    public String saveMessage(String userId, String question, String answer) throws Exception {
        String rowKey = buildRowKey(userId);
        Table table = connection.getTable(TableName.valueOf(TABLE_NAME));

        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(CF, COL_USER, Bytes.toBytes(userId));
        put.addColumn(CF, COL_QUESTION, Bytes.toBytes(question));
        put.addColumn(CF, COL_ANSWER, Bytes.toBytes(answer));
        put.addColumn(CF, COL_TIMESTAMP, Bytes.toBytes(String.valueOf(System.currentTimeMillis())));

        table.put(put);
        table.close();
        return rowKey;
    }

    /** 查询某用户的最新 N 条历史对话 */
    public List<ChatMessage> getChatHistory(String userId, int limit) throws Exception {
        String startRow = md5Prefix(userId) + "_" + userId + "_";
        String stopRow  = md5Prefix(userId) + "_" + userId + "|";  // | ASCII 码 > _

        Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
        Scan scan = new Scan();
        scan.withStartRow(Bytes.toBytes(startRow));
        scan.withStopRow(Bytes.toBytes(stopRow));
        scan.setReversed(true);   // 反向扫描 → 最新的在前
        scan.setMaxResultSize((long) limit);

        List<ChatMessage> messages = new ArrayList<>();
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            ChatMessage msg = ChatMessage.builder()
                    .rowKey(Bytes.toString(r.getRow()))
                    .userId(Bytes.toString(r.getValue(CF, COL_USER)))
                    .question(Bytes.toString(r.getValue(CF, COL_QUESTION)))
                    .answer(Bytes.toString(r.getValue(CF, COL_ANSWER)))
                    .timestamp(Bytes.toString(r.getValue(CF, COL_TIMESTAMP)))
                    .build();
            messages.add(msg);
            if (messages.size() >= limit) break;
        }

        scanner.close();
        table.close();
        return messages;
    }

    /** 按 RowKey 精确查询 */
    public String getByRowKey(String rowKey) throws Exception {
        Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
        Get get = new Get(Bytes.toBytes(rowKey));
        Result r = table.get(get);
        table.close();

        if (r.isEmpty()) return null;
        return Bytes.toString(r.getValue(CF, COL_ANSWER));
    }

    @Data @Builder
    public static class ChatMessage {
        private String rowKey;
        private String userId;
        private String question;
        private String answer;
        private String timestamp;
    }
}
