# Commit Procedure & PR Checklist

This document outlines the recommended process for committing code and opening a pull request in the ChessAI repository. It follows best practices to ensure consistency, test coverage, and a smooth review + CI pipeline.

## Branching
- Always create a new feature/fix branch for your changes.
	- Naming: `feature/<ticket>-<short-desc>` or `fix/<ticket>-<short-desc>`.

## Local Development
- Keep `main` clean — work in branches.
- Rebase/merge with `main` often to keep conflicts manageable.

## Running tests
- Run the full automated test suite at least once before committing.

Example:
```bash
# run the full suite
mvn -q test

# run a single test class for faster iterations
mvn -Dtest=com.ddemott.chessai.SpecialMovesTest test

# run a single test method
mvn -Dtest=com.ddemott.chessai.SpecialMovesTest#testPawnPromotion test
```

## Commit message format
Use a small set of conventional prefixes. Example:
- `feat:` – new features
- `fix:` – bug fixes
- `refactor:` – internal changes unrelated to features or fixes
- `docs:` – changes to documentation
- `test:` – changes to tests

Clear message examples:
```bash
git commit -m "fix(Board): set piece.position in setPieceAt() to keep consistent positions"
git commit -m "feat: add opening book integration with MinMaxStrategy"
```

## Pre-commit checklist
- [ ] All tests pass (`mvn -q test`)
- [ ] No debug prints in core files (`System.out.println` used only intentionally in console UI modules)
 - [ ] No debug prints / stack traces in `src/main` files (`System.out.println`, `System.err.println`, `printStackTrace` not allowed outside console or demo code)
- [ ] CI-friendly: prefer non-interactive checks and correct exit codes
- [ ] `README.md`, `ARCHITECTURE.md`, or `CHANGELOG.md` updates included if the feature/bug fix requires it

### Note about demo/debug files
- Demo/Debug Java files that are used for local debugging are stored under `dev/demos/`.
- These demo files can contain `System.out.println` prints and are intended for local debugging; they are not part of the main product and CI does not run them by default.

## Push and PR
1. Push the branch to remote:
```bash
git push -u origin feature/<branch-name>
```
2. Open a PR against `main`. Include:
- Summary of changes
- Related ticket(s) / issue numbers
- Testing and verification notes
- If tests were added/changed, list coverage and scenarios

## CI & Review
- Wait for CI to complete; address any test failures.
- Address review comments, keeping commits small and readable.
- Rebase on `main` if needed and re-run tests locally.

## Merge & Post-merge
- Prefer squash merging or merge commits as per the team's conventions.
- Clean up: delete the feature branch on remote once merged.
- Update `CHANGELOG.md` or docs as appropriate.

## Special cases & Tips
- For hotfixes that need to land to `main` quickly, make sure to preserve the quick turnaround with tests and a concise PR.
- For large refactor or performance changes (e.g., migrating to bitboards), create a design PR first for architecture review.

---

If you like, I can add a `pre-commit` hook to automate running tests and scanning for a few patterns (like stray debug prints), and create a PR template and `CONTRIBUTING.md` that references this procedure.

## Install repository git hooks (recommended)

To enable the repository hooks that run checks like the `mvn -q test` and `System.out.println` scans, run the install helper once after cloning:

```bash
# configure the repository to use hooks under .githooks
bash scripts/install-hooks
```

After this, the pre-commit checks will run automatically for new commits in this repo worktree.

### Hook behaviors
- `pre-commit` (lightweight): runs the debug-print scan (staged files only) and a quick smoke test if test files were changed. The scan looks for `System.out.println`, `System.err.println`, and `.printStackTrace(` in `src/main/java` (excluding console UI files). This provides fast local feedback without running the full test suite.
- `pre-push` (conditional): runs smoke tests for non-main branch pushes and a full test suite for pushes to `main`.
- `CI` (GitHub Actions): always runs full tests and additional static analysis checks (Checkstyle, SpotBugs) on PRs and merges to `main`.

If you prefer a stricter developer workflow, you can update the hook scripts in `.githooks/` to enforce a heavier pre-commit step (e.g., run a full test suite), however this will increase local commit latency.

## Pre-push hook

This repository ships a `pre-push` hook which runs a heavier set of checks to prevent bad pushes into `main` from developer worktrees.

- The hook runs the **full unit test suite** (`mvn -q -B test`), and also runs the debug print scanner to ensure there are no stray `System.out.println`, `System.err.println`, or `.printStackTrace(` statements in non-console code.
- To enable the hook, run `bash scripts/install-hooks` after cloning the repository.
- To test the hook locally, run:

```bash
bash scripts/pre-push
```

If the full test suite fails, the push will be aborted and CI should show further details on the failing test(s).

## Static analysis baselines

To avoid failing CI on historical style/analysis issues while still preventing new violations, we use *baselines* for Checkstyle and SpotBugs.

- If you need to regenerate baseline files (for example after a major refactor), run:
	```bash
	bash scripts/generate-baselines.sh
	git add config/checkstyle config/spotbugs
	git commit -m "chore: update static analysis baselines"
	git push
	```
- CI runs a comparison between the baseline and the current report; if *new* violations are found, the CI job will fail.