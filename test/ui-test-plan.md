# Dandelion UI test plan

Run the plan from the repository root with:

```text
python3 test-ui/scripts/run_ui_tests.py
```

The runner compiles the application before testing. Each expected-output line is a meaningful output fragment; fragments must appear in the listed order in the console output.

## Test case: Create and manage all task types

**Aim:** Verify that todo, deadline, and event commands create the correct task types, and that marking a task updates its status.

**Input:**

```text
todo read book
deadline return book /by Sunday
event team meeting /from Monday 10am /to Monday 11am
list
mark 2
list
bye
```

**Expected output:**

```text
added: [T][ ] read book
added: [D][ ] return book (by: Sunday)
added: [E][ ] team meeting (from: Monday 10am to: Monday 11am)
1.[T][ ] read book
2.[D][ ] return book (by: Sunday)
3.[E][ ] team meeting (from: Monday 10am to: Monday 11am)
Nice! I've marked this task as done:
[D][X] return book (by: Sunday)
Bye, User.
```

## Test case: Reject an invalid deadline and continue

**Aim:** Verify that a missing `/by` marker displays an error without terminating the session.

**Input:**

```text
deadline return book
todo recover after error
bye
```

**Expected output:**

```text
Deadline must include /by.
added: [T][ ] recover after error
Bye, User.
```

## Test case: Reject an event with reversed time markers

**Aim:** Verify that `/from` must appear before `/to` and that the application continues afterward.

**Input:**

```text
event meeting /to 11am /from 10am
todo recover after event error
bye
```

**Expected output:**

```text
Event must place /from before /to.
added: [T][ ] recover after event error
Bye, User.
```

## Test case: Handle invalid mark and unmark arguments

**Aim:** Verify that invalid task numbers produce messages and valid unmark commands still work.

**Input:**

```text
todo submit assignment
mark abc
mark 2
unmark 1
bye
```

**Expected output:**

```text
added: [T][ ] submit assignment
Please enter a task number after the command.
Please enter a task number from 1 to 1.
OK, I've marked this task as not done yet:
[T][ ] submit assignment
Bye, User.
```
