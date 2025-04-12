package com.example.meetimeoauth.controller;

import com.example.meetimeoauth.service.WebhookValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

    @Mock
    private WebhookValidationService validationService;

    @Mock
    private HttpServletRequest request;

    private WebhookController webhookController;

    @BeforeEach
    void setUp() {
        webhookController = new WebhookController(validationService);
        when(request.getRequestURI()).thenReturn("/api/webhook/contact-creation");
    }

    @Test
    void handleContactCreation_withValidSignature_shouldReturnSuccess() {
        // Arrange
        String signature = "valid-signature";
        String requestBody = "test-body";
        when(validationService.isValidSignatureV1(anyString(), anyString())).thenReturn(true);

        // Act
        ResponseEntity<String> response = webhookController.handleContactCreation(signature, request, requestBody);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Webhook recebido e validado com sucesso", response.getBody());
        verify(validationService, times(1)).isValidSignatureV1(requestBody, signature);
    }

    @Test
    void handleContactCreation_withMissingSignature_shouldReturnBadRequest() {
        // Arrange
        String requestBody = "test-body";

        // Act
        ResponseEntity<String> response = webhookController.handleContactCreation(null, request, requestBody);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Assinatura ausente", response.getBody());
        verify(validationService, never()).isValidSignatureV1(anyString(), anyString());
    }

    @Test
    void handleContactCreation_withEmptySignature_shouldReturnBadRequest() {
        // Arrange
        String signature = "";
        String requestBody = "test-body";

        // Act
        ResponseEntity<String> response = webhookController.handleContactCreation(signature, request, requestBody);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Assinatura ausente", response.getBody());
        verify(validationService, never()).isValidSignatureV1(anyString(), anyString());
    }

    @Test
    void handleContactCreation_withInvalidSignature_shouldReturnBadRequest() {
        // Arrange
        String signature = "invalid-signature";
        String requestBody = "test-body";
        when(validationService.isValidSignatureV1(anyString(), anyString())).thenReturn(false);

        // Act
        ResponseEntity<String> response = webhookController.handleContactCreation(signature, request, requestBody);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Assinatura inválida", response.getBody());
        verify(validationService, times(1)).isValidSignatureV1(requestBody, signature);
    }
} 