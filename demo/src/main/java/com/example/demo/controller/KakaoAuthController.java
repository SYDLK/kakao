package com.example.demo.controller;

import com.example.demo.dto.TokenResponse;
import com.example.demo.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @Operation(summary = "카카오 로그인")
    @GetMapping("/kakao/callback")
    public TokenResponse kakaoLogin(@RequestParam String code) {
        return kakaoAuthService.kakaoLogin(code);
    }

    @Operation(summary = "카카오 로그아웃")
    @PostMapping("/kakao/logout")
    public ResponseEntity<String> kakaoLogout(@RequestParam("accessToken") String accessToken) {
        return kakaoAuthService.kakaoLogout(accessToken);
    }
}
