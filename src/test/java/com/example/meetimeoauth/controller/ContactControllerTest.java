package com.example.meetimeoauth.controller;

import com.example.meetimeoauth.dto.ContactDTO;
import com.example.meetimeoauth.dto.BatchContactRequestDTO;
import com.example.meetimeoauth.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactControllerTest {

    @Mock
    private ContactService contactService;

    private ContactController contactController;

    @BeforeEach
    void setUp() {
        contactController = new ContactController(contactService);
    }

    @Test
    void listContacts_shouldReturnContacts() {
        // Arrange
        String authToken = "Bearer test-token";
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("contacts", new ArrayList<>());
        when(contactService.listContacts(anyString(), anyInt(), anyInt())).thenReturn(mockResponse);

        // Act
        ResponseEntity<Object> response = contactController.listContacts(authToken, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        verify(contactService, times(1)).listContacts(authToken, 0, 10);
    }

    @Test
    void getContact_shouldReturnContact() {
        // Arrange
        String authToken = "Bearer test-token";
        String contactId = "123";
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("id", contactId);
        when(contactService.getContact(anyString(), anyString())).thenReturn(mockResponse);

        // Act
        ResponseEntity<Object> response = contactController.getContact(authToken, contactId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals(contactId, ((Map<?, ?>) response.getBody()).get("id"));
        verify(contactService, times(1)).getContact(authToken, contactId);
    }

    @Test
    void createContact_shouldReturnCreatedContact() {
        // Arrange
        String authToken = "Bearer test-token";
        ContactDTO contact = new ContactDTO();
        contact.setEmail("test@example.com");
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("id", "123");
        when(contactService.createContact(anyString(), any(ContactDTO.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Object> response = contactController.createContact(authToken, contact);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals("123", ((Map<?, ?>) response.getBody()).get("id"));
        verify(contactService, times(1)).createContact(authToken, contact);
    }

    @Test
    void createContactsBatch_withValidRequest_shouldReturnCreatedContacts() {
        // Arrange
        String authToken = "Bearer test-token";
        BatchContactRequestDTO request = new BatchContactRequestDTO();
        List<ContactDTO> contacts = new ArrayList<>();
        ContactDTO contact = new ContactDTO();
        contact.setEmail("test@example.com");
        contacts.add(contact);
        request.setContacts(contacts);
        
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("results", new ArrayList<>());
        when(contactService.createContactsBatch(anyString(), any())).thenReturn(mockResponse);

        // Act
        ResponseEntity<Object> response = contactController.createContactsBatch(authToken, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        verify(contactService, times(1)).createContactsBatch(authToken, contacts);
    }

    @Test
    void createContactsBatch_withNullRequest_shouldReturnBadRequest() {
        // Arrange
        String authToken = "Bearer test-token";

        // Act
        ResponseEntity<Object> response = contactController.createContactsBatch(authToken, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals("invalid_request", ((Map<?, ?>) response.getBody()).get("error"));
        verify(contactService, never()).createContactsBatch(anyString(), any());
    }

    @Test
    void createContactsBatch_withEmptyContacts_shouldReturnBadRequest() {
        // Arrange
        String authToken = "Bearer test-token";
        BatchContactRequestDTO request = new BatchContactRequestDTO();
        request.setContacts(new ArrayList<>());

        // Act
        ResponseEntity<Object> response = contactController.createContactsBatch(authToken, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals("invalid_request", ((Map<?, ?>) response.getBody()).get("error"));
        verify(contactService, never()).createContactsBatch(anyString(), any());
    }

    @Test
    void createContactsBatch_withTooManyContacts_shouldReturnBadRequest() {
        // Arrange
        String authToken = "Bearer test-token";
        BatchContactRequestDTO request = new BatchContactRequestDTO();
        List<ContactDTO> contacts = new ArrayList<>();
        for (int i = 0; i < 101; i++) {
            ContactDTO contact = new ContactDTO();
            contact.setEmail("test" + i + "@example.com");
            contacts.add(contact);
        }
        request.setContacts(contacts);

        // Act
        ResponseEntity<Object> response = contactController.createContactsBatch(authToken, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals("invalid_request", ((Map<?, ?>) response.getBody()).get("error"));
        verify(contactService, never()).createContactsBatch(anyString(), any());
    }
} 