# Wobble User Guide

Wobble is a JavaFX task companion that stores tasks in its memory tray. It supports
ToDos, deadlines, and events, and can search, filter, complete, and remove them.

## Starting Wobble

The normal entry point is the JavaFX GUI:

```bash
./gradlew run
```

You can also run `wobble.gui.Launcher` from IntelliJ IDEA. Set both the project SDK
and the Gradle JVM to JDK 25 first. The GUI is resizable, keeps the newest conversation
message visible, and shows errors in a separate diagnostic style.

For a console-only session, run `wobble.Wobble.main()` from IntelliJ. Both interfaces
load and save tasks in `data/wobble.txt`, relative to the directory used to start Wobble.
The file and its parent directory are created automatically when a task is saved.

Enter `help` in either interface for a compact command reference. Enter `bye` to run
Wobble's robot-themed shutdown sequence and exit.

## Commands

| Command format | Purpose | Example |
| --- | --- | --- |
| `todo <description>` | Add a task without a date | `todo read book` |
| `deadline <description> /by <date/time>` | Add a task with a due date or time | `deadline submit report /by 2026-09-15 1800` |
| `event <description> /from <start> /to <end>` | Add a task spanning a time range | `event meeting /from 2026-09-15 1400 /to 2026-09-15 1600` |
| `list` | Display every task | `list` |
| `find <keyword>` | Search descriptions, ignoring letter case | `find report` |
| `mark <number>` | Mark a task as done | `mark 2` |
| `unmark <number>` | Mark a task as not done | `unmark 2` |
| `delete <number>` | Remove a task | `delete 2` |
| `remove <number>` | Alias for `delete` | `remove 2` |
| `due on <date>` | Display deadlines and events occurring on a date | `due on 2026/09/15` |
| `reminders` | Show unfinished scheduled tasks in the next 7 days | `reminders` |
| `reminders <days>` | Choose a reminder window from 0 to 36,500 days | `reminders 14` |
| `help` | Display the command reference | `help` |
| `bye` | Shut down Wobble | `bye` |

Wobble normalizes leading/trailing whitespace, repeated spaces, tabs, and command
capitalization. A command still needs the required parameters in the formats above.
Descriptions cannot be empty or contain control characters. Duplicate tasks are
rejected, and invalid commands do not change the task list.

## Date and time formats

Use a 24-hour clock. The recommended format is `yyyy-MM-dd HH:mm`, for example
`2026-09-15 18:00`.

Supported date-only formats are:

- `yyyy-MM-dd`, for example `2026-09-15`
- `yyyy.MM.dd`, for example `2026.09.15`
- `yyyy/MM/dd`, for example `2026/09/15`

For a date and time, append either `HHmm` or `HH:mm` to a supported date, for example:

- `2026/09/15 1800`
- `2026.09.15 18:00`

ISO local date-time input such as `2026-09-15T18:00` or
`2026-09-15T18:00:00` is also accepted, although the formats above are recommended
for command input.

Valid times range from `00:00` to `23:59`. Non-existent calendar dates such as
`2027-02-30`, and invalid times such as `24:00`, are rejected with a diagnostic.
Date-only values are displayed as `Sep 15 2026`; values with a time are displayed as
`Sep 15 2026 6:00 pm`.

## Reminders and status tags

`reminders` shows unfinished deadlines and events whose scheduled date is within the
requested window. A deadline is scheduled at its due time; an event is scheduled at
its start time. Completed tasks are excluded.

Past unfinished deadlines are shown with an `[OVERDUE]` tag. Past unfinished events
are shown with a `[PAST]` tag. These tags are displayed in red in the GUI. Marking a
task as done removes its past or overdue tag.

## Persistence and recovery

Tasks are written automatically whenever the list changes. Wobble writes through a
temporary file and replaces the save file so a failed write does not partially replace
the previous file. Malformed or duplicate saved records are skipped with a diagnostic;
the remaining valid tasks are still loaded.

## Building the fat JAR

From the project root, run:

```bash
./gradlew clean shadowJar
```

The executable JAR is created at `build/libs/wobble.jar`. Run it with Java 25:

```bash
java -jar build/libs/wobble.jar
```

JavaFX runtime dependencies for macOS, Windows, and Linux are included in the JAR.

## Verification and CI

Run the local checks with:

```bash
./gradlew check
```

This runs JUnit tests and Checkstyle. GitHub Actions repeats the check on Ubuntu,
macOS, and Windows for every push and pull request using Java 25.
