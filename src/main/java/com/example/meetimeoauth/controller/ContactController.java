package com.example.meetimeoauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.meetimeoauth.dto.ContactDTO;
import com.example.meetimeoauth.service.ContactService;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;
    
    @GetMapping
    public ResponseEntity<Object> listContacts(
            @RequestHeader("Authorization") String authToken,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(contactService.listContacts(authToken, offset, limit));
    }
    
    @GetMapping("/{contactId}")
    public ResponseEntity<Object> getContact(
            @RequestHeader("Authorization") String authToken, 
            @PathVariable String contactId) {
        return ResponseEntity.ok(contactService.getContact(authToken, contactId));
    }
    
    @PostMapping
    public ResponseEntity<Object> createContact(
            @RequestHeader("Authorization") String authToken,
            @RequestBody ContactDTO contact) {
        return ResponseEntity.ok(contactService.createContact(authToken, contact));
    }
} 