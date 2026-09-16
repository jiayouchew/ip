# Wobble User Guide

Wobble is a JavaFX task companion that stores tasks in its memory tray. It supports
ToDos, deadlines, and events, and can search, review, complete, and remove them.

## Contents

- [Quick start](#quick-start)
- [Command format](#command-format)
- [Command reference](#command-reference)
- [Date and time formats](#date-and-time-formats)
- [Reminders and status tags](#reminders-and-status-tags)
- [Saving and recovering data](#saving-and-recovering-data)
- [FAQ](#faq)
- [Troubleshooting](#troubleshooting)
- [Building the fat JAR](#building-the-fat-jar)
- [Verification and CI](#verification-and-ci)

## Quick start

Before starting Wobble, make sure JDK 25 is installed. Run commands from the
project root, which is the folder containing `build.gradle` and `gradlew`.

Start the JavaFX GUI with:

```bash
./gradlew run
```

Type a command in the input box and press `Enter` or click `Send`. Try these commands:

```text
todo read book
deadline submit report /by 2026-09-15 18:00
list
```

![Wobble GUI](Ui.png)

Enter `help` at any time to display the in-app command guide. Enter `bye` to run
Wobble's robot-themed shutdown sequence and exit. The GUI window is resizable, and the
conversation area can be scrolled.

You can also run `wobble.gui.Launcher` from IntelliJ IDEA. Set both the project SDK and
the Gradle JVM to JDK 25 first.

For a console-only session, run `wobble.Wobble.main()` from IntelliJ. Both interfaces
load and save tasks in `data/wobble.txt`, relative to the directory used to start Wobble.

## Command format

### Command notation

The angle brackets in a command describe a value that you replace; do not type the
angle brackets themselves. For example, `todo <description>` becomes `todo read book`.
The square brackets in `[days]` mean that the value is optional. The parameter markers
`/by`, `/from`, and `/to` are part of the command and must be typed exactly.

Descriptions may contain spaces. Do not surround descriptions, keywords, dates, or
times with quotation marks unless the quotation marks are intended to be part of the
text.

## Command reference

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

### Adding a ToDo: `todo`

Adds a task without a date or time.

Format: `todo <description>`

Example:

```text
todo read book
```

### Adding a deadline: `deadline`

Adds a task that is due at a specified date or time.

Format: `deadline <description> /by <date/time>`

Example:

```text
deadline submit report /by 2026-09-15 18:00
```

Put the description before `/by`, and use `/by` exactly once.

### Adding an event: `event`

Adds a task that spans a start and end date/time.

Format: `event <description> /from <start> /to <end>`

Example:

```text
event team meeting /from 2026-09-15 14:00 /to 2026-09-15 16:00
```

The end must be later than the start. Use `/from` and `/to` exactly once each.

### Listing tasks: `list`

Displays every task and its current one-based task number.

Format: `list`

### Finding tasks: `find`

Searches task descriptions for a case-insensitive substring. The original task numbers
are preserved in the results. Completed, overdue, and past tasks can all be returned.

Format: `find <keyword>`

Examples:

```text
find report
find team meeting
```

### Completing a task: `mark`

Marks the selected task as done.

Format: `mark <number>`

Example: `mark 2`

### Reopening a task: `unmark`

Marks the selected task as not done again.

Format: `unmark <number>`

Example: `unmark 2`

### Deleting a task: `delete` or `remove`

Removes the selected task from the memory tray. `remove` is an alternative name for
the same operation.

Formats: `delete <number>` or `remove <number>`

Examples:

```text
delete 2
remove 2
```

### Viewing tasks on a date: `due on`

Displays deadlines whose due date matches the requested date and events that include
that date. It searches by calendar date, so use a date-only value.

Format: `due on <date>`

Example: `due on 2026-09-15`

If a date/time is supplied, Wobble uses only its date portion. Completed matching tasks
are included in the results.

### Viewing reminders: `reminders`

Shows unfinished deadlines and events within a reminder window. With no argument, the
window defaults to 7 days. A custom window can be from 0 to 36,500 days.

Formats: `reminders` or `reminders <days>`

Examples:

```text
reminders
reminders 14
```

The window starts at the beginning of today and ends the specified number of days from
the current time. Deadlines are scheduled by their due time; events are scheduled by
their start time. A value of `0` searches from the beginning of today through the
current time.

### Viewing this guide: `help`

Displays a compact command and date/time reference in the GUI or console.

Format: `help`

### Exiting Wobble: `bye`

Runs the robot-themed shutdown sequence and exits the application.

Format: `bye`

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

Past dates are allowed. An unfinished deadline or event with a past schedule is marked
in the task display instead of being rejected.

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
its start time.

Past unfinished deadlines are shown with an `[OVERDUE]` tag. Past unfinished events
are shown with a `[PAST]` tag. These tags are displayed in red in the GUI. Marking a
task as done removes its past or overdue tag.

The reminder window does not include completed tasks. A past task from an earlier day is
not returned by `reminders`, but it remains visible in `list`, `find`, and applicable
`due on` results.

## Saving and recovering data

Tasks are written automatically whenever the list changes; there is no save command.
Wobble writes through a temporary file and replaces the save file so a failed write does
not partially replace the previous file.

The save file is relative to the directory from which Wobble is started. Starting
Wobble from a different directory can therefore use a different `data/wobble.txt`.
To reset Wobble for a fresh demonstration, close the application and remove or rename
`data/wobble.txt`; Wobble creates a new empty save file when the next task is saved.

If the save file contains malformed or duplicate records, Wobble skips those records,
shows a diagnostic, and loads the remaining valid tasks. Back up the file before
editing it manually; it uses an internal encoded format and is not intended as a normal
user-editing interface.

## FAQ

### How do I move my tasks to another computer?

Install Wobble on the other computer and copy `data/wobble.txt` to the corresponding
`data/wobble.txt` path relative to the directory from which Wobble will be started.
Close Wobble before copying the file.

### Why is my task list empty?

Check that Wobble was started from the directory containing the save file you intended
to use. Also check that `data/wobble.txt` exists and is readable.

### Why did Wobble reject my command?

Check the command marker, required parameters, task number, date, and time. For a
misspelled or unrecognized command, Wobble may display a suggested command. Suggestions
are only prompts; Wobble never executes the suggested command automatically.

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
