# Unify refactor baseline

Recorded: 2026-08-06 (America/Edmonton)

This is the starting point for an incremental refactor, not a claim that every behavior is correct.

## Repository and runtime

- Baseline commit: `afc97a12b7901cb61b1d41064e13faf98f7dd3d1`
- Worktree was clean when milestone 1 began.
- `financialapp/mvnw` was already executable and tracked as `100755`.
- No database backup was requested; its contents were declared disposable.
- Java runtime: 25.0.2 LTS; project target: Java 24
- Maven wrapper: 3.9.9; Spring Boot: 3.5.0
- PostgreSQL observed during testing: 18.3
- Node.js: 26.5.0; npm: 11.17.0; React: 19.1.0; Vite: 7.3.6

## Automated baseline

| Check | Result | Notes |
| --- | --- | --- |
| `cd financialapp && ./mvnw test` | PASS | 1 context-load test; 0 failures. Requires local PostgreSQL access. |
| `cd financial-frontend && npm run lint` | PASS | No findings. |
| `cd financial-frontend && npm run build` | PASS | TypeScript and Vite passed; bundle-size warning only. |

The first backend attempt was blocked by the managed shell's local-socket restriction. The approved rerun connected to PostgreSQL and passed.

Warnings observed:

- Open Session in View is enabled.
- The explicit PostgreSQL Hibernate dialect is redundant.
- Spring Security warns that the custom authentication provider affects automatic `UserDetailsService` configuration.
- Java 25 reports future compatibility warnings for native access and Mockito agent loading.
- The frontend main bundle is approximately 570 kB minified; Vite warns above 500 kB.

## Local configuration contract

The backend process must receive these variables:

```text
JDBC_DATABASE_USERNAME
JDBC_DATABASE_PASSWORD
PLAID_CLIENT_ID
PLAID_SECRET
PLAID_ENV
PLAID_WEBHOOK_URL
ENCRYPTION_KEY
REMEMBER_ME_KEY
```

`JDBC_DATABASE_URL` is optional locally and defaults to `jdbc:postgresql://localhost:5432/finappdb`.

The planned VS Code shortcut must provide the same variables. Never place their values in this folder.

## Current known behavior

- Backend starts with valid database and Plaid configuration.
- Signup and login have been exercised locally.
- Sandbox Link-token creation works after valid credential loading.
- Sandbox `user_good` / `pass_good` reaches Link success.
- Frontend lint and production compilation pass.
- The full current flow still needs sign-off using `smoke-test.md`.

## Known defects and risks

- Plaid URLs and environment choices are repeated in controllers/services.
- Configuration is split between Spring properties and a static dotenv class.
- The webhook URL lacks `https://`.
- `GET /api/getUserData` performs Plaid calls and database writes.
- Exceptions are swallowed, printed, or returned as uninformative 500s.
- CSRF is globally cached and can become stale.
- Token exchange triggers duplicate dashboard retrieval.
- There is one backend context test and no frontend tests.
- Plaid access tokens are stored plainly and included by `PlaidItem.toString()`.
- Webhook authenticity is not verified.
- Webhook mutations may not persist consistently.
- Hibernate `ddl-auto=update` controls schema changes.
- Controllers directly construct Plaid requests; API contracts rely heavily on loose maps.

## Privacy changes in milestone 1

- Removed the tracked legacy `.env.application.properties.env` file.
- Replaced tracked database credentials with environment-variable references.
- Replaced the hardcoded remember-me signing key with a `REMEMBER_ME_KEY` environment reference.
- Expanded ignore rules for dotenv and local secret-property files.

Ignoring or deleting a secret does not erase Git history. Any historically committed real credential should eventually be rotated.
