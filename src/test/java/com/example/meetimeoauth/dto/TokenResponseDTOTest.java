package com.example.meetimeoauth.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TokenResponseDTOTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        TokenResponseDTO token = new TokenResponseDTO();
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";
        String tokenType = "Bearer";
        Integer expiresIn = 3600;
        LocalDateTime createdAt = LocalDateTime.now();

        // Act
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setTokenType(tokenType);
        token.setExpiresIn(expiresIn);
        token.setCreatedAt(createdAt);

        // Assert
        assertEquals(accessToken, token.getAccessToken());
        assertEquals(refreshToken, token.getRefreshToken());
        assertEquals(tokenType, token.getTokenType());
        assertEquals(expiresIn, token.getExpiresIn());
        assertEquals(createdAt, token.getCreatedAt());
    }

    @Test
    void testDefaultCreatedAt() {
        // Arrange
        TokenResponseDTO token = new TokenResponseDTO();

        // Assert
        assertNotNull(token.getCreatedAt());
        assertTrue(token.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testNullValues() {
        // Arrange
        TokenResponseDTO token = new TokenResponseDTO();

        // Act
        token.setAccessToken(null);
        token.setRefreshToken(null);
        token.setTokenType(null);
        token.setExpiresIn(null);
        token.setCreatedAt(null);

        // Assert
        assertNull(token.getAccessToken());
        assertNull(token.getRefreshToken());
        assertNull(token.getTokenType());
        assertNull(token.getExpiresIn());
        assertNull(token.getCreatedAt());
    }

    @Test
    void testEmptyValues() {
        // Arrange
        TokenResponseDTO token = new TokenResponseDTO();

        // Act
        token.setAccessToken("");
        token.setRefreshToken("");
        token.setTokenType("");

        // Assert
        assertEquals("", token.getAccessToken());
        assertEquals("", token.getRefreshToken());
        assertEquals("", token.getTokenType());
    }
} 