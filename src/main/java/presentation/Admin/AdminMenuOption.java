package presentation.Admin;

// Admin menu options using enum
public enum AdminMenuOption {
    FOOD_MANAGEMENT(1, "Food Management"),
    ORDER_REPORT(2, "Order Report"),
    BACK_MAIN_MENU(0, "Back Main Menu");

    private final int optionNumber;
    private final String displayText;

    AdminMenuOption(int optionNumber, String displayText) {
        this.optionNumber = optionNumber;
        this.displayText = displayText;
    }

    //Get the option number and return the option number
    public int getOptionNumber() {
        return optionNumber;
    }
    
    //Get the display text and return the display text
    public String getDisplayText() {
        return displayText;
    }

    //Display the admin menu using enum values
    public static void displayMenu() {
        
        String exitOption = "0.Back Main Menu";

        System.out.println("\n[]===============================[]");
        System.out.println("[]             Admin             []");
        System.out.println("[]===============================[]");

        for (AdminMenuOption option : AdminMenuOption.values()) {
            if (option.getOptionNumber() != 0) {
                String optionText = option.getOptionNumber() + "." + option.getDisplayText();
                String formattedLine = String.format("        %-25s", optionText);
                System.out.println(formattedLine);
            }
        }
        String exitLine = String.format("        %-25s", exitOption);
        System.out.println(exitLine);
        System.out.println("[]===============================[]\n");
    }

    //Get AdminMenuOption by option number and return AdminMenuOption or null if not found
    public static AdminMenuOption getByOptionNumber(int optionNumber) {
        for (AdminMenuOption option : AdminMenuOption.values()) {
            if (option.getOptionNumber() == optionNumber) {
                return option;
            }
        }
        return null;
    }
}
