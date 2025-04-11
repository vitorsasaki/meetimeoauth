package com.example.meetimeoauth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.meetimeoauth.service.WebhookValidationService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @Autowired
    private WebhookValidationService validationService;

    @PostMapping("/contact-creation")
    public ResponseEntity<String> handleContactCreation(
            @RequestHeader(value = "X-HubSpot-Signature", required = false) String signature,
            HttpServletRequest request,
            @RequestBody String requestBody) {

        logger.info("Webhook recebido: {}", request.getRequestURI());
        
        // Validar a assinatura
        if (signature == null || signature.isEmpty()) {
            logger.error("Assinatura do webhook ausente");
            return ResponseEntity.badRequest().body("Assinatura ausente");
        }

        // Validar a assinatura
        boolean isValid = validationService.isValidSignatureV1(requestBody, signature);

        if (!isValid) {
            logger.error("Assinatura do webhook inválida");
            return ResponseEntity.badRequest().body("Assinatura inválida");
        }

        // Processar o webhook
        try {
            logger.info("Webhook processado com sucesso");
            return ResponseEntity.ok("Webhook recebido e validado com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao processar webhook", e);
            return ResponseEntity.internalServerError().body("Erro ao processar webhook");
        }
    }

} 