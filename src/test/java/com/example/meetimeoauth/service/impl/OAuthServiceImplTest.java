package com.example.meetimeoauth.service.impl;

import com.example.meetimeoauth.dto.TokenResponseDTO;
import com.example.meetimeoauth.exception.OAuthException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OAuthServiceImpl oAuthService;

    private static final String TEST_CLIENT_ID = "test-client-id";
    private static final String TEST_CLIENT_SECRET = "test-client-secret";
    private static final String TEST_REDIRECT_URL = "http://localhost:8080/callback";
    private static final String TEST_TOKEN_URL = "https://api.hubspot.com/oauth/v1/token";
    private static final String TEST_AUTHORIZATION_CODE = "test-auth-code";
    private static final String TEST_REFRESH_TOKEN = "test-refresh-token";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(oAuthService, "clientId", TEST_CLIENT_ID);
        ReflectionTestUtils.setField(oAuthService, "clientSecret", TEST_CLIENT_SECRET);
        ReflectionTestUtils.setField(oAuthService, "redirectUrl", TEST_REDIRECT_URL);
        ReflectionTestUtils.setField(oAuthService, "tokenUrl", TEST_TOKEN_URL);
    }

    @Test
    void exchangeCodeForToken_withValidCode_shouldReturnTokenResponse() throws Exception {
        // Arrange
        String expectedAccessToken = "test-access-token";
        String expectedRefreshToken = "test-refresh-token";
        String expectedTokenType = "bearer";
        int expectedExpiresIn = 3600;

        JsonNode mockResponse = createMockTokenResponse(
            expectedAccessToken,
            expectedRefreshToken,
            expectedTokenType,
            expectedExpiresIn
        );

        ResponseEntity<JsonNode> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(JsonNode.class)))
            .thenReturn(mockResponseEntity);

        // Act
        TokenResponseDTO result = oAuthService.exchangeCodeForToken(TEST_AUTHORIZATION_CODE);

        // Assert
        assertNotNull(result);
        assertEquals(expectedAccessToken, result.getAccessToken());
        assertEquals(expectedRefreshToken, result.getRefreshToken());
        assertEquals(expectedTokenType, result.getTokenType());
        assertEquals(expectedExpiresIn, result.getExpiresIn());

        verify(restTemplate).postForEntity(
            eq(TEST_TOKEN_URL),
            argThat(entity -> {
                HttpEntity<?> httpEntity = (HttpEntity<?>) entity;
                MultiValueMap<String, String> body = (MultiValueMap<String, String>) httpEntity.getBody();
                return body != null &&
                       "authorization_code".equals(body.getFirst("grant_type")) &&
                       TEST_CLIENT_ID.equals(body.getFirst("client_id")) &&
                       TEST_CLIENT_SECRET.equals(body.getFirst("client_secret")) &&
                       TEST_REDIRECT_URL.equals(body.getFirst("redirect_uri")) &&
                       TEST_AUTHORIZATION_CODE.equals(body.getFirst("code"));
            }),
            eq(JsonNode.class)
        );
    }

    @Test
    void exchangeCodeForToken_withInvalidResponse_shouldThrowOAuthException() {
        // Arrange
        ResponseEntity<JsonNode> mockResponseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(JsonNode.class)))
            .thenReturn(mockResponseEntity);

        // Act & Assert
        assertThrows(OAuthException.class, () -> {
            oAuthService.exchangeCodeForToken(TEST_AUTHORIZATION_CODE);
        });
    }

    @Test
    void refreshToken_withValidToken_shouldReturnTokenResponse() throws Exception {
        // Arrange
        String expectedAccessToken = "new-access-token";
        String expectedRefreshToken = "new-refresh-token";
        String expectedTokenType = "bearer";
        int expectedExpiresIn = 3600;

        JsonNode mockResponse = createMockTokenResponse(
            expectedAccessToken,
            expectedRefreshToken,
            expectedTokenType,
            expectedExpiresIn
        );

        ResponseEntity<JsonNode> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(JsonNode.class)))
            .thenReturn(mockResponseEntity);

        // Act
        TokenResponseDTO result = oAuthService.refreshToken(TEST_REFRESH_TOKEN);

        // Assert
        assertNotNull(result);
        assertEquals(expectedAccessToken, result.getAccessToken());
        assertEquals(expectedRefreshToken, result.getRefreshToken());
        assertEquals(expectedTokenType, result.getTokenType());
        assertEquals(expectedExpiresIn, result.getExpiresIn());

        verify(restTemplate).postForEntity(
            eq(TEST_TOKEN_URL),
            argThat(entity -> {
                HttpEntity<?> httpEntity = (HttpEntity<?>) entity;
                MultiValueMap<String, String> body = (MultiValueMap<String, String>) httpEntity.getBody();
                return body != null &&
                       "refresh_token".equals(body.getFirst("grant_type")) &&
                       TEST_CLIENT_ID.equals(body.getFirst("client_id")) &&
                       TEST_CLIENT_SECRET.equals(body.getFirst("client_secret")) &&
                       TEST_REFRESH_TOKEN.equals(body.getFirst("refresh_token"));
            }),
            eq(JsonNode.class)
        );
    }

    @Test
    void refreshToken_withInvalidResponse_shouldThrowOAuthException() {
        // Arrange
        ResponseEntity<JsonNode> mockResponseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(JsonNode.class)))
            .thenReturn(mockResponseEntity);

        // Act & Assert
        assertThrows(OAuthException.class, () -> {
            oAuthService.refreshToken(TEST_REFRESH_TOKEN);
        });
    }

    @Test
    void exchangeCodeForToken_withNullCode_shouldThrowOAuthException() {
        // Act & Assert
        assertThrows(OAuthException.class, () -> {
            oAuthService.exchangeCodeForToken(null);
        });
    }

    @Test
    void refreshToken_withNullToken_shouldThrowOAuthException() {
        // Act & Assert
        assertThrows(OAuthException.class, () -> {
            oAuthService.refreshToken(null);
        });
    }

    private JsonNode createMockTokenResponse(String accessToken, String refreshToken, String tokenType, int expiresIn) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = String.format(
            "{\"access_token\":\"%s\",\"refresh_token\":\"%s\",\"token_type\":\"%s\",\"expires_in\":%d}",
            accessToken, refreshToken, tokenType, expiresIn
        );
        return mapper.readTree(json);
    }
} 