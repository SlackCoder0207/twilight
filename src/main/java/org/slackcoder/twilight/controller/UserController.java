package org.slackcoder.twilight.controller;

import org.slackcoder.twilight.dto.ApiResponse;
import org.slackcoder.twilight.model.User;
import org.slackcoder.twilight.security.JwtUtil;
import org.slackcoder.twilight.service.UserService;
import org.slackcoder.twilight.utils.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody User user) {
        User createdUser = userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword(), user.getUserType());
        return new ApiResponse<>(200, createdUser);
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody User user) {
        String token = jwtUtil.generateToken(user.getUserId().toString());
        return new ApiResponse<>(200, token);
    }

    @GetMapping("/{email}")
    public ApiResponse<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(value -> new ApiResponse<>(200, value))
                .orElseThrow(() -> new ResourceNotFoundException("用户未找到"));
    }

}
