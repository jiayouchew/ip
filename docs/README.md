# Wobble User Guide

Wobble is a friendly little robot chatbot that stores tasks in its memory tray, tracks completion, and exits when you type `bye`.

## Starting Wobble

Run `Wobble.main()` from `src/main/java/Wobble.java`.

After starting, type a task and press Enter. Wobble stores it and confirms that it was added.
Type `list` to display all stored tasks, `mark <number>` to mark a task as done, and
`unmark <number>` to mark it as unfinished again. Type `bye` to exit.

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
added: return book
Wobble note: safely tucked into the memory tray!
list
Scanning my task tray... beep!
1.[ ] read book
2.[ ] return book
mark 2
Nice! I've marked this task as done:
  [X] return book
list
Scanning my task tray... beep!
1.[ ] read book
2.[X] return book
unmark 2
OK, I've marked this task as not done yet:
  [ ] return book
bye
Bye. Hope to see you again soon!
Wobble powering down... beep!
==============================
```
