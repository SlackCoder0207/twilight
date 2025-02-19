package org.slackcoder.twilight.controller;

import org.slackcoder.twilight.dto.ApiResponse;
import org.slackcoder.twilight.model.Resource;
import org.slackcoder.twilight.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resource")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @PostMapping("/add")
    public ApiResponse<Resource> addResource(@RequestBody Resource resource) {
        Resource createdResource = resourceService.addResource(resource.getTitle(), resource.getDescription(),
                resource.getCategory(), resource.getUrl(), resource.getPublisher());
        return new ApiResponse<>(200, createdResource);
    }

    @GetMapping("/category/{category}")
    public ApiResponse<List<Resource>> getResourcesByCategory(@PathVariable String category) {
        List<Resource> resources = resourceService.getResourcesByCategory(category);
        return new ApiResponse<>(200, resources);
    }
}
