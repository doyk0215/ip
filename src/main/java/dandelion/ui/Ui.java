package dandelion.ui;

import java.util.Scanner;

/**
 * Handles console input and session-level messages for Dandelion.
 */
public class Ui implements AutoCloseable {
    /** Text displayed when the application starts. */
    private static final String BANNER = """
                 .
              \\  |  /
            ――  (✻)  ――
                 |
            D A N D E L I O N

            """;

    /** Reads commands entered through the console. */
    private final Scanner scanner;

    /**
     * Creates a console user interface that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays the application welcome message.
     */
    public void showWelcomeMessage() {
        System.out.print(BANNER);
        System.out.println("  Welcome, User.");
        System.out.println("  Type anything...");
        System.out.println();
    }

    /**
     * Reads and trims the next command from the console.
     *
     * @return Trimmed command, or {@code null} when the input ends.
     */
    public String readCommand() {
        System.out.print("  you  › ");
        System.out.flush();

        if (!scanner.hasNextLine()) {
            System.out.println();
            return null;
        }
        return scanner.nextLine().trim();
    }

    /**
     * Displays the application goodbye message.
     */
    public void showGoodbyeMessage() {
        System.out.println("  bot  › Bye, User.");
    }

    /**
     * Closes the console input source.
     */
    @Override
    public void close() {
        scanner.close();
    }
}
