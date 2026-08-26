# CloudTask Enterprise — Login com Google e GitHub

A autenticação social usa **Spring Security OAuth2 Client** no backend. Após o provedor autenticar o usuário, o backend cria/localiza a conta, emite o JWT do CloudTask e redireciona o navegador para o frontend.

## Fluxo

```text
React
  ↓
/oauth2/authorization/{provider}
  ↓
Spring Security OAuth2 Client
  ↓
Google ou GitHub
  ↓
/login/oauth2/code/{provider}
  ↓
OAuth2AuthenticationSuccessHandler
  ↓
JWT CloudTask
  ↓
http://localhost:5173/oauth2/callback#token=...
```

## 1. Criar o arquivo `.env`

Copie o template:

```powershell
Copy-Item .env.example .env
```

Preencha os valores reais sem versionar o arquivo `.env`.

```dotenv
SPRING_PROFILES_ACTIVE=oauth
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
GITHUB_CLIENT_ID=...
GITHUB_CLIENT_SECRET=...
APP_OAUTH_FRONTEND_REDIRECT=http://localhost:5173
```

## 2. Google OAuth 2.0

No projeto OAuth do Google, configure a URI de redirecionamento autorizada:

```text
http://localhost:8080/login/oauth2/code/google
```

Use o Client ID e Client Secret gerados nos campos `GOOGLE_CLIENT_ID` e `GOOGLE_CLIENT_SECRET`.

## 3. GitHub OAuth App

Configure a aplicação OAuth do GitHub com:

```text
Homepage URL
http://localhost:5173

Authorization callback URL
http://localhost:8080/login/oauth2/code/github
```

Use o Client ID e Client Secret gerados nos campos `GITHUB_CLIENT_ID` e `GITHUB_CLIENT_SECRET`.

## 4. Subir a stack

```powershell
docker compose down
docker compose up --build -d
```

Acesse:

```text
http://localhost:5173
```

A tela de autenticação oferece:

- login tradicional com e-mail e senha
- criação de conta
- login com Google
- login com GitHub
- alternância de tema

## Segurança

- `.env` permanece fora do Git e do GitHub.
- Client secrets nunca devem ser enviados para o frontend.
- O backend converte a autenticação OAuth em um JWT próprio do CloudTask.
- O JWT é devolvido ao SPA no fragmento `#` da URL para evitar envio do token ao servidor web do frontend durante o redirect.
- Em produção, os segredos devem migrar para AWS Secrets Manager ou SSM Parameter Store.
