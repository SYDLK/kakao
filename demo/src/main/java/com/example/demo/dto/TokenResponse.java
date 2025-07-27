package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class TokenResponse {
    private String nickname;
    private String accessToken;
    private String refreshToken;
    private String kakaoAccessToken;
}
