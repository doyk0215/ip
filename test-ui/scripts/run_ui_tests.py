#!/usr/bin/env python3
"""Run the Dandelion console UI test plan and stop at the first failure."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
import tempfile
from pathlib import Path


CASE_PATTERN = re.compile(r"^## Test case: (.+)$", re.MULTILINE)
BLOCK_PATTERN = re.compile(r"\*\*(Input|Expected output):\*\*\s*```(?:text)?\n(.*?)```", re.DOTALL)


def parse_test_plan(plan_path: Path) -> list[dict[str, str]]:
    """Parse test cases and their input/output blocks from a Markdown plan."""
    plan = plan_path.read_text(encoding="utf-8")
    headings = list(CASE_PATTERN.finditer(plan))
    cases = []

    for index, heading in enumerate(headings):
        section_end = headings[index + 1].start() if index + 1 < len(headings) else len(plan)
        section = plan[heading.end():section_end]
        blocks = {name: content.strip("\n") for name, content in BLOCK_PATTERN.findall(section)}
        if "Input" not in blocks or "Expected output" not in blocks:
            raise ValueError(
                f"Test case '{heading.group(1)}' must contain Input and Expected output blocks."
            )
        if "bye" not in blocks["Input"].splitlines():
            raise ValueError(f"Test case '{heading.group(1)}' must include bye as an input command.")
        cases.append({"name": heading.group(1), **blocks})

    if not cases:
        raise ValueError(f"No test cases found in {plan_path}.")
    return cases


def compile_application(source_root: Path, output_dir: Path) -> None:
    """Compile all project Java sources into a temporary output directory."""
    sources = sorted(source_root.rglob("*.java"))
    if not sources:
        raise RuntimeError(f"No Java source files found under {source_root}.")
    result = subprocess.run(
        ["javac", "-d", str(output_dir), *(str(source) for source in sources)],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        raise RuntimeError("Compilation failed:\n" + result.stdout + result.stderr)


def run_case(case: dict[str, str], classes_dir: Path) -> tuple[str, str, int]:
    """Run one test case and return its input, output, and exit status."""
    test_input = case["Input"] + "\n"
    result = subprocess.run(
        ["java", "-cp", str(classes_dir), "Dandelion"],
        input=test_input,
        capture_output=True,
        text=True,
        timeout=10,
    )
    output = result.stdout + result.stderr
    return test_input, output, result.returncode


def assert_expected_output(output: str, expected: str) -> None:
    """Check that expected output fragments occur in order in the actual output."""
    search_start = 0
    for fragment in expected.splitlines():
        if not fragment.strip():
            continue
        match_start = output.find(fragment, search_start)
        if match_start == -1:
            raise AssertionError(
                f"Expected output fragment not found after character {search_start}: {fragment!r}"
            )
        search_start = match_start + len(fragment)


def print_transcript(test_input: str, output: str) -> None:
    """Print a readable record of one console session."""
    print("Console input:")
    print(test_input, end="" if test_input.endswith("\n") else "\n")
    print("Console output:")
    print(output, end="" if output.endswith("\n") else "\n")


def main() -> int:
    """Compile the application and execute the test plan."""
    argument_parser = argparse.ArgumentParser(description=__doc__)
    argument_parser.add_argument(
        "--plan",
        type=Path,
        default=Path("test/ui-test-plan.md"),
        help="Markdown test plan to execute.",
    )
    argument_parser.add_argument(
        "--source-root",
        type=Path,
        default=Path("src/main/java"),
        help="Root directory containing Java sources.",
    )
    arguments = argument_parser.parse_args()

    try:
        cases = parse_test_plan(arguments.plan)
        with tempfile.TemporaryDirectory(prefix="dandelion-ui-") as temporary_dir:
            classes_dir = Path(temporary_dir)
            compile_application(arguments.source_root, classes_dir)

            for case_number, case in enumerate(cases, start=1):
                print(f"\n=== Test case {case_number}: {case['name']} ===")
                test_input, output, exit_status = run_case(case, classes_dir)
                print_transcript(test_input, output)
                if exit_status != 0:
                    print("TEST FAILED")
                    print(f"Actual output: program exited with status {exit_status}.")
                    print("Expected output fragments:")
                    print(case["Expected output"])
                    return 1
                try:
                    assert_expected_output(output, case["Expected output"])
                except AssertionError as error:
                    print("TEST FAILED")
                    print("Expected output fragments:")
                    print(case["Expected output"])
                    print(f"Actual-output detail: {error}")
                    return 1
                print("TEST PASSED")

        print(f"\nAll {len(cases)} UI test cases passed.")
        return 0
    except (AssertionError, OSError, RuntimeError, ValueError, subprocess.TimeoutExpired) as error:
        print(f"TEST SESSION FAILED: {error}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
