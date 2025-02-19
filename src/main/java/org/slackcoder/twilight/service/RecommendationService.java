package org.slackcoder.twilight.service;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.slackcoder.twilight.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {
    @Autowired
    private Driver neo4jDriver;

    public ApiResponse<List<Map<String, Object>>> recommendResources(String userId) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (u:User {userId: $userId})-[:INTERACTS_WITH]->(r:Resource) " +
                            "WITH u, gds.similarity.cosine(u.embedding, r.embedding) AS score, r " +
                            "RETURN r {.*} AS resource ORDER BY score DESC LIMIT 10",
                    Map.of("userId", userId)
            );
            result.stream().forEach(record -> recommendations.add(record.get("resource").asMap()));
        }
        return new ApiResponse<>(200, recommendations);
    }
}
