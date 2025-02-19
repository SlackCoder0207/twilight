package org.slackcoder.twilight.service;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RedisStreamConsumer {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private Driver neo4jDriver;

    @Scheduled(fixedRate = 60000)  //每分钟执行一次
    public void processUserInteractions() {
        List<String> logs = redisTemplate.opsForList().range("user-interactions", 0, -1);
        if (!logs.isEmpty()) {
            System.out.println("处理用户交互日志:");
            logs.forEach(log -> {
                System.out.println(log);
                saveToNeo4j(log);
            });

            //清空Redis Stream
            redisTemplate.delete("user-interactions");
        }
    }


    private void saveToNeo4j(String log) {
        String[] parts = log.split(" ");
        if (parts.length < 3) {
            return;
        }

        String userId = parts[0];
        String action = parts[1];
        String resourceId = parts[2];

        try (Session session = neo4jDriver.session()) {
            session.run(
                    "MATCH (u:User {userId: $userId}), (r:Resource {resourceId: $resourceId}) " +
                            "MERGE (u)-[:INTERACTS_WITH {type: $action, timestamp: timestamp()}]->(r)",
                    Map.of("userId", userId, "resourceId", resourceId, "action", action)
            );
        }
    }
}
