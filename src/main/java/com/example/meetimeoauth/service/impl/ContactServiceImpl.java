package com.example.meetimeoauth.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.meetimeoauth.dto.ContactDTO;
import com.example.meetimeoauth.exception.ApiException;
import com.example.meetimeoauth.service.ContactService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

@Service
public class ContactServiceImpl implements ContactService {

    @Value("${hubspot.api.base-url:https://api.hubapi.com}")
    private String baseUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public ContactServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Object listContacts(String authToken, Integer offset, Integer limit) {
        try {
            HttpHeaders headers = createHeaders(authToken);
            
            String url = UriComponentsBuilder
                    .fromHttpUrl(baseUrl + "/crm/v3/objects/contacts")
                    .queryParam("limit", limit)
                    .queryParam("after", offset)
                    .build()
                    .toUriString();
            
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    new HttpEntity<>(headers), 
                    JsonNode.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new ApiException("Falha ao obter contatos: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new ApiException("Erro ao obter contatos: " + e.getMessage(), e);
        }
    }

    @Override
    public Object getContact(String authToken, String contactId) {
        try {
            HttpHeaders headers = createHeaders(authToken);
            
            String url = baseUrl + "/crm/v3/objects/contacts/" + contactId;
            
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    new HttpEntity<>(headers), 
                    JsonNode.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new ApiException("Falha ao obter contato: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new ApiException("Erro ao obter contato: " + e.getMessage(), e);
        }
    }

    @Override
    public Object createContact(String authToken, ContactDTO contact) {
        try {
            HttpHeaders headers = createHeaders(authToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            ObjectNode requestBody = objectMapper.createObjectNode();
            ObjectNode properties = objectMapper.createObjectNode();
            
            if (contact.getEmail() != null) {
                properties.put("email", contact.getEmail());
            }
            
            if (contact.getFirstName() != null) {
                properties.put("firstname", contact.getFirstName());
            }
            
            if (contact.getLastName() != null) {
                properties.put("lastname", contact.getLastName());
            }
            
            if (contact.getPhone() != null) {
                properties.put("phone", contact.getPhone());
            }
            
            // Adicionar propriedades customizadas
            if (contact.getProperties() != null && !contact.getProperties().isEmpty()) {
                contact.getProperties().forEach((key, value) -> properties.put(key, value.toString()));
            }
            
            requestBody.set("properties", properties);
            
            String url = baseUrl + "/crm/v3/objects/contacts";
            
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url, 
                    HttpMethod.POST, 
                    new HttpEntity<>(requestBody, headers), 
                    JsonNode.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new ApiException("Falha ao criar contato: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new ApiException("Erro ao criar contato: " + e.getMessage(), e);
        }
    }
    
    private HttpHeaders createHeaders(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);
        return headers;
    }
} 