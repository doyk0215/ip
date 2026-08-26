import java.util.Scanner;

/**
 * Runs the Dandelion chatbot's command loop.
 */
public class Dandelion {
    /**
     * Echoes commands entered by the user until the user enters {@code bye}.
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

                System.out.println("  bot  › " + command);
                System.out.println();
            }
        }
    }
}
