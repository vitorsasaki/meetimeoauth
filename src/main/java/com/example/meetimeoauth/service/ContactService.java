package com.example.meetimeoauth.service;

import com.example.meetimeoauth.dto.ContactDTO;

public interface ContactService {

    /**
     * Lista os contatos do HubSpot
     * 
     * @param authToken token de autenticação
     * @param offset    índice para paginação
     * @param limit     quantidade de registros por página
     * @return lista de contatos
     */
    Object listContacts(String authToken, Integer offset, Integer limit);
    
    /**
     * Obtém um contato do HubSpot pelo ID
     * 
     * @param authToken token de autenticação
     * @param contactId ID do contato
     * @return detalhes do contato
     */
    Object getContact(String authToken, String contactId);
    
    /**
     * Cria um novo contato no HubSpot
     * 
     * @param authToken token de autenticação
     * @param contact   dados do contato
     * @return detalhes do contato criado
     */
    Object createContact(String authToken, ContactDTO contact);
} 