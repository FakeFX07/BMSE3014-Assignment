package presentation.Food;

import controller.FoodController;
import model.Food;
import presentation.General.UserInputHandler;

import java.util.List;

/**
 * Food Handler Class
 * Handles all food CRUD operations (Create, Read, Update, Delete)
 */
public class FoodHandler {
    
    private final FoodController foodController;
    private final UserInputHandler inputHandler;
    
    /**
     * Constructor
     * 
     * @param foodController Food controller instance
     * @param inputHandler User input handler instance
     */
    public FoodHandler(FoodController foodController, UserInputHandler inputHandler) {
        this.foodController = foodController;
        this.inputHandler = inputHandler;
    }
    
    /**
     * Handle food registration
     */
    public void handleRegisterFood() {
        
        System.out.println("\n=== Register New Food ===");

        String foodName = readValidFoodName("Enter your food name: ");
        double foodPrice = readValidFoodPrice("Enter your food price : RM ");
        String foodType = readValidFoodType("Enter your food type (S=Set / A=A la carte) : ");
        
        if (inputHandler.readYesNo("Are you want to proceed to add (Y/N) : ")) {
            Food food = new Food(foodName, foodPrice, foodType);
            Food registeredFood = foodController.registerFood(food);
            if (registeredFood != null) {
                printFoodDetails(registeredFood);
            }
        } else {
            System.out.println("You have cancel registered !");
        }
    }

    /**
     * Read and validate food name from user input
     * Validates format (letters only) and checks for duplicates
     * 
     * @param prompt The prompt message to display
     * @return Valid and unique food name
     */
    private String readValidFoodName(String prompt) {
        String foodName;
        do {
            foodName = inputHandler.readString(prompt);
            if (!foodController.validateFoodName(foodName)) {
                System.out.println("Enter letters only!\n");
            } else if (!foodController.isFoodNameUnique(foodName)) {
                System.out.println("Food name already exists! Please enter a different name.\n");
            }
        } while (!foodController.validateFoodName(foodName) || !foodController.isFoodNameUnique(foodName));
        return foodName;
    }

    /**
     * Read and validate food price from user input
     * Ensures price is positive and greater than zero
     * 
     * @param prompt The prompt message to display
     * @return Valid food price
     */
    private double readValidFoodPrice(String prompt) {
        double foodPrice;
        do {
            foodPrice = inputHandler.readDouble(prompt);
            if (!foodController.validateFoodPrice(foodPrice)) {
                System.out.println("Price are not able to be 0 or negative !!!\n");
            }
        } while (!foodController.validateFoodPrice(foodPrice));
        return foodPrice;
    }

    /**
     * Read and validate food type from user input
     * Accepts shortcuts: S for Set, A for A la carte
     * 
     * @param prompt The prompt message to display
     * @return Valid food type (Set or A la carte)
     */
    private String readValidFoodType(String prompt) {
        String foodType;
        String convertedType;
        do {
            foodType = inputHandler.readString(prompt);
            convertedType = convertFoodType(foodType);
            if (!foodController.validateFoodType(convertedType)) {
                System.out.println("Type can be only S (Set) or A (A la carte)\n");
            }
        } while (!foodController.validateFoodType(convertedType));
        return convertedType;
    }

    /**
     * Convert food type shortcuts to full names
     * S or s -> Set
     * A or a -> A la carte
     * 
     * @param input User input string
     * @return Converted food type or original input
     */
    private String convertFoodType(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        String trimmed = input.trim();
        if (trimmed.equalsIgnoreCase("S")) {
            return "Set";
        } else if (trimmed.equalsIgnoreCase("A")) {
            return "A la carte";
        }
        return input;
    }
    
    /**
     * Handle food editing
     */
    public void handleEditFood() {
        if (!inputHandler.readYesNo("Are you sure want to edit Y(YES) / N (No): ")) {
            System.out.println("Quit From Edit !!!\n");
            return;
        }
        
        int editFoodId = inputHandler.readInt("Enter the food id that want to edit : ");
        Food food = foodController.getFoodById(editFoodId);
        
        if (food == null) {
            System.out.println("Unable to find id in the database\n");
            return;
        }
        
        printFoodDetails(food);
        
        char continueEdit = 'Y';
        do {
            printEditMenu();
            int editChoice = inputHandler.readInt("Select your choice : ");
            FoodEditOption option = FoodEditOption.fromCode(editChoice);

            if (option == null) {
                System.out.println("Other than 1, 2 and 3 is invalid !!!! \n");
            } else {
                switch (option) {
                    case NAME:
                        food.setFoodName(readValidFoodName("Enter food name : "));
                        break;
                    case PRICE:
                        food.setFoodPrice(readValidFoodPrice("Enter food price : "));
                        break;
                    case TYPE:
                        food.setFoodType(readValidFoodType("Enter food type (S=Set / A=A la carte): "));
                        break;
                    default:
                        System.out.println("Other than 1, 2 and 3 is invalid !!!! \n");
                }
            }

            continueEdit = inputHandler.readChar("Do you want to continue edit id: " + food.getFoodId() + "( Y / N ) : ");
        } while (continueEdit == 'Y' || continueEdit == 'y');
        
        Food updatedFood = foodController.updateFood(food);
        if (updatedFood != null) {
            System.out.println("File updated successfully");
        }
    }
    
    /**
     * Handle food deletion
     */
    public void handleDeleteFood() {
        if (!inputHandler.readYesNo("Do you want to delete a food (Y / N ) : ")) {
            System.out.println("Quit From Delete Function !!!n");
            return;
        }
        
        int deleteFoodId = inputHandler.readInt("Enter the food id want to delete : ");
        
        if (inputHandler.readYesNo("Are you sure want to delete id : " + deleteFoodId + " (Y/N) :")) {
            if (foodController.deleteFood(deleteFoodId)) {
                System.out.println("Food deleted successfully");
            }
        } else {
            System.out.println("==== You have cancel to delete id :" + deleteFoodId + " !!!!! ===\n");
        }
    }
    
    /**
     * Handle display all foods
     */
    public void handleDisplayAllFoods() {
        List<Food> foods = foodController.getAllFoods();
        MenuDisplay.displayAllFoods(foods);
        waitForExit();
    }

    /**
     * Wait for user to press X to exit
     */
    private void waitForExit() {
        char input;
        do {
            input = inputHandler.readChar("Press X to go back: ");
        } while (input != 'X' && input != 'x');
    }

    /**
     * Display the food edit menu options
     */
    private void printEditMenu() {
        System.out.println("=====================");
        System.out.println("[]       EDIT      []");
        System.out.println("=====================");
        for (FoodEditOption option : FoodEditOption.values()) {
            System.out.printf("      %d.%s%n", option.getCode(), option.getLabel());
        }
        System.out.println("=====================");
    }

    /**
     * Display food details in formatted view
     * 
     * @param food The food object to display
     */
    private void printFoodDetails(Food food) {
        System.out.println("\n======================\n");
        System.out.println("[] Food Details []\n");
        System.out.println("======================\n");
        System.out.println("Id :" + food.getFoodId() + "\n");
        System.out.println("Name : " + food.getFoodName() + "\n");
        System.out.println("Food Price : " + food.getFoodPrice() + "\n");
        System.out.println("Food type : " + food.getFoodType() + "\n");
        System.out.println("======================\n");
    }
}

