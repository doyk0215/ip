import java.util.Scanner;

/**
 * Runs the Dandelion chatbot's command loop.
 */
public class Dandelion {
    /** Maximum number of tasks that can be stored during one session. */
    private static final int MAX_TASKS = 100;

    /** Text displayed when the application starts. */
    private static final String BANNER = """
                     .
                  \\  |  /
                ――  (✻)  ――
                     |
                D A N D E L I O N
                
                """;

    /** Tasks created during the current session. */
    private final Task[] tasks = new Task[MAX_TASKS];

    /** Number of tasks currently stored. */
    private int taskCount;

    /** Converts user input into task objects. */
    private final Parser parser = new Parser();

    /**
     * Starts the chatbot.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Dandelion().run();
    }

    /** Runs the input loop until the user enters {@code bye}. */
    private void run() {
        showWelcomeMessage();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("  you  › ");
                System.out.flush();

                if (!scanner.hasNextLine()) {
                    System.out.println();
                    break;
                }

                String command = scanner.nextLine().trim();
                if (!handleCommand(command)) {
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
            handleBye();
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
        case "todo":
        case "deadline":
        case "event":
            handleTaskCreation(command, commandWord);
            System.out.println("  Number of tasks: " + taskCount);
            break;
        default:
            handleUnknownCommand(command);
            break;
        }

        return true;
    }

    /** Displays the welcome message. */
    private void showWelcomeMessage() {
        System.out.print(BANNER);
        System.out.println("  Welcome, User.");
        System.out.println("  Type anything...");
        System.out.println();
    }

    /** Displays the goodbye message. */
    private void handleBye() {
        System.out.println("  bot  › Bye, User.");
    }

    /** Displays all stored tasks. */
    private void handleList() {
        if (taskCount == 0) {
            System.out.println("  bot  › No tasks added yet.");
            return;
        }

        for (int i = 0; i < taskCount; i++) {
            String linePrefix = i == 0 ? "  bot  › " : "         ";
            System.out.println(linePrefix + (i + 1) + "." + tasks[i]);
        }
    }

    /** Marks the requested task as done. */
    private void handleMark(String command) {
        updateTaskStatus(command, true);
    }

    /** Marks the requested task as not done. */
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
        if (taskCount == 0) {
            System.out.println("  bot  › No tasks added yet.");
            return;
        }

        try {
            int taskNumber = Integer.parseInt(parser.getArgument(command));
            if (taskNumber < 1 || taskNumber > taskCount) {
                System.out.println("  bot  › Please enter a task number from 1 to " + taskCount + ".");
                return;
            }

            Task task = tasks[taskNumber - 1];
            if (isDone) {
                task.markAsDone();
                System.out.println("  bot  › Nice! I've marked this task as done:");
            } else {
                task.markAsNotDone();
                System.out.println("  bot  › OK, I've marked this task as not done yet:");
            }
            System.out.println("           " + task);
        } catch (NumberFormatException exception) {
            System.out.println("  bot  › Please enter a task number after the command.");
        }
    }

    /**
     * Creates and stores a task based on its command type.
     *
     * @param command Complete user command.
     * @param commandWord Command type.
     */
    private void handleTaskCreation(String command, String commandWord) {
        if (taskCount == tasks.length) {
            System.out.println("  bot  › Task list is full.");
            return;
        }

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
     * Treats unrecognised input as a todo task, preserving the original behaviour.
     *
     * @param command User input.
     */
    private void handleUnknownCommand(String command) {
        if (taskCount == tasks.length) {
            System.out.println("  bot  › Task list is full.");
            return;
        }
//        addTask(new Todo(command));
        System.out.println("  bot  › Unknown command.");
    }

    /**
     * Adds a task to the task array and confirms the addition.
     *
     * @param task Task to add.
     */
    private void addTask(Task task) {
        tasks[taskCount] = task;
        taskCount++;
        System.out.println("  bot  › added: " + task);
    }
}
