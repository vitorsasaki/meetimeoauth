package com.example.meetimeoauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.meetimeoauth.service.OAuthService;
import com.example.meetimeoauth.dto.TokenResponseDTO;
import com.example.meetimeoauth.dto.AuthUrlResponseDTO;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private OAuthService oauthService;
    
    @Value("${hubspot.client.id}")
    private String clientId;
    
    @Value("${hubspot.auth.url}")
    private String authUrl;
    
    @Value("${hubspot.redirect.url}")
    private String redirectUrl;

    /**
     * Endpoint responsável por gerar e retornar a URL de autorização
     * para iniciar o fluxo OAuth com o HubSpot.
     * 
     * @return ResponseEntity contendo a URL de autorização
     */
    @GetMapping("/login")
    public ResponseEntity<AuthUrlResponseDTO> initiateOAuth() {
        String authorizationUrl = authUrl + 
                "?client_id=" + clientId + 
                "&redirect_uri=" + redirectUrl + 
                "&scope=contacts%20oauth" +
                "&response_type=code";
        
        AuthUrlResponseDTO response = new AuthUrlResponseDTO();
        response.setAuthorizationUrl(authorizationUrl);
        response.setMessage("URL de autorização gerada com sucesso. Redirecione o usuário para esta URL para iniciar o fluxo OAuth.");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/callback")
    public ResponseEntity<TokenResponseDTO> handleCallback(@RequestParam("code") String code) {
        TokenResponseDTO tokenResponse = oauthService.exchangeCodeForToken(code);
        return ResponseEntity.ok(tokenResponse);
    }
} 