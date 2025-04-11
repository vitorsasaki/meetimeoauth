package com.example.meetimeoauth.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.meetimeoauth.dto.ContactDTO;

/**
 * Classe utilitária para métodos de logging comuns usados em toda a aplicação
 */
public class TokenConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(TokenConfig.class);
    
    private TokenConfig() {
        // Construtor privado para impedir instanciação
    }
    
    /**
     * Loga apenas o prefixo do token de autenticação para fins de depuração,
     * ocultando a maior parte do token por questões de segurança.
     * 
     * @param authToken Token de autorização a ser logado
     * @param callerClass Classe que está chamando este método (para logging contextual)
     */
    public static void logAuthTokenPrefix(String authToken, Class<?> callerClass) {
        Logger contextLogger = LoggerFactory.getLogger(callerClass);
        
        if (authToken != null && authToken.length() > 15) {
            String tokenPrefix = authToken.substring(0, Math.min(15, authToken.length()));
            contextLogger.debug("Token de autorização fornecido (prefixo): {}...", tokenPrefix);
        } else {
            contextLogger.warn("Token de autorização inválido ou muito curto");
        }
    }
    
    /**
     * Loga uma amostra de emails da lista de contatos para depuração
     * 
     * @param contacts Lista de contatos
     * @param callerClass Classe que está chamando este método (para logging contextual)
     */
    public static void logSampleEmails(List<ContactDTO> contacts, Class<?> callerClass) {
        Logger contextLogger = LoggerFactory.getLogger(callerClass);
        
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
                contextLogger.debug("Amostra de emails: {}", sampleEmails.toString());
            }
        }
    }
    
    /**
     * Loga o tamanho de uma lista e outros detalhes para depuração
     * 
     * @param list Lista a ser analisada
     * @param listName Nome da lista para referência no log
     * @param callerClass Classe que está chamando este método (para logging contextual)
     */
    public static void logListDetails(List<?> list, String listName, Class<?> callerClass) {
        Logger contextLogger = LoggerFactory.getLogger(callerClass);
        
        if (list == null) {
            contextLogger.debug("Lista '{}' é nula", listName);
        } else if (list.isEmpty()) {
            contextLogger.debug("Lista '{}' está vazia", listName);
        } else {
            contextLogger.debug("Lista '{}' contém {} item(s)", listName, list.size());
        }
    }
    
    /**
     * Loga detalhes da requisição HTTP para depuração
     * 
     * @param endpoint Endpoint sendo acessado
     * @param method Método HTTP (GET, POST, etc)
     * @param callerClass Classe que está chamando este método (para logging contextual)
     */
    public static void logRequestDetails(String endpoint, String method, Class<?> callerClass) {
        Logger contextLogger = LoggerFactory.getLogger(callerClass);
        contextLogger.debug("Requisição {} para endpoint: {}", method, endpoint);
    }
} 