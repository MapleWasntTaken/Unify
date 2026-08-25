# Configuration reference

The backend uses Spring Boot configuration properties. For local development it optionally imports `financialapp/.env`; production should inject the same keys through its secret/environment system.

Start the backend from `financialapp/` so the optional `.env` import resolves consistently.

## Required keys

| Key | Purpose | Safe example |
| --- | --- | --- |
| `JDBC_DATABASE_URL` | PostgreSQL JDBC address | `jdbc:postgresql://localhost:5432/finappdb` |
| `JDBC_DATABASE_USERNAME` | Database login | `unify` |
| `JDBC_DATABASE_PASSWORD` | Database password | never document the real value |
| `PLAID_CLIENT_ID` | Plaid application ID | never document the real value |
| `PLAID_SECRET` | Secret for the selected Plaid environment | never document the real value |
| `PLAID_ENV` | Selects every Plaid API base URL | `sandbox` or `prod` |
| `PLAID_WEBHOOK_URL` | HTTPS webhook destination sent to Plaid | `https://example.com/api/plaid/webhook` |
| `REMEMBER_ME_KEY` | Signs remember-me cookies | at least 32 characters |
| `ENCRYPTION_KEY` | Reserved for token encryption work | never document the real value |

`PLAID_ENV=sandbox` selects `https://sandbox.plaid.com`. `PLAID_ENV=prod` selects `https://production.plaid.com`. Any other value prevents startup.

The Plaid client adds `client_id` and `secret` internally. Controllers and services must never build credentials into request bodies themselves or hardcode a Plaid hostname.

## Startup validation

Plaid and remember-me settings are typed and validated while Spring creates the application context. Missing/blank keys identify the configuration property that could not bind rather than failing later inside a request.

## Local setup

```bash
cd financialapp
cp .env.example .env
```

Replace every placeholder locally. The real `.env` remains ignored. Do not paste its contents into logs, issues, chat, or documentation.

The historical local key `Remember_Me_Key` must be renamed exactly to `REMEMBER_ME_KEY`. Add `PLAID_WEBHOOK_URL` if it is missing.

## Logging

Each backend start creates `financialapp/logs/unify-YYYYMMDD-HHMMSS.log` and continues writing the same events to the console. Set `LOG_DIR` to redirect runtime logs. The directory is ignored by Git.

Logs include an `X-Request-ID` correlation value for HTTP requests. Plaid failures retain the Plaid `request_id`, error type/code, operation, environment, and status, but never the submitted body or tokens.
