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
1. After that, locate the `src/main/java/Wobble.java` file, right-click it, and choose `Run Wobble.main()` (if the code editor is showing compile errors, try restarting the IDE). Type tasks into the console; Wobble stores them, displays them with `list`, lets you use `mark <number>` and `unmark <number>`, and exits when you type `bye`.
   ```
   ==============================
     WOBBL-E // systems online
   ==============================
   Hello! I'm Wobble.
   Beep boop! Your friendly little robot companion is ready.
   My memory tray is polished and ready for tasks.
   What can I do for you?
   ==============================
   read book
   added: read book
   Wobble note: safely tucked into the memory tray!
   return book
   added: return book
   Wobble note: safely tucked into the memory tray!
   list
   Scanning my task tray... beep!
   1. read book
   2. return book
   mark 2
   Nice! I've marked this task as done:
     [X] return book
   bye
   Bye. Hope to see you again soon!
   Wobble powering down... beep!
   ==============================
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
