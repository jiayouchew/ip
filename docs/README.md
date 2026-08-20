# Wobble User Guide

Wobble is a friendly little robot chatbot that stores tasks in its memory tray, tracks completion, and exits when you type `bye`.

## Starting Wobble

Run `Wobble.main()` from `src/main/java/Wobble.java`.

After starting, type a task and press Enter. Wobble stores it and confirms that it was added.
Type `todo <description>` to add a ToDo, `deadline <description> /by <date>` to add a
Deadline, or `event <description> /from <start> /to <end>` to add an Event.
Type `list` to display all stored tasks, `mark <number>` to mark a task as done, and
`unmark <number>` to mark it as unfinished again. Type `bye` to exit.

Wobble gives specific, robot-themed error messages for empty descriptions, malformed dates or times,
unknown commands, invalid task numbers, and a full task tray. Invalid commands do not
change the task list.

Example output:

```
==============================
  WOBBL-E // systems online
==============================
Hello! I'm Wobble.
Beep boop! Your friendly little robot companion is ready.
What can I do for you?
==============================
My memory tray is polished and ready for tasks.
read book
added: read book
Wobble note: safely tucked into the memory tray!
return book
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
list
Scanning my task tray... beep!
1.[T][ ] read book
2.[T][ ] return book
mark 2
Nice! I've marked this task as done:
  [T][X] return book
list
Scanning my task tray... beep!
1.[T][ ] read book
2.[T][X] return book
unmark 2
OK, I've marked this task as not done yet:
  [ ] return book
bye
Bye. Hope to see you again soon!
Wobble powering down... beep!
==============================
```
