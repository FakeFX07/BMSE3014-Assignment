package presentation.Payment;

/**
 * Payment Option Enum
 * Represents payment method options with their display text
 * Follows OOP principles: Encapsulation
 */
public enum PaymentOption {
    TNG(1, "TNG"),
    GRAB(2, "Grab"),
    BANK(3, "Bank");
    
    private final int optionNumber;
    private final String displayText;
    
    /**
     * Constructor for PaymentOption
     * 
     * @param optionNumber The option number
     * @param displayText The display text for the option
     */
    PaymentOption(int optionNumber, String displayText) {
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
     * Display the payment options menu using enum values
     */
    public static void displayMenu() {
        System.out.println("===================");
        System.out.println("      Payment      ");
        System.out.println("===================");
        
        // Build the payment options line
        StringBuilder paymentLine = new StringBuilder("");
        for (int i = 0; i < PaymentOption.values().length; i++) {
            PaymentOption option = PaymentOption.values()[i];
            paymentLine.append(option.getOptionNumber()).append(".").append(option.getDisplayText());
            if (i < PaymentOption.values().length - 1) {
                paymentLine.append(" ");
            }
        }
        System.out.println(paymentLine.toString());
        
        System.out.println("===================");
    }
    
    /**
     * Get PaymentOption by option number
     * 
     * @param optionNumber The option number
     * @return PaymentOption or null if not found
     */
    public static PaymentOption getByOptionNumber(int optionNumber) {
        for (PaymentOption option : PaymentOption.values()) {
            if (option.getOptionNumber() == optionNumber) {
                return option;
            }
        }
        return null;
    }
}

