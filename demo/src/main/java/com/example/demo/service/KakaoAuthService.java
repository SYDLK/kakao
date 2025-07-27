package com.example.demo.service;

import com.example.demo.MemberRepository;
import com.example.demo.dto.TokenResponse;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.user-info-uri}")
    private String userInfoUri;


    public TokenResponse kakaoLogin(String code) {
        RestTemplate restTemplate = new RestTemplate();

        // 1) 인가코드 → 카카오 access token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);
        ResponseEntity<Map> tokenResponse =
                restTemplate.postForEntity(tokenUri, tokenRequest, Map.class);

        String kakaoAccessToken = (String) tokenResponse.getBody().get("access_token");

        // 2) 사용자 정보 조회
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(kakaoAccessToken);
        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userHeaders);

        ResponseEntity<Map> userResponse =
                restTemplate.exchange(userInfoUri, HttpMethod.GET, userInfoRequest, Map.class);

        Map<String, Object> bodyMap = userResponse.getBody();
        Long kakaoId = ((Number) bodyMap.get("id")).longValue();

        Map<String, Object> properties =
                (Map<String, Object>) bodyMap.get("properties");
        String nickname = (String) properties.get("nickname");

        // 3) DB 저장 or 조회
        Member member = memberRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> memberRepository.save(new Member(null, kakaoId, nickname)));

        // 4) JWT 발급
        String accessToken = jwtUtil.generateAccessToken(nickname);
        String refreshToken = jwtUtil.generateRefreshToken(nickname);

        return new TokenResponse(nickname, accessToken, refreshToken, kakaoAccessToken);
    }
    public ResponseEntity<String> kakaoLogout(String bearerToken) {
        String logoutUrl = "https://kapi.kakao.com/v1/user/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(logoutUrl, request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("카카오 로그아웃 실패");
        }
    }
}