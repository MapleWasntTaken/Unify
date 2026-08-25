# Manual smoke-test checklist

Run this after changes to configuration, authentication, Plaid integration, persistence, API contracts, or frontend state. Never record passwords, cookies, CSRF values, Plaid tokens, or real financial records here.

## Preconditions and build

- [ ] PostgreSQL is running and backend variables are present.
- [ ] All Plaid calls use Sandbox during local testing.
- [ ] Backend starts on 8080; frontend starts on 5173.
- [ ] `./mvnw test` exits 0.
- [ ] `npm run lint` exits 0.
- [ ] `npm run build` exits 0.

## Authentication

- [ ] CSRF endpoint returns 200 with a token and header name.
- [ ] Valid signup returns 200; duplicate returns 409; weak password returns 400.
- [ ] Valid login returns 200; invalid login returns 401.
- [ ] `whoami` confirms the session.
- [ ] Protected endpoints reject unauthenticated access.
- [ ] Logout invalidates the session.

## Plaid Sandbox

- [ ] Add Account makes exactly one Link-token request.
- [ ] Link opens and accepts `user_good` / `pass_good`.
- [ ] Link success makes exactly one exchange request.
- [ ] Exchange returns 200 and associates the item with the current user.
- [ ] Server output contains no public, Link, or access tokens.

## Dashboard and persistence

- [ ] Dashboard makes one non-overlapping request and returns 200.
- [ ] Accounts, transactions, statuses, balances, dates, and null liability values display correctly.
- [ ] Browser refresh and repeated sync do not create duplicate rows.
- [ ] A Plaid failure does not mark synchronization successful.
- [ ] Errors are visible to the user and correlate with a safe backend log.

## Update, removal, and authorization

- [ ] Login-required state displays and successful update clears it.
- [ ] Removing one account preserves unrelated accounts.
- [ ] Removing the last account removes only its Plaid item.
- [ ] A user cannot view, sync, update, or remove another user's resource.

## Webhooks

- [ ] HTTPS webhook URL is correct and reachable for the environment.
- [ ] Valid events are acknowledged promptly and duplicate delivery is idempotent.
- [ ] Login-required and pending-expiration states persist.
- [ ] Unknown events follow a documented policy.
- [ ] Authenticity validation is tested once implemented.

## Failure paths

- [ ] Missing configuration names the missing key at startup.
- [ ] Stale CSRF produces a recognizable 403 and recoverable UI.
- [ ] Expired/mismatched Plaid tokens produce specific safe errors.
- [ ] Database or Plaid outages do not corrupt state.

## Sign-off template

```text
Date/time:
Commit:
Environment:
Tester:
Passed:
Failed:
Blocked:
Related log filename(s):
Notes:
```
