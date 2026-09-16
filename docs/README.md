# Wobble User Guide

Wobble is a JavaFX task companion that stores tasks in its memory tray. It supports
ToDos, deadlines, and events, and can search, filter, complete, and remove them.

## Starting Wobble

Before starting Wobble, make sure JDK 25 is installed. Run commands from the
project root, which is the folder containing `build.gradle` and `gradlew`.

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

### Command notation

The angle brackets in a command describe a value that you replace; do not type the
angle brackets themselves. For example, `todo <description>` becomes `todo read book`.
The square brackets in `[days]` mean that the value is optional. The parameter markers
`/by`, `/from`, and `/to` are part of the command and must be typed exactly.

Descriptions may contain spaces. Do not surround descriptions, keywords, dates, or
times with quotation marks unless the quotation marks are intended to be part of the
text.

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

Task numbers are one-based and come from the order shown by `list`. Use the current
number when marking, unmarking, deleting, or removing a task. The numbers can change
after a task is deleted. `remove` is an alias for `delete`; both commands behave the
same way.

The `find` command searches for a case-insensitive substring in task descriptions. It
can return completed, overdue, and past tasks. The `due on` command displays deadlines
whose due date matches the requested date and events that include that date; completed
tasks are included if they match. The `reminders` command displays only unfinished
deadlines and events in its reminder window.

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
for command input. The ISO form is useful for deadlines and events; use a date-only
value with `due on` because that command searches by calendar date. If a time is
included with `due on`, Wobble uses only its date portion.

Valid times range from `00:00` to `23:59`. Non-existent calendar dates such as
`2027-02-30`, and invalid times such as `24:00`, are rejected with a diagnostic.
Date-only values are displayed as `Sep 15 2026`; values with a time are displayed as
`Sep 15 2026 6:00 pm`.

The format used for display is different from the format used for input. For example,
enter `deadline report /by 2026-09-15 1800`, not
`deadline report /by Sep 15 2026 6:00 pm`.

## Common mistakes

- Use `/by`, `/from`, and `/to` exactly once where required.
- Put the description before the date marker, for example
  `deadline submit report /by 2026-09-15 18:00`.
- For an event, make the end later than the start:
  `event meeting /from 2026-09-15 14:00 /to 2026-09-15 16:00`.
- Use a real calendar date. Dates such as `2027-02-30` are invalid.
- Use a 24-hour time from `00:00` through `23:59`; `6:00 pm` and `24:00` are not
  accepted as command input.
- If a command is misspelled or its format is invalid, Wobble displays a diagnostic
  and may suggest a corrected command. The original command is not executed.

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

The save file is relative to the directory from which Wobble is started. Starting
Wobble from a different directory can therefore use a different `data/wobble.txt`.
To reset Wobble for a fresh demonstration, close the application and remove or rename
`data/wobble.txt`; Wobble creates a new empty save file when the next task is saved.

## Troubleshooting

If Gradle reports `invalid source release: 25`, or Java reports that a class was
compiled by a more recent version, the current terminal or IDE is using the wrong
Java version. Check it with:

```bash
java -version
```

It must report Java 25. In IntelliJ IDEA, set both the project SDK and the Gradle JVM
to JDK 25. On macOS with SDKMAN, switch Java in the same terminal session used to
run Wobble:

```bash
sdk use java 25.0.3.fx-zulu
java -version
```

If Wobble starts with an empty tray unexpectedly, check that you launched it from the
intended project directory and that `data/wobble.txt` is readable.

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
