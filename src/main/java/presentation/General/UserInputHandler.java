package presentation.General;
import java.util.Scanner;

/**
 * User Input Handler
 * Handles user input validation and reading
 * Follows SOLID: Single Responsibility Principle, DRY principle
 */
public class UserInputHandler {

    private final Scanner scanner;

    public UserInputHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Read integer input with validation
     * User can enter 'X' or 'x' to cancel
     * 
     * @param prompt Prompt message
     * @return Integer value
     * @throws UserCancelledException if user enters X to cancel
     */
    public int readInt(String prompt) {
    while (true) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim(); 

        if (input.equalsIgnoreCase("X")) {
            throw new UserCancelledException();
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number or X to cancel.");
        }
    }
}

    /**
     * Read double input with validation
     * User can enter 'X' or 'x' to cancel
     * 
     * @param prompt Prompt message
     * @return Double value
     * @throws UserCancelledException if user enters X to cancel
     */
    public double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("X")) {
                    throw new UserCancelledException();
                }

                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number or X to cancel.");
            }
        }
    }

    /**
     * Read string input
     * 
     * @param prompt Prompt message
     * @return String value
     */
    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Read yes/no confirmation
     * 
     * @param prompt Prompt message
     * @return true if Yes, false if No
     */
    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            if ("Y".equals(input) || "YES".equals(input)) {
                return true;
            } else if ("N".equals(input) || "NO".equals(input)) {
                return false;
            } else {
                System.out.println("Please enter Y (Yes) or N (No) only.");
            }
        }
    }

    /**
     * Read character input
     * 
     * @param prompt Prompt message
     * @return Character value
     */
    public char readChar(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        return input.isEmpty() ? ' ' : input.charAt(0);
    }
}
