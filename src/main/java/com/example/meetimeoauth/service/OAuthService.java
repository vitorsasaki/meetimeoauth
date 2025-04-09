package com.example.meetimeoauth.service;

import com.example.meetimeoauth.dto.TokenResponseDTO;

public interface OAuthService {
    
    /**
     * Troca o código de autorização por um token de acesso
     * 
     * @param authorizationCode código obtido após redirecionamento do HubSpot
     * @return objeto contendo as informações do token
     */
    TokenResponseDTO exchangeCodeForToken(String authorizationCode);
    
    /**
     * Atualiza o token de acesso usando o refresh token
     * 
     * @param refreshToken token de atualização
     * @return novo objeto contendo as informações do token atualizado
     */
    TokenResponseDTO refreshToken(String refreshToken);
} 