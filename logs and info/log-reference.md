# Log and diagnostic reference

Each backend start writes to the terminal and to `financialapp/logs/unify-YYYYMMDD-HHMMSS.log`. The runtime directory is ignored by Git. Set `LOG_DIR` to redirect it.

Every HTTP response includes `X-Request-ID`. The same ID appears in the corresponding backend log line as the value inside the second brackets. Use it to connect a browser failure to server activity.

## Sources

| Logger/signal | Corresponding component |
| --- | --- |
| `RequestCorrelationFilter` | Completed HTTP method, path, status, and duration |
| `PlaidController` | Link creation, exchange, update, and removal workflow |
| `PlaidApiClient` | Outbound Plaid operation, environment, outcome, and duration |
| `PlaidService` | Account/transaction synchronization |
| `WebhookControllers` | Webhook type, code, item, and dispatch |
| `GlobalExceptionHandler` | Safe application/Plaid failure classification |
| Spring Security filter chain | Authentication, authorization, CSRF, login/logout |
| Hibernate / Spring Data | Entity queries, persistence, transactions, lazy loading |
| HikariCP | PostgreSQL pool and connectivity |
| PostgreSQL server | Database login, constraints, locks, and SQL failures |
| Browser Network panel | Frontend request status, response JSON, and `X-Request-ID` |
| Browser Console | Frontend exceptions and rejected promises |
| Maven/Surefire | Backend compilation and tests |
| Vite/TypeScript/ESLint/Vitest | Frontend build, static analysis, and tests |
| Plaid Dashboard | Plaid-side request ID and environment diagnostics |

## Reading failures

Find the deepest `Caused by:` and first `com.financialapp.financialapp` frame.

- `CSRF_TOKEN_MISSING` / `CSRF_TOKEN_INVALID`: frontend refreshes the token and retries once.
- `ACCESS_DENIED`: real authorization denial; never automatically retried.
- `PLAID_LINK_TOKEN_INVALID`: create a fresh Link token.
- `PLAID_PUBLIC_TOKEN_INVALID`: restart the new-account Link flow.
- `PLAID_ACCESS_TOKEN_INVALID`: reconnect/update the existing item.
- `PLAID_CONFIGURATION_ERROR`: environment or server configuration problem.
- Hikari/JDBC error: database configuration or reachability.
- Constraint error: inspect the named constraint and transaction.
- Generic 500: use `X-Request-ID` to find the unhandled server exception.

## Never log or retain

Passwords/hashes, cookies, CSRF values, authorization headers, Plaid secrets or tokens, encryption keys, complete env files, full bank/card numbers, or unnecessary real financial records. Plaid error logs retain only safe error type/code, operation, status, environment, and Plaid `request_id`.
