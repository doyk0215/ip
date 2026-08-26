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
                            System.out.println(linePrefix + (i + 1) + ". " + tasks[i]);
                        }
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
