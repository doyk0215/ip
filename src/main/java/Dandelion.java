import java.util.Scanner;

/**
 * Runs the Dandelion chatbot's command loop.
 */
public class Dandelion {
    /** Maximum number of tasks that can be stored during one session. */
    private static final int MAX_TASKS = 100;

    /**
     * Stores tasks, displays them on request, and runs until the user enters {@code bye}.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        String banner = """
                     .
                  \\  |  /
                ――  (✻)  ――
                     |
                D A N D E L I O N
                
                """;
        String[] tasks = new String[MAX_TASKS];
        boolean[] isDone = new boolean[MAX_TASKS];
        int taskCount = 0;

        System.out.print(banner);
        System.out.println("  Welcome, User.");
        System.out.println("  Type anything...");
        System.out.println();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("  you  › ");
                System.out.flush();

                if (!scanner.hasNextLine()) {
                    System.out.println();
                    break;
                }

                String command = scanner.nextLine();

                if (command.equals("bye")) {
                    System.out.println("  bot  › Bye, User.");
                    break;
                }

                if (command.equals("list")) {
                    if (taskCount == 0) {
                        System.out.println("  bot  › No tasks added yet.");
                    } else {
                        for (int i = 0; i < taskCount; i++) {
                            String linePrefix = i == 0 ? "  bot  › " : "         ";
                            String status = isDone[i] ? "[X]" : "[ ]";
                            System.out.println(linePrefix + (i + 1) + "." + status + " " + tasks[i]);
                        }
                    }
                    System.out.println();
                    continue;
                }

                if (command.equals("mark") || command.startsWith("mark ")) {
                    if (taskCount == 0) {
                        System.out.println("  bot  › No tasks added yet.");
                        System.out.println();
                        continue;
                    }

                    String taskNumberText = command.substring("mark".length()).trim();
                    try {
                        int taskNumber = Integer.parseInt(taskNumberText);
                        if (taskNumber < 1 || taskNumber > taskCount) {
                            System.out.println("  bot  › Please enter a task number from 1 to " + taskCount + ".");
                        } else {
                            int taskIndex = taskNumber - 1;
                            isDone[taskIndex] = true;
                            System.out.println("  bot  › Nice! I've marked this task as done:");
                            System.out.println("           [X] " + tasks[taskIndex]);
                        }
                    } catch (NumberFormatException exception) {
                        System.out.println("  bot  › Please enter a task number after mark.");
                    }
                    System.out.println();
                    continue;
                }

                if (command.equals("unmark") || command.startsWith("unmark ")) {
                    if (taskCount == 0) {
                        System.out.println("  bot  › No tasks added yet.");
                        System.out.println();
                        continue;
                    }

                    String taskNumberText = command.substring("unmark".length()).trim();
                    try {
                        int taskNumber = Integer.parseInt(taskNumberText);
                        if (taskNumber < 1 || taskNumber > taskCount) {
                            System.out.println("  bot  › Please enter a task number from 1 to " + taskCount + ".");
                        } else {
                            int taskIndex = taskNumber - 1;
                            isDone[taskIndex] = false;
                            System.out.println("  bot  › OK, I've marked this task as not done yet:");
                            System.out.println("           [ ] " + tasks[taskIndex]);
                        }
                    } catch (NumberFormatException exception) {
                        System.out.println("  bot  › Please enter a task number after unmark.");
                    }
                    System.out.println();
                    continue;
                }

                if (taskCount == tasks.length) {
                    System.out.println("  bot  › Task list is full.");
                } else {
                    tasks[taskCount] = command;
                    taskCount++;
                    System.out.println("  bot  › added: " + command);
                }
                System.out.println();
            }
        }
    }
}
