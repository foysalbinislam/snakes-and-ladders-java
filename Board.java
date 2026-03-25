import java.util.HashMap;
import java.util.Random;

public class Board {

    private int[][] board;
    private HashMap<Integer, Integer> snakes;
    private HashMap<Integer, Integer> ladders;
    private Random random;

    public Board() {
        board = new int[5][5];
        snakes = new HashMap<>();
        ladders = new HashMap<>();
        random = new Random();

        initialiseBoard();
        generateLadders();
        generateSnakes();
    }

    private void initialiseBoard() {
        int square = 1;

        for (int r = 4; r >= 0; r--) {
            if ((4 - r) % 2 == 0) {
                for (int c = 0; c < 5; c++) {
                    board[r][c] = square;
                    square++;
                }
            } else {
                for (int c = 4; c >= 0; c--) {
                    board[r][c] = square;
                    square++;
                }
            }
        }
    }

    private void generateLadders() {
        while (ladders.size() < 2) {
            int bottom = random.nextInt(20) + 2; // 2 to 21
            int top = random.nextInt(23) + 3;    // 3 to 25

            if (top > bottom
                    && !sameRow(bottom, top)
                    && !isSnakeEnd(bottom)
                    && !isSnakeEnd(top)
                    && !isLadderEnd(bottom)
                    && !isLadderEnd(top)) {

                ladders.put(bottom, top);
            }
        }
    }

    private void generateSnakes() {
        while (snakes.size() < 2) {
            int head = random.nextInt(22) + 3; // 3 to 24
            int tail = random.nextInt(20) + 2; // 2 to 21

            if (head > tail
                    && head != 25
                    && !sameRow(head, tail)
                    && !isLadderEnd(head)
                    && !isLadderEnd(tail)
                    && !isSnakeEnd(head)
                    && !isSnakeEnd(tail)) {

                snakes.put(head, tail);
            }
        }
    }

    private boolean sameRow(int a, int b) {
        int rowA = (a - 1) / 5;
        int rowB = (b - 1) / 5;
        return rowA == rowB;
    }

    private boolean isSnakeEnd(int square) {
        return snakes.containsKey(square) || snakes.containsValue(square);
    }

    private boolean isLadderEnd(int square) {
        return ladders.containsKey(square) || ladders.containsValue(square);
    }

    public int checkSnakesAndLadders(int position) {
        if (snakes.containsKey(position)) {
            System.out.println("Snake! Going down to " + snakes.get(position));
            return snakes.get(position);
        }

        if (ladders.containsKey(position)) {
            System.out.println("Ladder! Going up to " + ladders.get(position));
            return ladders.get(position);
        }

        return position;
    }

    public void displayBoardSetup() {
        System.out.println("\nGame Setup:");
        System.out.println("Snakes:");

        for (Integer head : snakes.keySet()) {
            System.out.println("  " + head + " -> " + snakes.get(head) + " (slide down)");
        }

        System.out.println("\nLadders:");

        for (Integer bottom : ladders.keySet()) {
            System.out.println("  " + bottom + " -> " + ladders.get(bottom) + " (climb up)");
        }

        System.out.println("\nBoard Layout:");

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                System.out.printf("%3d ", board[r][c]);
            }
            System.out.println();
        }
    }

    public HashMap<Integer, Integer> getSnakes() {
        return snakes;
    }

    public HashMap<Integer, Integer> getLadders() {
        return ladders;
    }
}