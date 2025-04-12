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
# Configurações do HubSpot
hubspot.client.id=seu-client-id
hubspot.client.secret=seu-client-secret
hubspot.app-id=seu-app-id
hubspot.redirect.url=http://localhost:8080/api/auth/callback
hubspot.api.base-url=https://api.hubapi.com

# Configurações da aplicação
server.port=8080
spring.application.name=meetimeoauth

# Configurações de segurança
spring.security.cors.allowed-origins=*
spring.security.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.security.cors.allowed-headers=*

# Configurações de logging
logging.level.root=INFO
logging.level.com.example.meetimeoauth=DEBUG
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

2. Execute o JAR:
```bash
java -jar target/meetimeoauth-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em: http://localhost:8080

## Endpoints da API

### Autenticação OAuth

- **Iniciar fluxo OAuth**: `GET /api/auth/authorize`
  - Redireciona para a página de autorização do HubSpot
  - Exemplo: `http://localhost:8080/api/auth/authorize`

- **Callback OAuth**: `GET /api/auth/callback?code={code}`
  - Recebe o código de autorização e troca por um token de acesso
  - Exemplo: `http://localhost:8080/api/auth/callback?code=seu-codigo`

### Gerenciamento de Contatos

Para todos os endpoints abaixo, é necessário incluir o token de acesso no cabeçalho da requisição:
```
Authorization: Bearer seu-token-de-acesso
```

- **Listar contatos**: `GET /api/contacts?offset={offset}&limit={limit}`
  - Retorna uma lista paginada de contatos
  - Parâmetros opcionais:
    - `offset`: índice para paginação (padrão: 0)
    - `limit`: quantidade de registros por página (padrão: 10)
  - Exemplo: `http://localhost:8080/api/contacts?offset=0&limit=10`

- **Obter contato por ID**: `GET /api/contacts/{contactId}`
  - Retorna os detalhes de um contato específico
  - Exemplo: `http://localhost:8080/api/contacts/123`

- **Criar contato**: `POST /api/contacts`
  - Cria um novo contato no HubSpot
  - Corpo da requisição:
    ```json
    {
      "email": "exemplo@email.com",
      "firstName": "Nome",
      "lastName": "Sobrenome",
      "phone": "11999999999",
      "properties": {
        "propriedade_personalizada": "valor"
      }
    }
    ```

- **Criar contatos em lote**: `POST /api/contacts/batch`
  - Cria múltiplos contatos em uma única operação
  - Implementa tratamento de rate limit
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