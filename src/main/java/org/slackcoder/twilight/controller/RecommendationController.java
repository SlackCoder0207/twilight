package org.slackcoder.twilight.controller;

import org.slackcoder.twilight.dto.ApiResponse;
import org.slackcoder.twilight.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@CrossOrigin(origins = "*")
@PreAuthorize("permitAll()")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/{userId}")
    public ApiResponse<List<Map<String, Object>>> getRecommendations(@PathVariable String userId) {
        return new ApiResponse<>(200, recommendationService.recommendResources(userId));
    }

    //返回交互数最多的前6个资源
    @GetMapping("/popular")
    public ApiResponse<List<Map<String, Object>>> getPopularResources() {
        return new ApiResponse<>(200, recommendationService.getPopularResources());
    }
}
