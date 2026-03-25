import swiftbot.SwiftBotAPI;

public class Navigator {

    private SwiftBotAPI swiftBot;

    public Navigator() {
        try {
            swiftBot = SwiftBotAPI.INSTANCE;
        } catch (Exception e) {
            System.out.println("Error: SwiftBot could not be initialised.");
            swiftBot = null;
        }
    }

    public void moveSwiftBotToSquare(int fromSquare, int toSquare) {
        System.out.println("[SwiftBot moving from " + fromSquare + " to " + toSquare + "]");

        if (swiftBot == null) {
            System.out.println("SwiftBot not available. Running in simulation mode.");
            return;
        }

        int current = fromSquare;

        try {
            if (current < toSquare) {
                while (current < toSquare) {
                    if (current == 5 || current == 15) {
                        turnLeft();
                        moveOneSquare();
                        turnLeft();
                    } else if (current == 10 || current == 20) {
                        turnRight();
                        moveOneSquare();
                        turnRight();
                    } else {
                        moveOneSquare();
                    }

                    current++;
                    Thread.sleep(300);
                }
            } else if (current > toSquare) {
                while (current > toSquare) {
                    if (current == 6 || current == 16) {
                        turnLeft();
                        moveOneSquare();
                        turnLeft();
                    } else if (current == 11 || current == 21) {
                        turnRight();
                        moveOneSquare();
                        turnRight();
                    } else {
                        moveOneSquareBackward();
                    }

                    current--;
                    Thread.sleep(300);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: SwiftBot movement failed.");
        }
    }

    private void moveOneSquare() throws Exception {
        swiftBot.move(50, 50, 1100);
    }

    private void moveOneSquareBackward() throws Exception {
        swiftBot.move(-50, -50, 1100);
    }

    private void turnLeft() throws Exception {
        swiftBot.move(-40, 40, 635);
        Thread.sleep(200);
    }

    private void turnRight() throws Exception {
        swiftBot.move(40, -40, 550);
        Thread.sleep(200);
    }
}