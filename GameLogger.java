import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class GameLogger {

    public void saveGameLog(Player user, Player bot, Board board, boolean hardMode) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");
        String timestamp = LocalDateTime.now().format(formatter);
        String fileName = "game_log_" + timestamp + ".txt";

        try (FileWriter writer = new FileWriter(fileName)) {

            writer.write("Snakes and Ladders Game Log\n");
            writer.write("===========================\n");
            writer.write("Date and Time: " + timestamp + "\n\n");

            writer.write("Difficulty: " + (hardMode ? "HARD" : "EASY") + "\n\n");

            writer.write("Final Positions:\n");
            writer.write(user.getName() + ": Square " + user.getPosition() + "\n");
            writer.write("SwiftBot: Square " + bot.getPosition() + "\n\n");

            writer.write("Snakes:\n");
            for (Map.Entry<Integer, Integer> entry : board.getSnakes().entrySet()) {
                writer.write(entry.getKey() + " -> " + entry.getValue() + "\n");
            }

            writer.write("\nLadders:\n");
            for (Map.Entry<Integer, Integer> entry : board.getLadders().entrySet()) {
                writer.write(entry.getKey() + " -> " + entry.getValue() + "\n");
            }

            System.out.println("\nSaving game log...");
            System.out.println("Difficulty: " + (hardMode ? "HARD" : "EASY") + " saved in log.");
            System.out.println("Saved to: " + fileName);
            System.out.println("Thank you for playing!");

        } catch (IOException e) {
            System.out.println("Error: Could not save log file.");
        }
    }
}