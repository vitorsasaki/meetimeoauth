package com.example.meetimeoauth.dto;

/**
 * DTO para retornar a URL de autorização gerada para o fluxo OAuth
 */
public class AuthUrlResponseDTO {
    
    private String authorizationUrl;
    private String message;
    
    public AuthUrlResponseDTO() {
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 