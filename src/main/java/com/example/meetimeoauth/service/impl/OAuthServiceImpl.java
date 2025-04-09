package com.example.meetimeoauth.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.meetimeoauth.dto.TokenResponseDTO;
import com.example.meetimeoauth.exception.OAuthException;
import com.example.meetimeoauth.service.OAuthService;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class OAuthServiceImpl implements OAuthService {

    @Value("${hubspot.client.id}")
    private String clientId;
    
    @Value("${hubspot.client.secret}")
    private String clientSecret;
    
    @Value("${hubspot.redirect.url}")
    private String redirectUrl;
    
    @Value("${hubspot.token.url}")
    private String tokenUrl;
    
    private final RestTemplate restTemplate;
    
    public OAuthServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public TokenResponseDTO exchangeCodeForToken(String authorizationCode) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", "authorization_code");
            requestBody.add("client_id", clientId);
            requestBody.add("client_secret", clientSecret);
            requestBody.add("redirect_uri", redirectUrl);
            requestBody.add("code", authorizationCode);
            
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    tokenUrl, 
                    requestEntity, 
                    JsonNode.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapTokenResponse(response.getBody());
            } else {
                throw new OAuthException("Falha ao trocar código por token: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new OAuthException("Erro ao trocar código por token: " + e.getMessage(), e);
        }
    }

    @Override
    public TokenResponseDTO refreshToken(String refreshToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", "refresh_token");
            requestBody.add("client_id", clientId);
            requestBody.add("client_secret", clientSecret);
            requestBody.add("refresh_token", refreshToken);
            
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    tokenUrl, 
                    requestEntity, 
                    JsonNode.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapTokenResponse(response.getBody());
            } else {
                throw new OAuthException("Falha ao atualizar token: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new OAuthException("Erro ao atualizar token: " + e.getMessage(), e);
        }
    }
    
    private TokenResponseDTO mapTokenResponse(JsonNode responseBody) {
        TokenResponseDTO tokenResponse = new TokenResponseDTO();
        tokenResponse.setAccessToken(responseBody.get("access_token").asText());
        tokenResponse.setRefreshToken(responseBody.get("refresh_token").asText());
        tokenResponse.setTokenType(responseBody.get("token_type").asText());
        tokenResponse.setExpiresIn(responseBody.get("expires_in").asInt());
        return tokenResponse;
    }
} 