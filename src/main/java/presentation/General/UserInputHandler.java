package presentation.General;
import java.io.Console;
import java.util.Scanner;

public class UserInputHandler {

    private final Scanner scanner;
    private final Console console;

    public UserInputHandler(Scanner scanner) {
        this.scanner = scanner;
        this.console = System.console();
    }

    //User can enter 'X' or 'x' to cancel
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

    //Read string input
    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    //Read yes/no confirmation
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

    //Read character input
    public char readChar(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        return input.isEmpty() ? ' ' : input.charAt(0);
    }

    //Read password input
    public String readPassword(String prompt) {
        System.out.print(prompt);
        
        // Use Console for password masking when available (production environment)
        if (console != null) {
            char[] passwordChars = console.readPassword();
            if (passwordChars != null) {
                String password = new String(passwordChars);
                // Clear the password from memory
                java.util.Arrays.fill(passwordChars, ' ');
                return password;
            }
        }
    
        return scanner.nextLine();
    }
}
