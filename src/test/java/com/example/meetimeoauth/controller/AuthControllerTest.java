package com.example.meetimeoauth.controller;

import com.example.meetimeoauth.dto.AuthUrlResponseDTO;
import com.example.meetimeoauth.dto.TokenResponseDTO;
import com.example.meetimeoauth.exception.OAuthException;
import com.example.meetimeoauth.service.OAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private OAuthService oauthService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        // Configurar valores das propriedades
        String clientId = "test-client-id";
        String authUrl = "https://test.auth.url";
        String redirectUrl = "https://test.redirect.url";
        String scopes = "test-scope1 test-scope2";
        
        authController = new AuthController(oauthService, clientId, authUrl, redirectUrl, scopes);
    }

    @Test
    void initiateOAuth_shouldReturnValidAuthUrl() {
        // Act
        ResponseEntity<AuthUrlResponseDTO> response = authController.initiateOAuth();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getAuthorizationUrl().contains("test-client-id"));
        assertTrue(response.getBody().getAuthorizationUrl().contains("test.redirect.url"));
        assertTrue(response.getBody().getAuthorizationUrl().contains("test-scope1"));
        assertTrue(response.getBody().getAuthorizationUrl().contains("test-scope2"));
    }

    @Test
    void handleCallback_withValidCode_shouldReturnTokenResponse() throws OAuthException {
        // Arrange
        String testCode = "test-code";
        TokenResponseDTO mockTokenResponse = new TokenResponseDTO();
        mockTokenResponse.setAccessToken("test-access-token");
        mockTokenResponse.setTokenType("Bearer");
        
        when(oauthService.exchangeCodeForToken(anyString())).thenReturn(mockTokenResponse);

        // Act
        ResponseEntity<?> response = authController.handleCallback(testCode, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Autenticação realizada com sucesso", responseBody.get("message"));
        assertNotNull(responseBody.get("token"));
        assertEquals("Bearer test-access-token", responseBody.get("authorization"));
        
        verify(oauthService, times(1)).exchangeCodeForToken(testCode);
    }

    @Test
    void handleCallback_withError_shouldReturnErrorResponse() {
        // Arrange
        String testError = "access_denied";

        // Act
        ResponseEntity<?> response = authController.handleCallback(null, testError);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(testError, responseBody.get("error"));
        assertEquals("Falha na autorização com o HubSpot", responseBody.get("message"));
        
        verify(oauthService, never()).exchangeCodeForToken(anyString());
    }

    @Test
    void handleCallback_withEmptyCode_shouldReturnErrorResponse() {
        // Act
        ResponseEntity<?> response = authController.handleCallback("", null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("invalid_request", responseBody.get("error"));
        assertEquals("Código de autorização não fornecido", responseBody.get("message"));
        
        verify(oauthService, never()).exchangeCodeForToken(anyString());
    }

    @Test
    void handleCallback_whenOAuthException_shouldReturnErrorResponse() throws OAuthException {
        // Arrange
        String testCode = "test-code";
        when(oauthService.exchangeCodeForToken(anyString()))
            .thenThrow(new OAuthException("Erro de autenticação"));

        // Act
        ResponseEntity<?> response = authController.handleCallback(testCode, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("token_exchange_error", responseBody.get("error"));
        assertEquals("Erro de autenticação", responseBody.get("message"));
        
        verify(oauthService, times(1)).exchangeCodeForToken(testCode);
    }
} 