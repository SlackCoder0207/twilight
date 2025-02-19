package org.slackcoder.twilight.service;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GNNTrainer {
    private final Driver neo4jDriver;

    public GNNTrainer(Driver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    @Scheduled(cron = "0 0 3 * * ?")  //3am
    public void trainGNN() {
        try (Session session = neo4jDriver.session()) {
            //1.创建推荐图
            session.run("CALL gds.graph.project('recommendationGraph', ['User', 'Resource'], " +
                    "{INTERACTS_WITH: {properties: 'weight'}, SIMILAR_TO: {properties: 'score'}})");

            //2.生成GNN Embedding
            session.run("CALL gds.fastRP.mutate('recommendationGraph', " +
                    "{embeddingDimension: 128, mutateProperty: 'embedding'})");

            System.out.println("GNN训练完成，已生成Embedding向量。");
        }
    }
}
