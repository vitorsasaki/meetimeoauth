package com.example.meetimeoauth.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    private WebRequest webRequest;
    private static final String TEST_MESSAGE = "Mensagem de teste";
    private static final long TEST_RETRY_AFTER = 10000L;

    @BeforeEach
    void setUp() {
        webRequest = new ServletWebRequest(new MockHttpServletRequest());
    }

    @Test
    void handleOAuthException_shouldReturnBadRequestResponse() {
        // Arrange
        OAuthException exception = new OAuthException(TEST_MESSAGE);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleOAuthException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body.get("timestamp"));
        assertEquals(TEST_MESSAGE, body.get("message"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("OAuth Error", body.get("error"));
    }

    @Test
    void handleApiException_shouldReturnBadRequestResponse() {
        // Arrange
        ApiException exception = new ApiException(TEST_MESSAGE);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleApiException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body.get("timestamp"));
        assertEquals(TEST_MESSAGE, body.get("message"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("API Error", body.get("error"));
    }

    @Test
    void handleRateLimitException_shouldReturnTooManyRequestsResponse() {
        // Arrange
        RateLimitException exception = new RateLimitException(TEST_MESSAGE, TEST_RETRY_AFTER);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleRateLimitException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body.get("timestamp"));
        assertEquals(TEST_MESSAGE, body.get("message"));
        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), body.get("status"));
        assertEquals("Rate Limit Exceeded", body.get("error"));
        assertEquals(TEST_RETRY_AFTER, body.get("retryAfter"));

        // Verificar o cabeçalho Retry-After
        assertTrue(response.getHeaders().containsKey("Retry-After"));
        assertEquals(String.valueOf(TEST_RETRY_AFTER / 1000), response.getHeaders().getFirst("Retry-After"));
    }

    @Test
    void handleGenericException_shouldReturnInternalServerErrorResponse() {
        // Arrange
        Exception exception = new Exception(TEST_MESSAGE);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleGenericException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body.get("timestamp"));
        assertEquals("Ocorreu um erro interno", body.get("message"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
    }

    @Test
    void handleOAuthException_withNullMessage_shouldReturnBadRequestResponse() {
        // Arrange
        OAuthException exception = new OAuthException(null);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleOAuthException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body.get("timestamp"));
        assertNull(body.get("message"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("OAuth Error", body.get("error"));
    }

    @Test
    void handleRateLimitException_withZeroRetryAfter_shouldReturnTooManyRequestsResponse() {
        // Arrange
        RateLimitException exception = new RateLimitException(TEST_MESSAGE, 0L);

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleRateLimitException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(0L, body.get("retryAfter"));
        assertEquals("0", response.getHeaders().getFirst("Retry-After"));
    }
} 