package com.example.demo.dto;

public record TokenResponse(
        String username,
        String accessToken,
        String refreshToken
){}