# CloudTask Enterprise — Login com Google e GitHub

A autenticação social usa **Spring Security OAuth2 Client** no backend. Após o provedor autenticar o usuário, o backend cria ou localiza a conta, emite um JWT próprio do CloudTask e redireciona o navegador para o frontend.

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

Na raiz do projeto:

```powershell
Copy-Item .env.example .env
notepad .env
```

Preencha o arquivo com os Client IDs e Client Secrets reais dos dois provedores:

```dotenv
SPRING_PROFILES_ACTIVE=oauth
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
GITHUB_CLIENT_ID=...
GITHUB_CLIENT_SECRET=...
APP_OAUTH_FRONTEND_REDIRECT=http://localhost:5173
```

> O profile `oauth` atual registra Google e GitHub. Portanto, os quatro valores de credenciais precisam estar preenchidos para o backend iniciar com esse profile.

### Regras de segurança

- `GOOGLE_CLIENT_ID` não é o e-mail da conta Google.
- `GOOGLE_CLIENT_SECRET` não é a senha do Gmail.
- `GITHUB_CLIENT_ID` não é o nome de usuário do GitHub.
- `GITHUB_CLIENT_SECRET` não é a senha da conta GitHub.
- Nunca envie Client Secrets para o frontend, para commits, issues, PRs ou mensagens públicas.
- Se um Client Secret for exposto, gere outro no provedor, atualize o `.env` e revogue o secret antigo.
- O arquivo `.env` é ignorado pelo Git; somente `.env.example` deve ser versionado.

Confirme localmente:

```powershell
git check-ignore .env
```

O resultado esperado é:

```text
.env
```

## 2. Google OAuth 2.0

Crie um cliente OAuth do tipo **Web application** no Google Auth Platform.

Configure a URI de redirecionamento autorizada exatamente como:

```text
http://localhost:8080/login/oauth2/code/google
```

Use o Client ID e o Client Secret gerados pelo Google nos campos:

```dotenv
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
```

O Client ID normalmente termina com:

```text
.apps.googleusercontent.com
```

Em ambiente de teste, adicione a conta Google utilizada no login à lista de test users quando o Google Auth Platform exigir isso.

## 3. GitHub OAuth App

Crie uma OAuth App em **GitHub > Settings > Developer settings > OAuth Apps**.

Configure:

```text
Application name
CloudTask Enterprise Local

Homepage URL
http://localhost:5173

Authorization callback URL
http://localhost:8080/login/oauth2/code/github
```

Use o Client ID e o Client Secret gerados pelo GitHub nos campos:

```dotenv
GITHUB_CLIENT_ID=...
GITHUB_CLIENT_SECRET=...
```

## 4. Portas locais

A stack local utiliza:

```text
Frontend      http://localhost:5173
Backend       http://localhost:8080
PostgreSQL    localhost:5433
Prometheus    http://localhost:9090
Grafana       http://localhost:3000
```

O PostgreSQL usa a porta `5433` apenas no host para evitar conflito com instalações locais de PostgreSQL. Dentro da rede Docker, o backend continua se conectando a:

```text
postgres:5432
```

## 5. Subir a stack

```powershell
docker compose down --remove-orphans
docker compose up --build -d
```

Aguarde a inicialização e valide:

```powershell
docker compose ps -a
```

O backend deve aparecer como `Up` e o PostgreSQL como `healthy`.

Para confirmar o profile OAuth sem exibir segredos:

```powershell
docker compose exec backend printenv SPRING_PROFILES_ACTIVE
```

Resultado esperado:

```text
oauth
```

## 6. Testar os provedores

Acesse:

```text
http://localhost:5173
```

Teste separadamente:

- **Google** — deve abrir o consentimento/login Google e retornar ao CloudTask autenticado.
- **GitHub** — deve abrir a autorização GitHub e retornar ao CloudTask autenticado.

Não abra manualmente as URLs abaixo para iniciar o login:

```text
http://localhost:8080/login/oauth2/code/google
http://localhost:8080/login/oauth2/code/github
```

Elas são callbacks utilizados pelos provedores após a autenticação.

Para iniciar diretamente um fluxo OAuth, use:

```text
http://localhost:8080/oauth2/authorization/google
http://localhost:8080/oauth2/authorization/github
```

## 7. Troubleshooting

### `Client id of registration 'google' must not be empty`

O `GOOGLE_CLIENT_ID` não chegou ao container ou está vazio. Revise o `.env` e recrie o backend.

### `Client id of registration 'github' must not be empty`

O `GITHUB_CLIENT_ID` não chegou ao container ou está vazio. Como o profile `oauth` registra os dois provedores, ambos precisam estar configurados.

### `invalid_client`

Confira se Client ID e Client Secret pertencem ao mesmo cliente OAuth e se o Client ID é realmente o identificador gerado pelo provedor.

### `ERR_CONNECTION_REFUSED` em `localhost:8080`

Verifique se o backend continua ativo:

```powershell
docker compose ps -a
docker compose logs backend --tail=120
```

### `Bind for 0.0.0.0:5432 failed: port is already allocated`

A configuração versionada usa `5433:5432` para evitar esse conflito. Se houver uma stack antiga, derrube-a e recrie os containers:

```powershell
docker compose down --remove-orphans
docker compose up --build -d
```

## Segurança para produção

- `.env` permanece fora do Git e do GitHub.
- Client Secrets nunca devem ser enviados ao frontend.
- O backend converte a autenticação OAuth em um JWT próprio do CloudTask.
- O JWT é devolvido ao SPA no fragmento `#` da URL, evitando o envio do token ao servidor web do frontend durante o redirect.
- Em produção, prefira um fluxo com cookie `HttpOnly` ou troca de código de curta duração para reduzir a exposição do token ao JavaScript.
- Segredos devem migrar para **AWS Secrets Manager** ou **AWS Systems Manager Parameter Store**.
- Redirect URIs de produção devem usar HTTPS e domínios controlados.
