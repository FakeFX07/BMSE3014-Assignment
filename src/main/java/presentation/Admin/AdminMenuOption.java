package presentation.Admin;

/**
 * Admin Menu Option Enum
 * Represents admin menu options with their display text
 * Follows OOP principles: Encapsulation
 */
public enum AdminMenuOption {
    REGISTER_FOOD(1, "Register New Food"),
    EDIT_FOOD(2, "Edit Food"),
    DELETE_FOOD(3, "Delete Food"),
    VIEW_ALL_FOOD(4, "View All Food"),
    ORDER_REPORT(5, "Order Report"),
    BACK_MAIN_MENU(0, "Back Main Menu");
    
    private final int optionNumber;
    private final String displayText;
    
    /**
     * Constructor for AdminMenuOption
     * 
     * @param optionNumber The option number
     * @param displayText The display text for the option
     */
    AdminMenuOption(int optionNumber, String displayText) {
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
     * Display the admin menu using enum values
     */
    public static void displayMenu() {
        System.out.println("\n[]===============================[]");
        System.out.println("[]             Admin             []");
        System.out.println("[]===============================[]");
        
        // Display options in order: 1-5, then 0
        // Fixed width: Each line must be exactly 35 chars to match border
        // Border: []===============================[] = 35 chars
        // Format: []        X. Text                [] = 35 chars
        for (AdminMenuOption option : AdminMenuOption.values()) {
            if (option.getOptionNumber() != 0) {
                // Build option text: "1.Register New Food"
                String optionText = option.getOptionNumber() + "." + option.getDisplayText();
                // Format: [] (2) + 8 spaces + text (max 25) + [] (2) = 35 total
                String formattedLine = String.format("[]        %-25s[]", optionText);
                System.out.println(formattedLine);
            }
        }
        
        System.out.println("[]===============================[]");
        // Display option 0 last with proper spacing - same format
        String exitOption = "0.Back Main Menu";
        String exitLine = String.format("[]        %-25s[]", exitOption);
        System.out.println(exitLine);
        System.out.println("[]==============================[]\n");
    }
    
    /**
     * Get AdminMenuOption by option number
     * 
     * @param optionNumber The option number
     * @return AdminMenuOption or null if not found
     */
    public static AdminMenuOption getByOptionNumber(int optionNumber) {
        for (AdminMenuOption option : AdminMenuOption.values()) {
            if (option.getOptionNumber() == optionNumber) {
                return option;
            }
        }
        return null;
    }
}

