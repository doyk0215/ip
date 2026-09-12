---
name: test-ui
description: Run the Dandelion console application against command sequences and ordered expected-output checks recorded in test/ui-test-plan.md. Stop at the first failed test and show the complete console transcript.
---

# Console UI testing

Use this project-specific skill when testing the interactive Dandelion console application.

## Test-plan format

Read `test/ui-test-plan.md`. Each test case must contain:

- a `## Test case: ...` heading;
- an `**Aim:**` description;
- an `**Input:**` fenced text block containing one command per line;
- an `**Expected output:**` fenced text block containing output fragments in the order they must appear.

Every test case must include `bye` in its input so the process terminates cleanly. Expected output is checked as ordered fragments rather than as a whole exact transcript, because prompts and banners are UI framing around the meaningful responses.

## Running tests

Run the bundled runner from the repository root:

```text
python3 test-ui/scripts/run_ui_tests.py
```

The runner compiles all Java files under `src/main/java` with `javac`, starts the packaged entry point `dandelion.Dandelion` once per test case, supplies that case's commands through standard input, and checks the expected fragments in order.

If a test fails, stop immediately. Report the test name, console input, complete actual output, and expected output fragments. Do not continue to later test cases. On success, report every test's input/output transcript and the final summary.

Do not modify application code or the test plan while executing tests unless the user explicitly asks for a fix or plan update.
