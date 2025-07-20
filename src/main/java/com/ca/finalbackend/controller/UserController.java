package com.ca.finalbackend.controller;

import com.ca.finalbackend.request.UserRequest;
import com.ca.finalbackend.request.LoginRequest;
import com.ca.finalbackend.model.User;
import com.ca.finalbackend.service.UserService;
import com.ca.finalbackend.security.JwtUtil;
import com.ca.finalbackend.response.ApiResponse;
import com.ca.finalbackend.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {
        User user = new User();
        user.setAccountName(userRequest.getAccountName());
        user.setPassword(userRequest.getPassword());
        user.setNickname(userRequest.getNickname());
        user.setEmail(userRequest.getEmail());
        user.setGender(userRequest.getGender());
        user.setBirth(userRequest.getBirth());
        user.setPhone(userRequest.getPhone());
        userService.register(user);
        return ResponseEntity.ok(new ApiResponse(true, "회원가입 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.findByAccountName(loginRequest.getAccountName());
        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
        String token = jwtUtil.generateToken(user.getId());
        return ResponseEntity.ok(new LoginResponse(true, token, user.getNickname()));
    }

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(new ApiResponse(false, "Authorization 헤더가 없거나 형식이 올바르지 않습니다."));
            }
            String token = authHeader.substring(7);
            boolean isValid = jwtUtil.validateToken(token);

            if (isValid) {
                Integer userId = jwtUtil.getUserId(token);
                User user = userService.findById(userId);

                return ResponseEntity.ok(new ApiResponse(true, user.getNickname()));
            } else {
                return ResponseEntity.status(401).body(new ApiResponse(false, "토큰이 유효하지 않습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "토큰 검증 중 오류가 발생했습니다."));
        }
    }
}
