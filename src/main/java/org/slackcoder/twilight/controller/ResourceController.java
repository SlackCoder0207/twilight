package org.slackcoder.twilight.controller;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.slackcoder.twilight.dto.ApiResponse;
import org.slackcoder.twilight.model.Resource;
import org.slackcoder.twilight.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resource")
@CrossOrigin(origins = "*")
@PreAuthorize("permitAll()")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    private final Driver neo4jDriver;

    public ResourceController(Driver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    @PostMapping("/add")
    public ApiResponse<Resource> addResource(@RequestBody Resource resource) {
        Resource createdResource = resourceService.addResource(resource.getTitle(), resource.getDescription(),
                resource.getCategory(), resource.getUrl(), resource.getPublisher(), resource.getType());
        return new ApiResponse<>(200, createdResource);
    }

    @GetMapping("/category/{category}")
    public ApiResponse<List<Resource>> getResourcesByCategory(@PathVariable String category) {
        List<Resource> resources = resourceService.getResourcesByCategory(category);
        return new ApiResponse<>(200, resources);
    }

    @GetMapping("/resDetail/{resourceId}")
    public ApiResponse<Map<String, Object>> getResourceDetail(@PathVariable String resourceId) {
        try (Session session = neo4jDriver.session(SessionConfig.forDatabase("twilight"))) {
            var result = session.run(
                    "MATCH (res:Resource {resourceId: $resourceId}) " +
                            "OPTIONAL MATCH (u:User)-[r:INTERACTS_WITH]->(res) " +
                            "WITH res, " +
                            "count(r) AS views, " +
                            "sum(case when r.liked = 1 then 1 else 0 end) AS likes, " +
                            "sum(case when r.liked = -1 then 1 else 0 end) AS dislikes, " +
                            "sum(case when r.favorite = 1 then 1 else 0 end) AS favorites " +
                            "RETURN res {.*, views: views, likes: likes, dislikes: dislikes, favorites: favorites} AS resource",
                    Collections.singletonMap("resourceId", resourceId)
            );
            if (result.hasNext()) {
                return new ApiResponse<>(200, result.next().get("resource").asMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(500, null);
        }
        return new ApiResponse<>(404, null);
    }

}
