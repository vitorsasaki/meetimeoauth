package com.example.meetimeoauth.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.meetimeoauth.dto.ContactDTO;
import com.example.meetimeoauth.exception.ApiException;
import com.example.meetimeoauth.exception.RateLimitException;
import com.example.meetimeoauth.service.ContactService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

    // Constantes para rate limit
    private static final long DEFAULT_RETRY_AFTER = 10000; // 10 segundos
    private static final int MAX_RETRIES = 3;

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
    
    @Override
    public Object createContactsBatch(String authToken, List<ContactDTO> contacts) {
        try {
            HttpHeaders headers = createHeaders(authToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Construir o corpo da requisição em lote
            ObjectNode requestBody = objectMapper.createObjectNode();
            ArrayNode inputs = objectMapper.createArrayNode();
            
            // Criar estrutura para cada contato da lista
            for (ContactDTO contact : contacts) {
                ObjectNode contactNode = objectMapper.createObjectNode();
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
                
                contactNode.set("properties", properties);
                inputs.add(contactNode);
            }
            
            requestBody.set("inputs", inputs);
            
            String url = baseUrl + "/crm/v3/objects/contacts/batch/create";
            
            // Implementar retry com backoff para rate limit
            return executeWithRetry(url, HttpMethod.POST, headers, requestBody);
            
        } catch (RateLimitException e) {
            logger.error("Rate limit atingido após múltiplas tentativas: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "rate_limit_exceeded");
            errorResponse.put("message", "Limite de taxa excedido. Tente novamente mais tarde.");
            errorResponse.put("retryAfter", e.getRetryAfter());
            return errorResponse;
        } catch (Exception e) {
            logger.error("Erro ao criar contatos em lote: {}", e.getMessage());
            throw new ApiException("Erro ao criar contatos em lote: " + e.getMessage(), e);
        }
    }
    
    /**
     * Executa a requisição HTTP com suporte a retentativas em caso de rate limit
     */
    private JsonNode executeWithRetry(String url, HttpMethod method, HttpHeaders headers, Object body) 
            throws RateLimitException {
        
        int retryCount = 0;
        long retryAfter = DEFAULT_RETRY_AFTER;
        
        while (retryCount < MAX_RETRIES) {
            try {
                ResponseEntity<JsonNode> response = restTemplate.exchange(
                        url, 
                        method, 
                        new HttpEntity<>(body, headers), 
                        JsonNode.class);
                
                // Se a requisição for bem-sucedida, retornar o resultado
                return response.getBody();
                
            } catch (HttpClientErrorException e) {
                // Se for erro 429 Too Many Requests (rate limit), tentar novamente
                if (e.getStatusCode().value() == 429) {
                    retryCount++;
                    
                    // Obter o tempo de espera do cabeçalho Retry-After, se disponível
                    List<String> retryAfterHeaders = e.getResponseHeaders() != null ? 
                            e.getResponseHeaders().get("Retry-After") : null;
                    
                    if (retryAfterHeaders != null && !retryAfterHeaders.isEmpty()) {
                        try {
                            retryAfter = Long.parseLong(retryAfterHeaders.get(0)) * 1000; // converter para milissegundos
                        } catch (NumberFormatException ex) {
                            logger.warn("Não foi possível converter o cabeçalho Retry-After: {}", retryAfterHeaders.get(0));
                        }
                    }
                    
                    if (retryCount < MAX_RETRIES) {
                        logger.warn("Rate limit atingido. Tentando novamente em {} ms (tentativa {}/{})", 
                                retryAfter, retryCount, MAX_RETRIES);
                        
                        try {
                            Thread.sleep(retryAfter);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new ApiException("Operação interrompida durante pausa para retry", ie);
                        }
                    } else {
                        throw new RateLimitException("Tentativas de retry esgotadas devido a rate limit", retryAfter);
                    }
                } else {
                    // Se for outro tipo de erro, propagar a exceção
                    logger.error("Erro HTTP durante a requisição: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
                    throw new ApiException("Erro HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
                }
            } catch (HttpServerErrorException e) {
                // Se for erro de servidor (5xx), tentar novamente com backoff
                retryCount++;
                
                if (retryCount < MAX_RETRIES) {
                    long backoff = retryAfter * retryCount; // Aumentar o tempo de espera a cada tentativa
                    
                    logger.warn("Erro do servidor {}. Tentando novamente em {} ms (tentativa {}/{})", 
                            e.getStatusCode(), backoff, retryCount, MAX_RETRIES);
                    
                    try {
                        Thread.sleep(backoff);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new ApiException("Operação interrompida durante pausa para retry", ie);
                    }
                } else {
                    throw new ApiException("Erro do servidor após múltiplas tentativas: " + e.getResponseBodyAsString(), e);
                }
            }
        }
        
        // Este ponto só deveria ser alcançado se houver uma falha no fluxo de retentativas
        throw new ApiException("Erro desconhecido ao executar requisição com retry");
    }
    
    /**
     * Cria os cabeçalhos HTTP para requisições à API do HubSpot
     * 
     * @param authToken Token de autorização
     * @return HttpHeaders configurados com o token de autorização
     */
    private HttpHeaders createHeaders(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        
        // Verifica se o token já começa com "Bearer" ou outro prefixo
        if (authToken != null && !authToken.trim().isEmpty()) {
            // Se o token não começar com "Bearer", adiciona o prefixo
            if (!authToken.startsWith("Bearer ")) {
                authToken = "Bearer " + authToken;
            }
            logger.debug("Configurando cabeçalho Authorization: {}", authToken.substring(0, 15) + "...");
            headers.set("Authorization", authToken);
        } else {
            logger.warn("Token de autorização vazio ou nulo");
        }
        
        return headers;
    }
} 