package org.slackcoder.twilight.service;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {
    @Autowired
    private Driver neo4jDriver;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public List<Map<String, Object>> recommendResources(String userId) {
        String cacheKey = "recommendations:" + userId;

        List<Map<String, Object>> cachedRecommendations = (List<Map<String, Object>>) redisTemplate.opsForValue().get(cacheKey); //先查询Redis
        if (!cachedRecommendations.isEmpty()) {
            return cachedRecommendations;
        }

        //缓存未命中再新获取
        List<Map<String, Object>> recommendations = new ArrayList<>();
        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (u:User {userId: $userId}), (r:Resource) " +
                            "RETURN r {.*, score: gds.similarity.cosine(u.embedding, r.embedding) } " +
                            "ORDER BY score DESC LIMIT 10",
                    Map.of("userId", userId)
            );
            result.stream().forEach(record -> recommendations.add(record.get("r").asMap()));
        }

        redisTemplate.opsForValue().set(cacheKey, recommendations, Duration.ofHours(1)); //缓存1h

        return recommendations;
    }
}
