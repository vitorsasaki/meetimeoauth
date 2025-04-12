package com.example.meetimeoauth.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    private static final String TEST_MESSAGE = "Mensagem de teste";
    private static final String TEST_CAUSE_MESSAGE = "Mensagem da causa";
    private static final long TEST_RETRY_AFTER = 10000L;

    @Test
    void apiException_withMessage_shouldCreateException() {
        // Act
        ApiException exception = new ApiException(TEST_MESSAGE);

        // Assert
        assertNotNull(exception);
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void apiException_withMessageAndCause_shouldCreateException() {
        // Arrange
        Throwable cause = new RuntimeException(TEST_CAUSE_MESSAGE);

        // Act
        ApiException exception = new ApiException(TEST_MESSAGE, cause);

        // Assert
        assertNotNull(exception);
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(TEST_CAUSE_MESSAGE, exception.getCause().getMessage());
    }

    @Test
    void oAuthException_withMessage_shouldCreateException() {
        // Act
        OAuthException exception = new OAuthException(TEST_MESSAGE);

        // Assert
        assertNotNull(exception);
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void oAuthException_withMessageAndCause_shouldCreateException() {
        // Arrange
        Throwable cause = new RuntimeException(TEST_CAUSE_MESSAGE);

        // Act
        OAuthException exception = new OAuthException(TEST_MESSAGE, cause);

        // Assert
        assertNotNull(exception);
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(TEST_CAUSE_MESSAGE, exception.getCause().getMessage());
    }

    @Test
    void rateLimitException_withMessageAndRetryAfter_shouldCreateException() {
        // Act
        RateLimitException exception = new RateLimitException(TEST_MESSAGE, TEST_RETRY_AFTER);

        // Assert
        assertNotNull(exception);
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertEquals(TEST_RETRY_AFTER, exception.getRetryAfter());
        assertNull(exception.getCause());
    }

    @Test
    void rateLimitException_withZeroRetryAfter_shouldCreateException() {
        // Act
        RateLimitException exception = new RateLimitException(TEST_MESSAGE, 0L);

        // Assert
        assertNotNull(exception);
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertEquals(0L, exception.getRetryAfter());
        assertNull(exception.getCause());
    }

    @Test
    void rateLimitException_withNegativeRetryAfter_shouldCreateException() {
        // Act
        RateLimitException exception = new RateLimitException(TEST_MESSAGE, -1L);

        // Assert
        assertNotNull(exception);
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertEquals(-1L, exception.getRetryAfter());
        assertNull(exception.getCause());
    }
} 