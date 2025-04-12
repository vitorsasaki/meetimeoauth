package com.example.meetimeoauth.dto;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BatchContactRequestDTOTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        BatchContactRequestDTO request = new BatchContactRequestDTO();
        List<ContactDTO> contacts = new ArrayList<>();
        ContactDTO contact1 = new ContactDTO();
        contact1.setEmail("test1@example.com");
        ContactDTO contact2 = new ContactDTO();
        contact2.setEmail("test2@example.com");
        contacts.add(contact1);
        contacts.add(contact2);

        // Act
        request.setContacts(contacts);

        // Assert
        assertNotNull(request.getContacts());
        assertEquals(2, request.getContacts().size());
        assertEquals("test1@example.com", request.getContacts().get(0).getEmail());
        assertEquals("test2@example.com", request.getContacts().get(1).getEmail());
    }

    @Test
    void testEmptyContacts() {
        // Arrange
        BatchContactRequestDTO request = new BatchContactRequestDTO();
        List<ContactDTO> emptyContacts = new ArrayList<>();

        // Act
        request.setContacts(emptyContacts);

        // Assert
        assertNotNull(request.getContacts());
        assertTrue(request.getContacts().isEmpty());
    }

    @Test
    void testNullContacts() {
        // Arrange
        BatchContactRequestDTO request = new BatchContactRequestDTO();

        // Act
        request.setContacts(null);

        // Assert
        assertNull(request.getContacts());
    }
} 