package org.slackcoder.twilight.service;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(2)
public class GNNTrainer implements CommandLineRunner {

    private final Driver neo4jDriver;

    public GNNTrainer(Driver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    @Override
    public void run(String... args) throws Exception {
        trainGNN();
    }

    public void trainGNN() {
        try (Session session = neo4jDriver.session(SessionConfig.forDatabase("twilight"))) {
            // 清除 User 和 Resource 节点上可能存在的 embedding 属性
            session.run("MATCH (u:User) REMOVE u.embedding");
            session.run("MATCH (r:Resource) REMOVE r.embedding");

            // 尝试删除已存在的图投影（如果可用）
            try {
                session.run("CALL gds.graph.drop('recommendationGraph', false)");
                System.out.println("Existing recommendationGraph dropped.");
            } catch (Exception e) {
                System.out.println("No existing recommendationGraph to drop: " + e.getMessage());
            }

            // 创建推荐图，仅投影 INTERACTS_WITH 关系
            session.run("CALL gds.graph.project('recommendationGraph', ['User', 'Resource'], {INTERACTS_WITH: {properties: 'weight'}})");

            // 生成 GNN Embedding 向量，不再使用 overwrite 参数
            session.run("CALL gds.fastRP.mutate('recommendationGraph', {embeddingDimension: 128, mutateProperty: 'embedding'})");

            System.out.println("GNN training completed, embedding vectors generated.");
        } catch (Exception e) {
            System.err.println("GNN training error: " + e.getMessage());
        }
    }
}
