package com.example.meetimeoauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import com.example.meetimeoauth.service.OAuthService;
import com.example.meetimeoauth.dto.TokenResponseDTO;
import com.example.meetimeoauth.dto.AuthUrlResponseDTO;
import com.example.meetimeoauth.exception.OAuthException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private OAuthService oauthService;
    
    @Value("${hubspot.client.id}")
    private String clientId;
    
    @Value("${hubspot.auth.url}")
    private String authUrl;
    
    @Value("${hubspot.redirect.url}")
    private String redirectUrl;
    
    @Value("${hubspot.scopes}")
    private String scopes;

    /**
     * Endpoint responsável por gerar e retornar a URL de autorização
     * para iniciar o fluxo OAuth com o HubSpot.
     * 
     * @return ResponseEntity contendo a URL de autorização
     */
    @GetMapping("/authorize")
    public ResponseEntity<AuthUrlResponseDTO> initiateOAuth() {
        // Codificar os escopos para URL
        String encodedScopes = UriUtils.encode(scopes, StandardCharsets.UTF_8);
        
        String authorizationUrl = authUrl + 
                "?client_id=" + clientId + 
                "&redirect_uri=" + UriUtils.encode(redirectUrl, StandardCharsets.UTF_8) + 
                "&scope=" + encodedScopes +
                "&response_type=code";
        
        logger.info("URL de autorização gerada: {}", authorizationUrl);
        
        AuthUrlResponseDTO response = new AuthUrlResponseDTO();
        response.setAuthorizationUrl(authorizationUrl);
        response.setMessage("URL de autorização gerada com sucesso. Redirecione o usuário para esta URL para iniciar o fluxo OAuth.");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint de callback que recebe o código de autorização do HubSpot
     * após o usuário conceder permissão. O código é trocado por um token de acesso.
     * 
     * @param code Código de autorização fornecido pelo HubSpot
     * @param error Mensagem de erro (opcional, se houver falha na autorização)
     * @return Objeto contendo as informações do token obtido
     */
    @GetMapping("/callback")
    public ResponseEntity<?> handleCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error) {
        
        // Verificar se houve erro na autorização
        if (error != null) {
            logger.error("Erro retornado pelo HubSpot durante a autorização: {}", error);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", error);
            errorResponse.put("message", "Falha na autorização com o HubSpot");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        
        // Verificar se o código foi fornecido
        if (code == null || code.trim().isEmpty()) {
            logger.error("Código de autorização não fornecido");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "invalid_request");
            errorResponse.put("message", "Código de autorização não fornecido");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        
        try {
            logger.info("Recebido código de autorização. Trocando por token de acesso...");
            // Trocar o código de autorização por token
            TokenResponseDTO tokenResponse = oauthService.exchangeCodeForToken(code);
            
            // Adicionar informações adicionais úteis na resposta
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Autenticação realizada com sucesso");
            response.put("token", tokenResponse);
            response.put("authorization", tokenResponse.getTokenType() + " " + tokenResponse.getAccessToken());
            
            logger.info("Token de acesso obtido com sucesso");
            return ResponseEntity.ok(response);
        } catch (OAuthException e) {
            logger.error("Erro ao trocar código por token: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "token_exchange_error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logger.error("Erro inesperado ao processar callback: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "server_error");
            errorResponse.put("message", "Erro interno ao processar a requisição");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
} 