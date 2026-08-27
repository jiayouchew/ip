# Wobble project template

This is a project template for a greenfield Java project named _Wobble_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
   1. After that, locate the `src/main/java/wobble/Wobble.java` file, right-click it, and choose `Run wobble.Wobble.main()` (if the code editor is showing compile errors, try restarting the IDE). Type tasks into the console; Wobble stores them, displays them with `list`, lets you use `mark <number>`, `unmark <number>`, `delete <number>`, `due on <date>`, and `find <keyword>`, and exits when you type `bye`.

   Wobble saves tasks automatically to the relative file `data/wobble.txt` and loads them
   again the next time it starts. The `data` folder is created automatically if needed.
   ```
   ==============================
     WOBBL-E // Systems Online
   ==============================
   Hello! I'm Wobble.
   Beep boop! Your friendly little robot companion is ready.
   My memory tray is polished and ready for tasks.
   What can I do for you?
   ==============================
   todo read book
   Beep boop! Got it. I've added this task to my memory tray:
     [T][ ] read book
   Now you have 1 tasks in the list.
   deadline return book /by 2019-12-02 1800
   Beep boop! Got it. I've added this task to my memory tray:
     [D][ ] return book (by: Dec 2 2019 6:00 pm)
   Now you have 2 tasks in the list.
   list
   Scanning my task tray... whirr, beep!
   1.[T][ ] read book
   2.[D][ ] return book (by: Dec 2 2019 6:00 pm)
   mark 2
   Nice! I've marked this task as done:
     [D][X] return book (by: Dec 2 2019 6:00 pm)
   delete 2
   Noted. I've removed this task:
     [D][X] return book (by: Dec 2 2019 6:00 pm)
   Now you have 1 tasks in the list.
   bye
   Bye. Hope to see you again soon!
   Wobble powering down... beep!
   ==============================
   ```

   Invalid commands receive a diagnostic message without changing the task list:

   ```
   todo
   Wobble diagnostic: a todo description cannot be empty.
   blah
   Wobble diagnostic: I do not know that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running a fat JAR

From the project root, create the executable fat JAR with:

```bash
./gradlew shadowJar
```

The output is created at `build/libs/wobble.jar`. Run it with:

```bash
java -jar build/libs/wobble.jar
```

The JAR includes the application classes and runtime dependencies, and its entry point
is configured as `wobble.Wobble`.
