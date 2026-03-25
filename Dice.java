import java.util.Random;

public class Dice {

    private Random random;

    public Dice() {
        random = new Random();
    }

    public int roll() {
        return random.nextInt(6) + 1;
    }

    public int rollForSwiftBot(boolean hardMode, int botPos, int playerPos) {

        if (hardMode && (playerPos - botPos) > 5) {

            int chance = random.nextInt(100) + 1;

            if (chance <= 70) {
                return random.nextInt(3) + 4;
            }

        }

        return roll();
    }

}