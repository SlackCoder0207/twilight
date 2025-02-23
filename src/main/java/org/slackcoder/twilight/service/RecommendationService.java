package org.slackcoder.twilight.service;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationService {

    private final Driver neo4jDriver;

    @Autowired
    public RecommendationService(Driver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    // 根据用户推荐资源，返回前10个资源，并对相似度仅保留两位小数
    public List<Map<String, Object>> recommendResources(String userId) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        try (Session session = neo4jDriver.session(SessionConfig.forDatabase("twilight"))) {
            var result = session.run(
                    "MATCH (u:User {userId: $userId}), (r:Resource) " +
                            "WHERE u.embedding IS NOT NULL AND r.embedding IS NOT NULL " +
                            "WITH u, r, gds.similarity.cosine(u.embedding, r.embedding) AS score " +
                            "RETURN r {.*, score: toFloat(round(score * 100))/100.0} AS recommendation " +
                            "ORDER BY score DESC LIMIT 10",
                    Collections.singletonMap("userId", userId)
            );
            while (result.hasNext()) {
                recommendations.add(result.next().get("recommendation").asMap());
            }
        }
        return recommendations;
    }

    // 新增热门资源接口：返回交互数最多的前6个资源
    public List<Map<String, Object>> getPopularResources() {
        List<Map<String, Object>> popularResources = new ArrayList<>();
        try (Session session = neo4jDriver.session(SessionConfig.forDatabase("twilight"))) {
            var result = session.run(
                    "MATCH (u:User)-[r:INTERACTS_WITH]->(res:Resource) " +
                            "WITH res, count(r) AS interactions " +
                            "RETURN res {.*, interactions: interactions} AS resource " +
                            "ORDER BY interactions DESC LIMIT 6"
            );
            while (result.hasNext()) {
                popularResources.add(result.next().get("resource").asMap());
            }
        }
        return popularResources;
    }
}
