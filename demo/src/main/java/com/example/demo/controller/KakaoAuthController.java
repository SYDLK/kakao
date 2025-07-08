package com.example.demo.controller;

import com.example.demo.dto.TokenResponse;
import com.example.demo.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @Operation(summary = "카카오 로그인 콜백", description = "인가코드(code)를 받아 JWT 토큰 발급")
    @GetMapping("/kakao/callback")
    public TokenResponse kakaoLogin(@RequestParam String code) {
        return kakaoAuthService.kakaoLogin(code);
    }
}
