package com.example.meetimeoauth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class WebhookValidationService {
    private static final Logger logger = LoggerFactory.getLogger(WebhookValidationService.class);

    @Value("${webhook.verification.token}")
    private String clientSecret;

    /**
     * Valida a assinatura v1 de um webhook do HubSpot
     */
    public boolean isValidSignatureV1(String requestBody, String signature) {
        try {
            // Verificar se o client secret está configurado
            if (clientSecret == null || clientSecret.isEmpty()) {
                logger.error("Client Secret não configurado");
                return false;
            }

            // Verificar parâmetros
            if (requestBody == null || signature == null) {
                logger.error("Parâmetros inválidos");
                return false;
            }

            // Criar a string fonte e calcular hash
            String sourceString = clientSecret + requestBody;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sourceString.getBytes(StandardCharsets.UTF_8));

            // Converter para hexadecimal
            StringBuilder calculatedSignature = new StringBuilder();
            for (byte b : hash) {
                calculatedSignature.append(String.format("%02x", b));
            }

            // Comparar as assinaturas (case insensitive)
            boolean isValid = calculatedSignature.toString().equalsIgnoreCase(signature);
            
            if (!isValid) {
                logger.warn("Assinatura inválida recebida");
            }

            return isValid;

        } catch (Exception e) {
            logger.error("Erro ao validar assinatura", e);
            return false;
        }
    }
} 