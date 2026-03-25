import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import swiftbot.*;

public class Game {

    private Player user;
    private Player bot;
    private Dice dice;
    private Board board;
    private Navigator navigator;
    private InputValidator validator;
    private GameLogger logger;
    private Scanner scanner;
    private SwiftBotAPI swiftBot;

    private boolean hardMode;
    private String mode;

    public Game() {
        dice = new Dice();
        board = new Board();
        navigator = new Navigator();
        validator = new InputValidator();
        logger = new GameLogger();
        scanner = new Scanner(System.in);

        try {
            swiftBot = SwiftBotAPI.INSTANCE;
        } catch (Exception e) {
            System.out.println("SwiftBot buttons unavailable. Falling back to keyboard input.");
            swiftBot = null;
        }
    }

    public void startGame() {
        displayWelcomeScreen();

        waitForStartButton();

        String name = readValidName();
        user = new Player(name);
        bot = new Player("SwiftBot");

        chooseDifficulty();
        chooseMode();

        board.displayBoardSetup();
        waitForContinueAfterBoardSetup();

        boolean userTurn = decideWhoGoesFirst();

        System.out.println("\nBoth players start at square 1.");

        boolean gameFinished = false;

        while (!gameFinished) {
            displayCurrentPositions();

            if (userTurn) {
                gameFinished = playerTurn();
            } else {
                gameFinished = botTurn();
            }

            if (!gameFinished) {
                userTurn = !userTurn;
            }
        }

        finishAndSaveGame();
    }

    private void displayWelcomeScreen() {
        System.out.println("============================");
        System.out.println("     SNAKES AND LADDERS");
        System.out.println("       vs SwiftBot");
        System.out.println("============================");
        System.out.println("Press [Y] on SwiftBot to START");
    }

    private void waitForStartButton() {
        waitForSwiftBotButton(Button.Y, "Waiting for Y button...");
    }

    private String readValidName() {
        System.out.println("\nEnter your name (1-20 letters/numbers):");
        String name = scanner.nextLine().trim();

        while (!validator.isValidName(name)) {
            System.out.println("Error: Use only letters/numbers, 1-20 characters.");
            System.out.println("Try again:");
            name = scanner.nextLine().trim();
        }

        return name;
    }

    private void chooseDifficulty() {
        System.out.println("\nSelect Difficulty:");
        System.out.println("[E] Easy");
        System.out.println("[H] Hard");
        System.out.println("Enter E or H:");

        String diff = scanner.nextLine().trim();

        while (!validator.isValidDifficulty(diff)) {
            System.out.println("Error: Enter E for Easy or H for Hard.");
            diff = scanner.nextLine().trim();
        }

        hardMode = diff.equalsIgnoreCase("H");
    }

    private void chooseMode() {
        System.out.println("\nSelect Game Mode:");
        System.out.println("A = Normal");
        System.out.println("B = Override");
        System.out.println("Enter A or B:");

        mode = scanner.nextLine().trim();

        while (!validator.isValidMode(mode)) {
            System.out.println("Error: Enter A or B.");
            mode = scanner.nextLine().trim();
        }
    }

    private void waitForContinueAfterBoardSetup() {
        waitForSwiftBotButton(Button.A, "\nPress A on SwiftBot to continue.");
    }

    private boolean decideWhoGoesFirst() {
        System.out.println("\nRolling dice to decide who starts...");

        int userRoll = dice.roll();
        int botRoll = dice.roll();

        System.out.println(user.getName() + " rolled: " + userRoll);
        System.out.println("SwiftBot rolled: " + botRoll);

        while (userRoll == botRoll) {
            System.out.println("Tie. Rolling again...");
            userRoll = dice.roll();
            botRoll = dice.roll();
            System.out.println(user.getName() + " rolled: " + userRoll);
            System.out.println("SwiftBot rolled: " + botRoll);
        }

        if (userRoll > botRoll) {
            System.out.println(user.getName() + " goes first.");
            return true;
        } else {
            System.out.println("SwiftBot goes first.");
            return false;
        }
    }

    private void displayCurrentPositions() {
        System.out.println("\nCurrent Positions:");
        System.out.println(user.getName() + ": Square " + user.getPosition());
        System.out.println("SwiftBot: Square " + bot.getPosition());
    }

    private boolean playerTurn() {
        System.out.println("\n--- Your Turn (" + getDifficultyText() + ") ---");
        System.out.println("Position: Square " + user.getPosition());

        waitForSwiftBotButton(Button.A, "Press A on SwiftBot to roll dice...");

        int roll = dice.roll();
        int oldPosition = user.getPosition();
        int attemptedPosition = oldPosition + roll;

        System.out.println("You rolled: " + roll);

        if (attemptedPosition > 25) {
            System.out.println(user.getName() + " needs an exact roll to reach square 25.");
            if (offerQuitIfAllowed()) {
                return true;
            }
            return false;
        }

        int finalPosition = board.checkSnakesAndLadders(attemptedPosition);

        System.out.println("[" + user.getName() + "] rolled a [" + roll + "] and moved from square ["
                + oldPosition + "] to [" + finalPosition + "].");

        user.setPosition(finalPosition);

        if (user.getPosition() == 25) {
            displayWinScreen(user.getName());
            return true;
        }

        if (offerQuitIfAllowed()) {
            return true;
        }

        return false;
    }

    private boolean botTurn() {
        System.out.println("\n--- SwiftBot's Turn (" + getDifficultyText() + ") ---");
        System.out.println("Position: Square " + bot.getPosition());

        int roll = dice.rollForSwiftBot(hardMode, bot.getPosition(), user.getPosition());
        int oldPosition = bot.getPosition();
        int attemptedPosition = oldPosition + roll;

        if (hardMode && (user.getPosition() - bot.getPosition()) > 5 && roll >= 4) {
            System.out.println("SwiftBot rolled: " + roll + " [BOOSTED]");
        } else {
            System.out.println("SwiftBot rolled: " + roll);
        }

        int chosenPosition;

        if (mode.equalsIgnoreCase("B")) {
            chosenPosition = readOverrideSquare(oldPosition);
            System.out.println("SwiftBot move overridden to square " + chosenPosition + ".");
        } else {
            chosenPosition = attemptedPosition;
        }

        if (chosenPosition > 25) {
            System.out.println("SwiftBot needs an exact roll to reach square 25.");
            if (offerQuitIfAllowed()) {
                return true;
            }
            return false;
        }

        int finalPosition = board.checkSnakesAndLadders(chosenPosition);

        navigator.moveSwiftBotToSquare(oldPosition, finalPosition);

        if (mode.equalsIgnoreCase("B")) {
            System.out.println("[SwiftBot] moved from square [" + oldPosition + "] to ["
                    + finalPosition + "] by override.");
        } else {
            System.out.println("[SwiftBot] rolled a [" + roll + "] and moved from square ["
                    + oldPosition + "] to [" + finalPosition + "].");
        }

        bot.setPosition(finalPosition);

        if (bot.getPosition() == 25) {
            displayWinScreen("SwiftBot");
            return true;
        }

        if (offerQuitIfAllowed()) {
            return true;
        }

        return false;
    }

    private int readOverrideSquare(int currentPosition) {
        int maxSquare = Math.min(currentPosition + 5, 25);

        System.out.println("Choose where SwiftBot moves (" + (currentPosition + 1) + "-" + maxSquare + "):");

        while (true) {
            String input = scanner.nextLine().trim();

            try {
                int square = Integer.parseInt(input);

                if (validator.isValidOverrideSquare(square, currentPosition)) {
                    return square;
                }

                System.out.println("Error: Choose a square from " + (currentPosition + 1)
                        + " to " + maxSquare + ".");
            } catch (NumberFormatException e) {
                System.out.println("Error: Enter a valid number.");
            }
        }
    }

    private boolean offerQuitIfAllowed() {
        if (user.getPosition() == 5 || bot.getPosition() == 5
                || user.getPosition() == 25 || bot.getPosition() == 25) {

            return waitForQuitButton();
        }

        return false;
    }

    private void displayWinScreen(String winnerName) {
        System.out.println("\nCONGRATULATIONS!");
        System.out.println(winnerName + " reached square 25 and WON!");
        waitForSwiftBotButton(Button.X, "Press X on SwiftBot to save and quit.");
    }

    private void finishAndSaveGame() {
        logger.saveGameLog(user, bot, board, hardMode);
        System.out.println("Game over.");
    }

    private String getDifficultyText() {
        if (hardMode) {
            return "Difficulty: HARD";
        }
        return "Difficulty: EASY";
    }

    private void waitForSwiftBotButton(Button button, String message) {
        System.out.println(message);

        if (swiftBot == null) {
            scanner.nextLine();
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);

        try {
            swiftBot.enableButton(button, () -> {
                System.out.println("Button " + button + " pressed.");
                latch.countDown();
            });

            latch.await();
            swiftBot.disableButton(button);

        } catch (Exception e) {
            System.out.println("Error: SwiftBot button input failed.");
        }
    }

    private boolean waitForQuitButton() {
        System.out.println("Press X on SwiftBot to quit, or press Enter on keyboard to continue.");

        if (swiftBot == null) {
            String input = scanner.nextLine().trim();
            return input.equalsIgnoreCase("X");
        }

        CountDownLatch xLatch = new CountDownLatch(1);

        try {
            swiftBot.enableButton(Button.X, () -> {
                System.out.println("Button X pressed.");
                xLatch.countDown();
            });

            long start = System.currentTimeMillis();

            while (System.currentTimeMillis() - start < 5000) {
                if (xLatch.getCount() == 0) {
                    swiftBot.disableButton(Button.X);
                    return true;
                }

                if (System.in.available() > 0) {
                    String input = scanner.nextLine();
                    if (input.isEmpty()) {
                        swiftBot.disableButton(Button.X);
                        return false;
                    }
                }

                Thread.sleep(100);
            }

            swiftBot.disableButton(Button.X);
            return false;

        } catch (Exception e) {
            System.out.println("Error: SwiftBot quit input failed.");
            return false;
        }
    }
}