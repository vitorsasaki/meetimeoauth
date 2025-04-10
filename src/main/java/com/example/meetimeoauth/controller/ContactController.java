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
import com.example.meetimeoauth.dto.BatchContactRequestDTO;
import com.example.meetimeoauth.service.ContactService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;
    
    @GetMapping
    public ResponseEntity<Object> listContacts(
            @RequestHeader("Authorization") String authToken,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        logger.info("Listando contatos - offset: {}, limit: {}", offset, limit);
        return ResponseEntity.ok(contactService.listContacts(authToken, offset, limit));
    }
    
    @GetMapping("/{contactId}")
    public ResponseEntity<Object> getContact(
            @RequestHeader("Authorization") String authToken, 
            @PathVariable String contactId) {
        logger.info("Buscando contato com ID: {}", contactId);
        return ResponseEntity.ok(contactService.getContact(authToken, contactId));
    }
    
    /**
     * Cria um novo contato no HubSpot CRM
     * 
     * @param authToken Token de autorização
     * @param contact Dados do contato a ser criado
     * @return Resposta da API com os dados do contato criado
     */
    @PostMapping
    public ResponseEntity<Object> createContact(
            @RequestHeader("Authorization") String authToken,
            @RequestBody ContactDTO contact) {
        logger.info("Criando contato com email: {}", contact.getEmail());
        Object response = contactService.createContact(authToken, contact);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Cria múltiplos contatos em lote no HubSpot CRM
     * Respeita as políticas de rate limit da API do HubSpot
     * 
     * @param authToken Token de autorização
     * @param request Request contendo a lista de contatos a serem criados
     * @return Resposta da API com os dados dos contatos criados
     */
    @PostMapping("/batch")
    public ResponseEntity<Object> createContactsBatch(
            @RequestHeader("Authorization") String authToken,
            @RequestBody BatchContactRequestDTO request) {
        
        logger.info("Criando lote de {} contatos", request.getContacts().size());
        
        if (request.getContacts() == null || request.getContacts().isEmpty()) {
            logger.warn("Tentativa de criar lote vazio de contatos");
            return ResponseEntity.badRequest().body("A lista de contatos não pode estar vazia");
        }
        
        // Limitar o número de contatos por requisição
        if (request.getContacts().size() > 100) {
            logger.warn("Tentativa de criar lote com mais de 100 contatos ({})", request.getContacts().size());
            return ResponseEntity.badRequest()
                    .body("Número máximo de contatos por lote excedido. Máximo permitido: 100");
        }
        
        Object response = contactService.createContactsBatch(authToken, request.getContacts());
        logger.info("Lote de contatos criado com sucesso");
        
        return ResponseEntity.ok(response);
    }
} 