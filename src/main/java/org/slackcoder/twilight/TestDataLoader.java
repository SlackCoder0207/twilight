package org.slackcoder.twilight;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.slackcoder.twilight.model.User;
import org.slackcoder.twilight.model.Resource;
import org.slackcoder.twilight.repository.UserRepository;
import org.slackcoder.twilight.repository.ResourceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Order(1)
public class TestDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final Driver neo4jDriver;

    public TestDataLoader(UserRepository userRepository, ResourceRepository resourceRepository, Driver neo4jDriver) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.neo4jDriver = neo4jDriver;
    }

    @Override
    public void run(String... args) throws Exception {
        // 清空数据库
        userRepository.deleteAll();
        resourceRepository.deleteAll();
        try (Session session = neo4jDriver.session(SessionConfig.forDatabase("twilight"))) {
            session.run("MATCH (n) DETACH DELETE n");
        }
        System.out.println("Cleared existing nodes.");

        // 创建50个用户
        List<String> usernames = Arrays.asList("Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Hannah", "Ivy", "Jack");
        List<User> users = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= 50; i++) {
            String username = usernames.get(random.nextInt(usernames.size())) + i;
            User user = new User(username, username.toLowerCase() + "@example.com", "Password1", 0);
            users.add(user);
        }
        userRepository.saveAll(users);
        System.out.println("Saved " + users.size() + " users.");

        // 创建50个资源
        List<String> titles = Arrays.asList("Java Basics", "Spring Boot Guide", "AI Fundamentals", "Web Development 101", "Machine Learning Intro");
        List<String> descriptions = Arrays.asList(
                "A comprehensive introduction to Java programming.",
                "Step-by-step guide to using Spring Boot.",
                "Understanding the fundamentals of AI and deep learning.",
                "How to build modern web applications from scratch.",
                "Introduction to machine learning concepts and algorithms."
        );
        String[] categories = {"Java", "Spring Boot", "AI", "Machine Learning", "Web Development"};
        String[] types = {"video", "post", "audio"};

        List<Resource> resources = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            User publisher = users.get(random.nextInt(users.size()));
            String title = titles.get(random.nextInt(titles.size()));
            String description = descriptions.get(random.nextInt(descriptions.size()));
            String category = categories[random.nextInt(categories.length)];
            String type = types[random.nextInt(types.length)];
            String url = type.equals("video") ? "video.mp4" : type.equals("post") ? "text.md" : "audio.mp3";

            Resource resource = new Resource(title, description, category, url, publisher.getUserId(), type);
            resources.add(resource);
        }
        resourceRepository.saveAll(resources);
        System.out.println("Saved " + resources.size() + " resources.");

        //
        int totalInteractions;
        try (Session session = neo4jDriver.session(SessionConfig.forDatabase("twilight"))) {
            totalInteractions = 500 + random.nextInt(200);
            for (int i = 0; i < totalInteractions; i++) {
                User user = users.get(random.nextInt(users.size()));
                Resource resource = resources.get(random.nextInt(resources.size()));

                int liked = random.nextInt(3) - 1; //-1点踩, 0无交互, 1点赞
                int favorite = random.nextBoolean() ? 1 : 0;
                int weight = 1 + random.nextInt(10);

                Map<String, Object> params = new HashMap<>();
                params.put("userId", user.getUserId().toString());
                params.put("resourceId", resource.getResourceId().toString());
                params.put("liked", liked);
                params.put("favorite", favorite);
                params.put("weight", weight);

                session.run(
                        "MATCH (u:User {userId: $userId}), (r:Resource {resourceId: $resourceId}) " +
                                "MERGE (u)-[rel:INTERACTS_WITH]->(r) " +
                                "SET rel.liked = $liked, rel.favorite = $favorite, rel.weight = $weight",
                        params);
                System.out.println("Created interaction: " + user.getUserId() + " -> " + resource.getResourceId());
            }
        }
        System.out.println("Created " + totalInteractions + " user-resource interactions.");
    }
}
