package com.example.meetimeoauth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthUrlResponseDTOTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        AuthUrlResponseDTO response = new AuthUrlResponseDTO();
        String url = "https://test.auth.url";
        String message = "Test message";

        // Act
        response.setAuthorizationUrl(url);
        response.setMessage(message);

        // Assert
        assertEquals(url, response.getAuthorizationUrl());
        assertEquals(message, response.getMessage());
    }

    @Test
    void testNullValues() {
        // Arrange
        AuthUrlResponseDTO response = new AuthUrlResponseDTO();

        // Act
        response.setAuthorizationUrl(null);
        response.setMessage(null);

        // Assert
        assertNull(response.getAuthorizationUrl());
        assertNull(response.getMessage());
    }

    @Test
    void testEmptyValues() {
        // Arrange
        AuthUrlResponseDTO response = new AuthUrlResponseDTO();

        // Act
        response.setAuthorizationUrl("");
        response.setMessage("");

        // Assert
        assertEquals("", response.getAuthorizationUrl());
        assertEquals("", response.getMessage());
    }
} 