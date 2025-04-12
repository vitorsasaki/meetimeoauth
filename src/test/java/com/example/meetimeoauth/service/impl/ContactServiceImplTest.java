package com.example.meetimeoauth.service.impl;

import com.example.meetimeoauth.dto.ContactDTO;
import com.example.meetimeoauth.exception.ApiException;
import com.example.meetimeoauth.exception.RateLimitException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ContactServiceImpl contactService;

    private static final String TEST_BASE_URL = "https://api.hubapi.com";
    private static final String TEST_AUTH_TOKEN = "Bearer test-token";
    private static final String TEST_CONTACT_ID = "123";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_PHONE = "1234567890";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(contactService, "baseUrl", TEST_BASE_URL);
    }

    @Test
    void listContacts_withValidParams_shouldReturnContacts() {
        // Arrange
        int offset = 0;
        int limit = 10;
        JsonNode mockResponse = mock(JsonNode.class);
        ResponseEntity<JsonNode> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(mockResponseEntity);

        // Act
        Object result = contactService.listContacts(TEST_AUTH_TOKEN, offset, limit);

        // Assert
        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(restTemplate).exchange(
            eq(TEST_BASE_URL + "/crm/v3/objects/contacts?limit=10&after=0"),
            eq(HttpMethod.GET),
            argThat(entity -> {
                HttpHeaders headers = ((HttpEntity<?>) entity).getHeaders();
                return headers.getFirst("Authorization").equals(TEST_AUTH_TOKEN);
            }),
            eq(JsonNode.class)
        );
    }

    @Test
    void listContacts_withInvalidResponse_shouldThrowApiException() {
        // Arrange
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        // Act & Assert
        assertThrows(ApiException.class, () -> {
            contactService.listContacts(TEST_AUTH_TOKEN, 0, 10);
        });
    }

    @Test
    void getContact_withValidId_shouldReturnContact() {
        // Arrange
        JsonNode mockResponse = mock(JsonNode.class);
        ResponseEntity<JsonNode> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(mockResponseEntity);

        // Act
        Object result = contactService.getContact(TEST_AUTH_TOKEN, TEST_CONTACT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(restTemplate).exchange(
            eq(TEST_BASE_URL + "/crm/v3/objects/contacts/" + TEST_CONTACT_ID),
            eq(HttpMethod.GET),
            argThat(entity -> {
                HttpHeaders headers = ((HttpEntity<?>) entity).getHeaders();
                return headers.getFirst("Authorization").equals(TEST_AUTH_TOKEN);
            }),
            eq(JsonNode.class)
        );
    }

    @Test
    void getContact_withInvalidResponse_shouldThrowApiException() {
        // Arrange
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        // Act & Assert
        assertThrows(ApiException.class, () -> {
            contactService.getContact(TEST_AUTH_TOKEN, TEST_CONTACT_ID);
        });
    }

    @Test
    void createContact_withValidData_shouldReturnCreatedContact() throws Exception {
        // Arrange
        ContactDTO contact = new ContactDTO();
        contact.setEmail(TEST_EMAIL);
        contact.setFirstName(TEST_FIRST_NAME);
        contact.setLastName(TEST_LAST_NAME);
        contact.setPhone(TEST_PHONE);

        ObjectNode mockRequestBody = mock(ObjectNode.class);
        ObjectNode mockProperties = mock(ObjectNode.class);
        JsonNode mockResponse = mock(JsonNode.class);
        ResponseEntity<JsonNode> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(objectMapper.createObjectNode()).thenReturn(mockRequestBody);
        when(mockRequestBody.put(eq("email"), eq(TEST_EMAIL))).thenReturn(mockRequestBody);
        when(mockRequestBody.put(eq("firstname"), eq(TEST_FIRST_NAME))).thenReturn(mockRequestBody);
        when(mockRequestBody.put(eq("lastname"), eq(TEST_LAST_NAME))).thenReturn(mockRequestBody);
        when(mockRequestBody.put(eq("phone"), eq(TEST_PHONE))).thenReturn(mockRequestBody);
        when(mockRequestBody.set(eq("properties"), any())).thenReturn(mockRequestBody);
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(mockResponseEntity);

        // Act
        Object result = contactService.createContact(TEST_AUTH_TOKEN, contact);

        // Assert
        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(restTemplate).exchange(
            eq(TEST_BASE_URL + "/crm/v3/objects/contacts"),
            eq(HttpMethod.POST),
            argThat(entity -> {
                HttpHeaders headers = ((HttpEntity<?>) entity).getHeaders();
                return headers.getFirst("Authorization").equals(TEST_AUTH_TOKEN) &&
                       headers.getContentType().equals(MediaType.APPLICATION_JSON);
            }),
            eq(JsonNode.class)
        );
    }


    @Test
    void createContactsBatch_withEmptyList_shouldThrowApiException() {
        // Arrange
        List<ContactDTO> contacts = new ArrayList<>();

        // Act & Assert
        assertThrows(ApiException.class, () -> {
            contactService.createContactsBatch(TEST_AUTH_TOKEN, contacts);
        });
    }
} 