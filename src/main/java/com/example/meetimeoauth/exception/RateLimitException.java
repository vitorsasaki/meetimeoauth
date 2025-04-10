package com.example.meetimeoauth.exception;

/**
 * Exceção lançada quando o rate limit da API é excedido
 */
public class RateLimitException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    private final long retryAfter;
    
    /**
     * Cria uma nova exceção de Rate Limit
     * 
     * @param message Mensagem de erro
     * @param retryAfter Tempo em milissegundos a aguardar antes de tentar novamente
     */
    public RateLimitException(String message, long retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }
    
    /**
     * @return Tempo em milissegundos a aguardar antes de tentar novamente
     */
    public long getRetryAfter() {
        return retryAfter;
    }
} 