# MeeTimeOAuth - Integração com HubSpot

Este projeto é uma API REST desenvolvida em Java com Spring Boot para integração com o HubSpot, implementando o fluxo OAuth 2.0 para autenticação e gerenciamento de contatos.

## Tecnologias e Dependências

### Versões
- Java 17
- Spring Boot 3.2.x
- Maven 3.8.x

### Principais Dependências
- **Spring Boot Starter Web**: Framework web e REST
- **Spring Boot Starter Security**: Segurança e autenticação
- **Spring Boot Starter Validation**: Validação de dados
- **Spring Boot Starter Test**: Testes unitários e de integração
- **Spring Boot Starter Actuator**: Monitoramento e métricas
- **Spring Boot DevTools**: Ferramentas de desenvolvimento
- **Lombok**: Redução de código boilerplate
- **Jackson**: Serialização/deserialização JSON
- **JUnit 5**: Framework de testes
- **Mockito**: Framework de mock para testes
- **Spring Boot Starter Data JPA**: Persistência de dados (se necessário)
- **Spring Boot Starter Cache**: Cache de dados (se necessário)

## Requisitos

- Java 17 ou superior
- Maven 3.8.x ou superior
- Uma conta de desenvolvedor no HubSpot com uma aplicação configurada
- IDE de sua preferência (recomendado: IntelliJ IDEA ou Eclipse)

## Configuração do Ambiente

1. Clone o repositório:
```bash
git clone https://github.com/vitorsasaki/meetimeoauth.git
cd meetimeoauth
```

2. Configure as credenciais do HubSpot no arquivo `application.properties`:
```properties
spring.application.name=meetimeoauth
server.port=8080

# HubSpot OAuth Configuration
hubspot.client.id=seu-client-id
hubspot.client.secret=seu-cliente-secret
hubspot.app-id=seu-app-id
hubspot.auth.url=https://app.hubspot.com/oauth/authorize
hubspot.token.url=https://api.hubapi.com/oauth/v1/token
hubspot.redirect.url=http://localhost:8080/api/auth/callback
hubspot.scopes=crm.objects.contacts.read crm.objects.contacts.write crm.objects.custom.read crm.objects.deals.read

# Webhook Configuration
webhook.verification.token=seu-webhook-token


# Logging
logging.level.com.example.meetimeoauth=DEBUG
logging.level.org.springframework.web=INFO
```

3. Configure a aplicação no HubSpot:
   - Acesse o [HubSpot Developer Portal](https://developers.hubspot.com/)
   - Crie uma nova aplicação ou selecione uma existente
   - Configure a URL de redirecionamento: `http://localhost:8080/api/auth/callback`
   - Copie as credenciais (Client ID, Client Secret e App ID) para o arquivo `application.properties`

## Executando a Aplicação

### Desenvolvimento

1. Usando Maven:
```bash
mvn spring-boot:run
```

2. Usando IDE:
   - Importe o projeto como projeto Maven
   - Execute a classe `MeetimeoauthApplication`

### Produção

1. Construa o projeto:
```bash
mvn clean package
```

2. Execute o JAR ou um run na aplicação se estiver usando uma IDE para testar:
```bash
java -jar target/meetimeoauth-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em: http://localhost:8080

## Endpoints da API

### Autenticação OAuth


- **Iniciar fluxo OAuth**: `GET /api/auth/authorize`
    - Redireciona para a página de autorização do HubSpot
    - Exemplo: `http://localhost:8080/api/auth/authorize`
    - A Resposta inclui:
        - A URL de autorização completa
        - Uma mensagem explicativa sobre como usar a URL
    - O cliente da API (frontend ou outra aplicação) pode agora decidir como usar essa URL, por exemplo:
        - Exibindo-a para o usuário
        - Redirecionando o usuário automaticamente
        - Abrindo-a em uma nova janela ou iframe
    - Para testar o endpoint, você pode acessar http://localhost:8080/api/auth/authorize no navegador ou usar uma ferramenta como Postman, e receberá uma resposta JSON com a URL de autorização gerada, em vez de ser redirecionado diretamente.
    - Não esquecer de configurar os escopos no arquivo aplication.properties ele tem que ser igual ao que foi configurado no HubSpot
    - A resposta será semelhante a:

``` 
  {"authorizationUrl": "https://app.hubspot.com/oauth/authorize?client_id=seu-client-id&redirect_uri=http://localhost:8080/api/auth/callback&scope=contacts%20oauth&response_type=code",
  "message": "URL de autorização gerada com sucesso. Redirecione o usuário para esta URL para iniciar o fluxo OAuth."}
```
      

- **Callback OAuth**: `GET /api/auth/callback?code={code}`
  - Recebe o código de autorização e troca por um token de acesso
  - Exemplo: `http://localhost:8080/api/auth/callback?code=seu-codigo`

### Gerenciamento de Contatos

Para todos os endpoints abaixo, é necessário incluir o token de acesso no cabeçalho da requisição:
```
Authorization: Bearer seu-token-de-acesso
```
- **Testar com o postman**
  - passe a url 
  - Na aba Headers crie 2 keys 
    - a primeira key do tipo Authorization e o value vai ser Bearer seu-toke-acesso
    - 2 key do tipo Content-Type passando como value application/json


- **Listar contatos**: `GET /api/contacts?offset={offset}&limit={limit}`
  - Retorna uma lista paginada de contatos
  - Parâmetros opcionais:
    - `offset`: índice para paginação (padrão: 0)
    - `limit`: quantidade de registros por página (padrão: 10)
  - Exemplo: `http://localhost:8080/api/contacts?offset=0&limit=10`

- **Obter contato por ID**: `GET /api/contacts/{contactId}`
  - Retorna os detalhes de um contato específico
  - Exemplo: `http://localhost:8080/api/contacts/123`


- **Criar contatos**: `POST /api/contacts/batch`
  - Cria 1 contatos ou multiplos contatos
  - Implementa tratamento de rate limit
  - Não estou validandos dados são obrigatorios e regras (tipo mesmo email) la do HubSpot
  - Corpo da requisição:
    ```json
    {
      "contacts": [
        {
          "email": "contato1@email.com",
          "firstName": "Nome1",
          "lastName": "Sobrenome1"
        },
        {
          "email": "contato2@email.com",
          "firstName": "Nome2",
          "lastName": "Sobrenome2"
        }
      ]
    }
    ```
## WEBHOOK    
- **Configurar a api para recebimentos de Webhook para Criação de Contatos**:

    - Acesse o seu aplicativo no HubSpot(eu criei uma conta de teste desenvolvero e criei uma api privada)
    - Na api -> configurações -> Integrações -> Aplicativos privados
    - Dentro da api na aba Webhooks tem que inserir a url de destino.
- Para testar localmente eu usei o ngrok , baixe o aplicativo e execute o comando `ngrok http 8080`
```
  cd C:\Users\SeuUsuario\Downloads
  .\ngrok.exe http 8080
```
- Pegue a url gerada(ex: https://abc123.ngrok.io)
- Atualize a URL de destino no webhook do HubSpot com https://abc123.ngrok.io/api/webhook/contact-creation
- Na api local abra o application.properties e configure o webhook.verification.token=seu-token-aqui que é encontrado na aba Autenticação Segredo do cliente
- E por ultimo no webhooks vá em editar aplicativo -> aba Webhooks -> Criar assinatura com scopo contact.creation
- Para testar só criar um contato , pode ser pela api local ou no HubSpot , vai aparecer uma mensagem no log se assinatura for valida

## Segurança

A aplicação implementa as seguintes medidas de segurança:

- CORS configurado para permitir requisições de qualquer origem
- CSRF desabilitado para APIs RESTful
- Autenticação por token com sessão stateless
- Endpoints públicos limitados ao fluxo de autenticação
- Tratamento de rate limit com retry e backoff
- Validação de assinatura de webhooks

## Boas Práticas Implementadas

- Separação de responsabilidades (padrão MVC)
- Tratamento adequado de exceções com handlers globais
- Uso de DTOs para transferência de dados
- Logging estruturado e configurável
- Testes unitários e de integração
- Documentação de código com JavaDoc
- Configuração externa via properties
- Tratamento de erros HTTP com respostas padronizadas

## Monitoramento

A aplicação expõe endpoints do Actuator para monitoramento:

- **Health Check**: `GET /actuator/health`
- **Info**: `GET /actuator/info`
- **Metrics**: `GET /actuator/metrics`

## Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes. 