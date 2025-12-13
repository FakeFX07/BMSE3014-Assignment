package presentation.General;

public enum MainMenuOption {
    LOGIN(1, "Login"),
    REGISTER(2, "Register"),
    ADMIN(3, "Admin"),
    EXIT(4, "Exit");
    
    private final int optionNumber;
    private final String displayText;
    
    MainMenuOption(int optionNumber, String displayText) {
        this.optionNumber = optionNumber;
        this.displayText = displayText;
    }
    
    public int getOptionNumber() {
        return optionNumber;
    }
    
    public String getDisplayText() {
        return displayText;
    }
    
    //Display main menu
    public static void displayMenu() {
        System.out.println("[]=====================================[]");
        System.out.println("[]       JB Food Ordering System       []");
        System.out.println("[]=====================================[]");
        
        for (MainMenuOption option : MainMenuOption.values()) {
            System.out.printf("                %d.%s%n", option.getOptionNumber(), option.getDisplayText());
        }
        
        System.out.println("[]=====================================[]");
    }
    
    //Get MainMenuOption
    public static MainMenuOption getByOptionNumber(int optionNumber) {
        for (MainMenuOption option : MainMenuOption.values()) {
            if (option.getOptionNumber() == optionNumber) {
                return option;
            }
        }
        return null;
    }
    
    // Alias for previous usage
    public static MainMenuOption fromCode(int code) {
        return getByOptionNumber(code);
    }
}

