package org.slackcoder.twilight.controller;

import org.slackcoder.twilight.dto.ApiResponse;
import org.slackcoder.twilight.exception.ResourceNotFoundException;
import org.slackcoder.twilight.model.User;
import org.slackcoder.twilight.security.JwtUtil;
import org.slackcoder.twilight.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@PreAuthorize("permitAll()")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil; //使用JWT+Spring Security进行鉴权

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody User user) {
        User createdUser = userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword(), user.getUserType());
        return new ApiResponse<>(200, createdUser);
    }

    @GetMapping("/login")
    public ApiResponse<String> login(@RequestBody User user) {
        String token = jwtUtil.generateToken(user.getUserId().toString());
        return new ApiResponse<>(200, token);
    }

    @GetMapping("/{userId}")
    public ApiResponse<User> getUserById(@PathVariable UUID userId) {
        return userService.findById(userId)
                .map(user -> new ApiResponse<>(200, user))
                .orElseThrow(() -> new ResourceNotFoundException("用户未找到"));
    }



//    @GetMapping("/{email}")
//    public ApiResponse<User> getUserByEmail(@PathVariable String email) {
//        Optional<User> user = userService.findByEmail(email);
//        return user.map(value -> new ApiResponse<>(200, value))
//                .orElseThrow(() -> new ResourceNotFoundException("用户未找到"));
//    }

}
