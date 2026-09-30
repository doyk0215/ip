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
                ui.showCommandSeparator();
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
            ui.showTaskList(tasks);
            break;
        case "mark":
            handleMark(command);
            break;
        case "unmark":
            handleUnmark(command);
            break;
        case "delete":
            handleTaskDeletion(command);
            ui.showTaskCount(tasks.size());
            break;
        case "todo":
        case "deadline":
        case "event":
            handleTaskCreation(command, commandWord);
            ui.showTaskCount(tasks.size());
            break;
        default:
            ui.showUnknownCommand();
            break;
        }
        return true;
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
            ui.showNoTasksMessage();
            return;
        }

        try {
            int taskNumber = Integer.parseInt(parser.getArgument(command));
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                ui.showInvalidTaskNumber(tasks.size());
                return;
            }

            Task task = tasks.get(taskNumber - 1);
            if (isDone) {
                task.markAsDone();
                saveTasks();
            } else {
                task.markAsNotDone();
                saveTasks();
            }
            ui.showTaskStatusUpdated(task, isDone);
        } catch (NumberFormatException exception) {
            ui.showMissingTaskNumber();
        }
    }

    /**
     * Deletes the task specified in a delete command.
     *
     * @param command User command containing a task number.
     */
    private void handleTaskDeletion(String command) {
        if (tasks.isEmpty()) {
            ui.showNoTasksMessage();
            return;
        }

        try {
            int taskNumber = Integer.parseInt(parser.getArgument(command));
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                ui.showInvalidTaskNumber(tasks.size());
                return;
            }
            Task deletedTask = tasks.get(taskNumber - 1);
            deleteTask(taskNumber - 1);
            ui.showTaskDeleted(deletedTask);
        } catch (NumberFormatException exception) {
            ui.showMissingTaskNumber();
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
            ui.showError(exception.getMessage());
        }
    }

    /**
     * Adds a task to the task list and confirms the addition.
     *
     * @param task Task to add.
     */
    private void addTask(Task task) {
        tasks.add(task);
        saveTasks();
        ui.showTaskAdded(task);
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
            ui.showLoading();
            for (Task task : storage.loadTasks()) {
                tasks.add(task);
            }
            if (!tasks.isEmpty()) {
                ui.showLoadedTaskCount(tasks.size());
            } else {
                ui.showNoSaveData();
            }
        } catch (IOException | IllegalArgumentException exception) {
            ui.showLoadingError(exception.getMessage());
        }
    }

    /**
     * Saves the current task list to the data file.
     */
    private void saveTasks() {
        try {
            storage.saveTasks(tasks);
        } catch (IOException exception) {
            ui.showSavingError(exception.getMessage());
        }
    }
}
