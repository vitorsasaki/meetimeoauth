# MeeTimeOAuth - Integração com HubSpot

Este projeto é uma API REST desenvolvida em Java com Spring Boot para integração com o HubSpot, implementando o fluxo OAuth 2.0 para autenticação e gerenciamento de contatos.

## Requisitos

- Java 17 ou superior
- Maven 3.8.x ou superior
- Uma conta de desenvolvedor no HubSpot com uma aplicação configurada

## Configuração

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/meetimeoauth.git
cd meetimeoauth
```

2. Configure as credenciais do HubSpot no arquivo `application.properties`:
```properties
hubspot.client.id=seu-client-id
hubspot.client.secret=seu-client-secret
hubspot.redirect.url=http://localhost:8080/api/auth/callback
```

3. Certifique-se de configurar corretamente a URL de redirecionamento no painel de desenvolvedor do HubSpot.

## Executando a aplicação

### Usando Maven

```bash
mvn spring-boot:run
```

### Usando Java

```bash
mvn clean package
java -jar target/meetimeoauth-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em: http://localhost:8080

## Endpoints da API

### Autenticação OAuth

- **Iniciar fluxo OAuth**: `GET /api/auth/login`
  - Redireciona para a página de autorização do HubSpot

- **Callback OAuth**: `GET /api/auth/callback?code={code}`
  - Recebe o código de autorização e troca por um token de acesso

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

- **Obter contato por ID**: `GET /api/contacts/{contactId}`
  - Retorna os detalhes de um contato específico

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

## Segurança

A aplicação implementa as seguintes medidas de segurança:

- CORS configurado para permitir requisições de qualquer origem
- CSRF desabilitado para APIs RESTful
- Autenticação por token com sessão stateless
- Endpoints públicos limitados ao fluxo de autenticação

## Boas Práticas Implementadas

- Separação de responsabilidades (padrão MVC)
- Tratamento adequado de exceções
- Uso de DTOs para transferência de dados
- Injeção de dependências
- Princípios SOLID
- Configuração modular 