/**
 * Starts the Dandelion chatbot by showing its greeting and farewell messages.
 */
public class Dandelion {
    /**
     * Displays the chatbot's initial message and then exits.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String banner = " ____    _    _   _ ____  _____ _     ___ ___  _   _\n"
                + "|  _ \\  / \\  | \\ | |  _ \\| ____| |   |_ _/ _ \\| \\ | |\n"
                + "| | | |/ _ \\ |  \\| | | | |  _| | |    | | | | |  \\| |\n"
                + "| |_| / ___ \\| |\\  | |_| | |___| |___ | | |_| | |\\  |\n"
                + "|____/_/   \\_\\_| \\_|____/|_____|_____|___\\___/|_| \\_|\n";
        String divider = "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ";

        System.out.println(divider);
        System.out.print(banner);
        System.out.println("Welcome, User");
//        System.out.println("What can I do for you?");
        System.out.println(divider);
        System.out.println("Thank you.");
        System.out.println(divider);
    }
}
