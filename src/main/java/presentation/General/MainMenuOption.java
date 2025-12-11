package presentation.General;

/**
 * Main Menu Option Enum
 * Represents main menu options with their display text
 * Follows OOP principles: Encapsulation
 */
public enum MainMenuOption {
    LOGIN(1, "Login"),
    REGISTER(2, "Register"),
    ADMIN(3, "Admin"),
    EXIT(4, "Exit");
    
    private final int optionNumber;
    private final String displayText;
    
    /**
     * Constructor for MainMenuOption
     * 
     * @param optionNumber The option number
     * @param displayText The display text for the option
     */
    MainMenuOption(int optionNumber, String displayText) {
        this.optionNumber = optionNumber;
        this.displayText = displayText;
    }
    
    /**
     * Get the option number
     * 
     * @return The option number
     */
    public int getOptionNumber() {
        return optionNumber;
    }
    
    /**
     * Get the display text
     * 
     * @return The display text
     */
    public String getDisplayText() {
        return displayText;
    }
    
    /**
     * Display the main menu using enum values
     */
    public static void displayMenu() {
        System.out.println("========================================");
        System.out.println("[]======JB Food Ordering System=======[]");
        System.out.println("========================================");
        
        for (MainMenuOption option : MainMenuOption.values()) {
            String line = "[]              " + option.getOptionNumber() + "." + option.getDisplayText();
            // Add spacing to match exact format
            switch (option) {
                case LOGIN:
                    line += "               []";
                    break;
                case REGISTER:
                    line += "            []";
                    break;
                case ADMIN:
                    line += "                []";
                    break;
                case EXIT:
                    line += "                []";
                    break;
            }
            System.out.println(line);
        }
        
        System.out.println("========================================");
    }
    
    /**
     * Get MainMenuOption by option number
     * 
     * @param optionNumber The option number
     * @return MainMenuOption or null if not found
     */
    public static MainMenuOption getByOptionNumber(int optionNumber) {
        for (MainMenuOption option : MainMenuOption.values()) {
            if (option.getOptionNumber() == optionNumber) {
                return option;
            }
        }
        return null;
    }
}

