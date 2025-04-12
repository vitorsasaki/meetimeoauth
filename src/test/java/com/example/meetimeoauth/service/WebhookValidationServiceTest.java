package com.example.meetimeoauth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookValidationServiceTest {

    @InjectMocks
    private WebhookValidationService validationService;

    private static final String TEST_CLIENT_SECRET = "test-client-secret";
    private static final String TEST_REQUEST_BODY = "test-request-body";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(validationService, "clientSecret", TEST_CLIENT_SECRET);
    }

    @Test
    void isValidSignatureV1_withValidSignature_shouldReturnTrue() throws Exception {
        // Arrange
        String requestBody = TEST_REQUEST_BODY;
        
        // Calcular a assinatura correta
        String sourceString = TEST_CLIENT_SECRET + requestBody;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(sourceString.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder calculatedSignature = new StringBuilder();
        for (byte b : hash) {
            calculatedSignature.append(String.format("%02x", b));
        }
        String signature = calculatedSignature.toString();

        // Act
        boolean result = validationService.isValidSignatureV1(requestBody, signature);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidSignatureV1_withInvalidSignature_shouldReturnFalse() {
        // Arrange
        String requestBody = TEST_REQUEST_BODY;
        String signature = "invalid-signature";

        // Act
        boolean result = validationService.isValidSignatureV1(requestBody, signature);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidSignatureV1_withNullRequestBody_shouldReturnFalse() {
        // Arrange
        String signature = TEST_REQUEST_BODY;

        // Act
        boolean result = validationService.isValidSignatureV1(null, signature);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidSignatureV1_withNullSignature_shouldReturnFalse() {
        // Arrange
        String requestBody = TEST_REQUEST_BODY;

        // Act
        boolean result = validationService.isValidSignatureV1(requestBody, null);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidSignatureV1_withEmptyRequestBody_shouldReturnFalse() {
        // Arrange
        String requestBody = "";
        String signature = TEST_REQUEST_BODY;

        // Act
        boolean result = validationService.isValidSignatureV1(requestBody, signature);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidSignatureV1_withEmptySignature_shouldReturnFalse() {
        // Arrange
        String requestBody = TEST_REQUEST_BODY;
        String signature = "";

        // Act
        boolean result = validationService.isValidSignatureV1(requestBody, signature);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidSignatureV1_withNullClientSecret_shouldReturnFalse() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "clientSecret", null);
        String requestBody = TEST_REQUEST_BODY;
        String signature = TEST_REQUEST_BODY;

        // Act
        boolean result = validationService.isValidSignatureV1(requestBody, signature);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidSignatureV1_withEmptyClientSecret_shouldReturnFalse() {
        // Arrange
        ReflectionTestUtils.setField(validationService, "clientSecret", "");
        String requestBody = TEST_REQUEST_BODY;
        String signature = TEST_REQUEST_BODY;

        // Act
        boolean result = validationService.isValidSignatureV1(requestBody, signature);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidSignatureV1_withDifferentCaseSignature_shouldReturnTrue() throws Exception {
        // Arrange
        String requestBody = TEST_REQUEST_BODY;
        
        // Calcular a assinatura correta
        String sourceString = TEST_CLIENT_SECRET + requestBody;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(sourceString.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder calculatedSignature = new StringBuilder();
        for (byte b : hash) {
            calculatedSignature.append(String.format("%02x", b));
        }
        // Converter para maiúsculas para testar a comparação case-insensitive
        String signature = calculatedSignature.toString().toUpperCase();

        // Act
        boolean result = validationService.isValidSignatureV1(requestBody, signature);

        // Assert
        assertTrue(result);
    }
} 