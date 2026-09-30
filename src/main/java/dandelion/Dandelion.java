package dandelion;

import dandelion.parser.Parser;
import dandelion.storage.Storage;
import dandelion.task.Task;
import dandelion.task.TaskList;
import dandelion.ui.Ui;

import java.io.IOException;

/**
 * Runs the Dandelion chatbot's command loop.
 */
public class Dandelion {
    /**
     * Tasks created during the current session.
     */
    private final TaskList tasks = new TaskList();

    /**
     * Loads and saves tasks on the hard disk.
     */
    private final Storage storage = new Storage();

    /**
     * Converts user input into task objects.
     */
    private final Parser parser = new Parser();

    /**
     * Handles console input and session-level messages.
     */
    private final Ui ui = new Ui();

    /**
     * Starts the chatbot.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Dandelion().run();
    }

    /**
     * Runs the input loop until the user enters {@code bye}.
     */
    private void run() {
        loadTasks();
        ui.showWelcomeMessage();

        try (Ui currentUi = ui) {
            while (true) {
                String command = currentUi.readCommand();
                if (command == null || !handleCommand(command)) {
                    break;
                }
                System.out.println();
            }
        }
    }

    /**
     * Handles one user command by dispatching it to the appropriate method.
     *
     * @param command User input.
     * @return {@code false} when the application should stop; {@code true} otherwise.
     */
    private boolean handleCommand(String command) {
        String commandWord = parser.getCommandWord(command);

        switch (commandWord) {
        case "bye":
            ui.showGoodbyeMessage();
            return false;
        case "list":
            handleList();
            break;
        case "mark":
            handleMark(command);
            break;
        case "unmark":
            handleUnmark(command);
            break;
        case "delete":
            handleTaskDeletion(command);
            System.out.println("         Number of tasks: " + tasks.size());
            break;
        case "todo":
        case "deadline":
        case "event":
            handleTaskCreation(command, commandWord);
            System.out.println("         Number of tasks: " + tasks.size());
            break;
        default:
            handleUnknownCommand(command);
            break;
        }
        return true;
    }

    /**
     * Displays all stored tasks.
     */
    private void handleList() {
        if (tasks.isEmpty()) {
            System.out.println("  bot  › No tasks added yet.");
            return;
        }

        for (int i = 0; i < tasks.size(); i++) {
            String linePrefix = i == 0 ? "  bot  › " : "         ";
            System.out.println(linePrefix + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Marks the requested task as done.
     */
    private void handleMark(String command) {
        updateTaskStatus(command, true);
    }

    /**
     * Marks the requested task as not done.
     */
    private void handleUnmark(String command) {
        updateTaskStatus(command, false);
    }

    /**
     * Updates a task's completion status.
     *
     * @param command User command containing a task number.
     * @param isDone Whether the task should be marked as done.
     */
    private void updateTaskStatus(String command, boolean isDone) {
        if (tasks.isEmpty()) {
            System.out.println("  bot  › No tasks added yet.");
            return;
        }

        try {
            int taskNumber = Integer.parseInt(parser.getArgument(command));
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                System.out.println("  bot  › Please enter a task number from 1 to " + tasks.size() + ".");
                return;
            }

            Task task = tasks.get(taskNumber - 1);
            if (isDone) {
                task.markAsDone();
                saveTasks();
                System.out.println("  bot  › Nice! I've marked this task as done:");
            } else {
                task.markAsNotDone();
                saveTasks();
                System.out.println("  bot  › OK, I've marked this task as not done yet:");
            }
            System.out.println("           " + task);
        } catch (NumberFormatException exception) {
            System.out.println("  bot  › Please enter a task number after the command.");
        }
    }

    /**
     * Deletes the task specified in a delete command.
     *
     * @param command User command containing a task number.
     */
    private void handleTaskDeletion(String command) {
        if (tasks.isEmpty()) {
            System.out.println("  bot  › No tasks added yet.");
            return;
        }

        try {
            int taskNumber = Integer.parseInt(parser.getArgument(command));
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                System.out.println("  bot  › Please enter a task number from 1 to " + tasks.size() + ".");
                return;
            }
            Task deletedTask = tasks.get(taskNumber - 1);
            deleteTask(taskNumber - 1);
            System.out.println("  bot  › deleted: " + deletedTask);
        } catch (NumberFormatException exception) {
            System.out.println("  bot  › Please enter a task number after the command.");
        }
    }

    /**
     * Creates and stores a task based on its command type.
     *
     * @param command     Complete user command.
     * @param commandWord Command type.
     */
    private void handleTaskCreation(String command, String commandWord) {
        try {
            Task task = switch (commandWord) {
            case "todo" -> parser.parseTodo(command);
            case "deadline" -> parser.parseDeadline(command);
            case "event" -> parser.parseEvent(command);
            default -> throw new IllegalArgumentException("Unknown task type.");
            };
            addTask(task);
        } catch (IllegalArgumentException exception) {
            System.out.println("  bot  › " + exception.getMessage());
        }
    }

    /**
     * Handles invalid command by showing error message.
     *
     * @param command User input.
     */
    private void handleUnknownCommand(String command) {
        System.out.println("  bot  › Unknown command.");
    }

    /**
     * Adds a task to the task list and confirms the addition.
     *
     * @param task Task to add.
     */
    private void addTask(Task task) {
        tasks.add(task);
        saveTasks();
        System.out.println("  bot  › added: " + task);
    }

    /**
     * Removes the task at the specified zero-based index.
     *
     * @param index Zero-based index of the task to remove.
     */
    private void deleteTask(int index) {
        tasks.remove(index);
        saveTasks();
    }

    /**
     * Loads saved tasks into the task list when the chatbot starts.
     */
    private void loadTasks() {
        try {
            System.out.println("Loading...");
            for (Task task : storage.loadTasks()) {
                tasks.add(task);
            }
            if (!tasks.isEmpty()) {
                System.out.println(tasks.size() + " task(s) successfully loaded.");
            } else {
                System.out.println("No save data exists.");
            }
        } catch (IOException | IllegalArgumentException exception) {
            System.out.println("  bot  › Unable to load saved tasks: "
                    + exception.getMessage());
        }
    }

    /**
     * Saves the current task list to the data file.
     */
    private void saveTasks() {
        try {
            storage.saveTasks(tasks);
        } catch (IOException exception) {
            System.out.println("  bot  › Unable to save tasks: "
                    + exception.getMessage());
        }
    }
}
