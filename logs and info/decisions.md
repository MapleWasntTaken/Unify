# Refactor decision log

Add a dated entry for decisions affecting architecture, contracts, persistence, security boundaries, or deployment.

## 2026-08-06 — Refactor instead of rewrite

**Status:** Accepted

**Decision:** Incrementally refactor the existing Spring Boot, React, and PostgreSQL application.

**Why:** It has demonstrated a real end-to-end flow; preserving it demonstrates debugging, testing, and migration skill.

**Consequences:** Each milestone must leave the project runnable and be split into reviewable changes.

## 2026-08-06 — Stabilization before AI and public exposure

**Status:** Accepted

**Decision:** Stabilize configuration, errors, tests, synchronization, and API boundaries before AI analytics or homelab exposure.

**Why:** Adding features now multiplies poorly observable failure modes. Immediate secret hygiene remains in scope.

## 2026-08-06 — No baseline database backup

**Status:** Accepted by project owner

**Decision:** Skip a milestone-1 dump because the new local database is disposable.

**Consequences:** Backups become mandatory once data is meaningful or non-reconstructable.

## 2026-08-06 — Refactor evidence lives in `logs and info`

**Status:** Accepted

**Decision:** Keep baseline summaries, smoke-test records, diagnostic guidance, and decisions here.

**Consequences:** Do not store secrets, tokens, raw financial data, or unlimited raw logs. Prefer sanitized summaries.

## 2026-08-06 — Privacy over checkout convenience

**Status:** Accepted

**Decision:** Tracked configuration contains names and safe defaults, never credential values.

**Consequences:** Local shortcuts supply environment variables. Historically committed real credentials must be treated as exposed and rotated.

## 2026-08-06 — Let Hibernate detect the PostgreSQL dialect

**Status:** Accepted

**Decision:** Remove the explicit `hibernate.dialect` setting.

**Why:** Hibernate detects PostgreSQL from JDBC metadata, and the baseline run identified the explicit setting as redundant.

**Consequences:** Successful startup requires database connectivity during normal application initialization, which was already required by this application.

## 2026-08-06 — Typed configuration and one Plaid environment switch

**Status:** Accepted

**Decision:** Replace static dotenv access with validated Spring configuration properties and route every Plaid call through one client selected by `PLAID_ENV=sandbox|prod`.

**Why:** Scattered URLs and nullable static values caused runtime failures and cross-environment token use.

**Consequences:** Invalid configuration prevents startup. Controllers/services cannot directly add Plaid credentials or hostnames.

## 2026-08-06 — Recover stale CSRF once

**Status:** Accepted

**Decision:** Return distinct JSON codes for missing/invalid CSRF tokens. The frontend refreshes the token and retries once, then shows a small refresh alert if recovery still fails.

**Why:** A backend/session restart previously caused an unexplained 403 while GET requests continued working.

**Consequences:** Authorization failures remain distinct and are never automatically retried.

## Template

```markdown
## YYYY-MM-DD — Title

**Status:** Proposed | Accepted | Superseded

**Decision:** What was decided.

**Why:** Forces and tradeoffs.

**Consequences:** What becomes required, easier, harder, or prohibited.
```
