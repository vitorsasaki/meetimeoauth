package com.example.meetimeoauth.dto;

import java.util.List;

/**
 * DTO para requisição de criação de contatos em lote
 */
public class BatchContactRequestDTO {
    
    private List<ContactDTO> contacts;
    
    public BatchContactRequestDTO() {
    }
    
    public List<ContactDTO> getContacts() {
        return contacts;
    }
    
    public void setContacts(List<ContactDTO> contacts) {
        this.contacts = contacts;
    }
} 