package com.example.meetimeoauth.dto;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ContactDTOTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        ContactDTO contact = new ContactDTO();
        String id = "123";
        String email = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";
        String phone = "1234567890";
        Map<String, Object> properties = new HashMap<>();
        properties.put("company", "Test Company");

        // Act
        contact.setId(id);
        contact.setEmail(email);
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setPhone(phone);
        contact.setProperties(properties);

        // Assert
        assertEquals(id, contact.getId());
        assertEquals(email, contact.getEmail());
        assertEquals(firstName, contact.getFirstName());
        assertEquals(lastName, contact.getLastName());
        assertEquals(phone, contact.getPhone());
        assertEquals(properties, contact.getProperties());
    }

    @Test
    void testAddProperty() {
        // Arrange
        ContactDTO contact = new ContactDTO();
        String key = "testKey";
        String value = "testValue";

        // Act
        contact.addProperty(key, value);

        // Assert
        assertTrue(contact.getProperties().containsKey(key));
        assertEquals(value, contact.getProperties().get(key));
    }

    @Test
    void testDefaultProperties() {
        // Arrange
        ContactDTO contact = new ContactDTO();

        // Assert
        assertNotNull(contact.getProperties());
        assertTrue(contact.getProperties().isEmpty());
    }

    @Test
    void testNullValues() {
        // Arrange
        ContactDTO contact = new ContactDTO();

        // Act
        contact.setId(null);
        contact.setEmail(null);
        contact.setFirstName(null);
        contact.setLastName(null);
        contact.setPhone(null);
        contact.setProperties(null);

        // Assert
        assertNull(contact.getId());
        assertNull(contact.getEmail());
        assertNull(contact.getFirstName());
        assertNull(contact.getLastName());
        assertNull(contact.getPhone());
        assertNull(contact.getProperties());
    }
} 