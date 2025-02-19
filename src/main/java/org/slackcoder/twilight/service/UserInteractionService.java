package org.slackcoder.twilight.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserInteractionService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void logInteraction(String userId, String resourceId, String action) {
        String message = userId + " " + action + " " + resourceId;
        redisTemplate.opsForList().leftPush("user-interactions", message);
    }
}
