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
import java.util.HashMap;
import java.util.Map;

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
        
        // Log do token para depuração (ocultando partes do token)
        logAuthTokenPrefix(authToken);
        
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
        
        try {
            logger.info("Recebida requisição para criar lote de contatos");
            
            // Log do token para depuração (ocultando partes do token)
            logAuthTokenPrefix(authToken);
            
            // Validar a estrutura da requisição
            if (request == null) {
                logger.warn("Requisição inválida: objeto request é nulo");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "invalid_request");
                errorResponse.put("message", "Requisição inválida: corpo da requisição não pode ser nulo");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Validar a lista de contatos
            if (request.getContacts() == null) {
                logger.warn("Requisição inválida: lista de contatos é nula");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "invalid_request");
                errorResponse.put("message", "Requisição inválida: lista de contatos não pode ser nula");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            logger.info("Criando lote de {} contatos", request.getContacts().size());
            
            if (request.getContacts().isEmpty()) {
                logger.warn("Tentativa de criar lote vazio de contatos");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "invalid_request");
                errorResponse.put("message", "A lista de contatos não pode estar vazia");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Limitar o número de contatos por requisição
            if (request.getContacts().size() > 100) {
                logger.warn("Tentativa de criar lote com mais de 100 contatos ({})", request.getContacts().size());
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "invalid_request");
                errorResponse.put("message", "Número máximo de contatos por lote excedido. Máximo permitido: 100");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Log de alguns emails para depuração
            logSampleEmails(request.getContacts());
            
            Object response = contactService.createContactsBatch(authToken, request.getContacts());
            logger.info("Lote de contatos criado com sucesso");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao processar requisição de criação em lote: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "internal_error");
            errorResponse.put("message", "Erro interno ao processar a requisição: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Loga apenas o prefixo do token de autenticação para fins de depuração
     */
    private void logAuthTokenPrefix(String authToken) {
        if (authToken != null && authToken.length() > 15) {
            String tokenPrefix = authToken.substring(0, Math.min(15, authToken.length()));
            logger.debug("Token de autorização fornecido (prefixo): {}...", tokenPrefix);
        } else {
            logger.warn("Token de autorização inválido ou muito curto");
        }
    }
    
    /**
     * Loga uma amostra de emails da lista de contatos
     */
    private void logSampleEmails(List<ContactDTO> contacts) {
        if (contacts != null && !contacts.isEmpty()) {
            int sampleSize = Math.min(3, contacts.size());
            StringBuilder sampleEmails = new StringBuilder();
            
            for (int i = 0; i < sampleSize; i++) {
                ContactDTO contact = contacts.get(i);
                if (contact != null && contact.getEmail() != null) {
                    if (sampleEmails.length() > 0) {
                        sampleEmails.append(", ");
                    }
                    sampleEmails.append(contact.getEmail());
                }
            }
            
            if (sampleEmails.length() > 0) {
                logger.debug("Amostra de emails a serem criados: {}", sampleEmails.toString());
            }
        }
    }
} 