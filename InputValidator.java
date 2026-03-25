public class InputValidator {

    public boolean isValidName(String name) {
        return name != null && name.matches("[A-Za-z0-9]{1,20}");
    }

    public boolean isValidDifficulty(String input) {
        return input != null && (input.equalsIgnoreCase("E") || input.equalsIgnoreCase("H"));
    }

    public boolean isValidMode(String input) {
        return input != null && (input.equalsIgnoreCase("A") || input.equalsIgnoreCase("B"));
    }

    public boolean isValidStartChoice(String input) {
        return input != null && (input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("X"));
    }

    public boolean isValidRollChoice(String input) {
        return input != null && input.equalsIgnoreCase("A");
    }

    public boolean isValidOverrideSquare(int chosen, int current) {
        int min = current + 1;
        int max = current + 5;
        return chosen >= min && chosen <= max && chosen <= 25;
    }
}