# Dependency audit — 2026-08-06

`npm audit` reports 11 findings: 1 low, 1 moderate, 9 high, and 0 critical.

The only direct production dependency named by the audit is `react-router-dom` 7.7.1 (through `react-router` 7.7.1). Other findings are transitive build/development dependencies including Rollup, PostCSS, Babel, AJV, minimatch, brace-expansion, picomatch, flatted, and js-yaml.

The audit reports fixes are available. No automatic `npm audit fix` was run because dependency updates should be reviewed, applied deliberately, and followed by lint, unit, build, and manual routing tests.

Recommended follow-up:

1. Update React Router to a fixed compatible 7.x release and test all routes.
2. Update the Vite toolchain so fixed Rollup/PostCSS versions resolve.
3. Update lint tooling for the remaining transitive advisories.
4. Rerun `npm audit`, `npm run test`, `npm run lint`, and `npm run build`.
