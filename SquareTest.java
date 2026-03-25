import swiftbot.SwiftBotAPI;

public class SquareTest {
    public static void main(String[] args) {
        System.out.println("Program started.");

        try {
            SwiftBotAPI bot = SwiftBotAPI.INSTANCE;
            System.out.println("SwiftBot connected.");
            System.out.println("Moving one square...");
            bot.move(50, 50, 1100);
            System.out.println("Done.");
        } catch (Exception e) {
            System.out.println("Error happened:");
            e.printStackTrace();
        }
    }
}