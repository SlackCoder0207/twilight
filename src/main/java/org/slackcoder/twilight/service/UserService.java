package org.slackcoder.twilight.service;

import org.slackcoder.twilight.model.User;
import org.slackcoder.twilight.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public User registerUser(String username, String email, String password, int userType) {
        User user = new User(username, email, password, userType);
        userRepository.save(user);
        return user;
    }

    public Optional<User> findById(UUID userId) {
        String cacheKey = "user:" + userId;

        //先从Redis缓存中查找
        User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);
        if (cachedUser != null) {
            return Optional.of(cachedUser);
        }

        //数据库查询
        Optional<User> user = userRepository.findById(userId);
        user.ifPresent(u -> redisTemplate.opsForValue().set(cacheKey, u, Duration.ofHours(1)));

        return user;
    }
}
