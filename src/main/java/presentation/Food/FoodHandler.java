package presentation.Food;

import model.Food;
import controller.FoodController;
import presentation.General.UserInputHandler;
import presentation.General.UserCancelledException;
import java.util.List;

public class FoodHandler {
    
    private final FoodController foodController;
    private final UserInputHandler inputHandler;
    
    public FoodHandler(FoodController foodController, UserInputHandler inputHandler) {
        this.foodController = foodController;
        this.inputHandler = inputHandler;
    }
    
    //Handle food registration
    public void handleRegisterFood() {
        try {
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
        } catch (UserCancelledException e) {
            System.out.println("\nOperation cancelled by user.\n");
        }
    }
    
    //Handle food editing
    public void handleEditFood() {
        try {
            Food food = null;
            System.out.println("\n[]================ Edit Food ================[]");     
            do {
                food = searchFoodByIdOrName("Enter the food id or name to edit (or X to cancel): ");
                
                if (food == null) {
                    System.out.println("\nUnable to find food in the database. Please try again.\n");
                }
            } while (food == null);
            
            printFoodDetails(food);
        
        boolean continueEdit = true;
        do {
            printEditMenu();
            int editChoice = inputHandler.readInt("Select your choice (or X to exit): ");
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
                }
                
                // Update DB immediately after edit
                Food updatedFood = foodController.updateFood(food);
                if (updatedFood != null) {
                    System.out.println("\nFood updated successfully!\n");
                    System.out.println("Updated Food Details:");
                    printFoodDetails(updatedFood);
                    food = updatedFood; 
                }
            }

            String continueChoice = inputHandler.readString("Want to edit anything else? (Press Enter to continue or X to exit): ");
            if (continueChoice.equalsIgnoreCase("X")) {
                continueEdit = false;
            }

            System.out.println();

        } while (continueEdit);
        } catch (UserCancelledException e) {
            System.out.println("\nOperation cancelled by user.\n");
        }
    }
    
    //Handle food deletion
    public void handleDeleteFood() {
        try {
            System.out.println("\n[]========= Delete Food =========[]");
            
            Food food = null;
            
            do {
                food = searchFoodByIdOrName("Enter the food id or name to delete (or X to cancel): ");
                
                if (food == null) {
                    System.out.println("\nUnable to find food in the database. Please try again.\n");
                }
            } while (food == null);
            
            printFoodDetails(food);
            
            if (inputHandler.readYesNo("Are you sure want to delete id : " + food.getFoodId() + " (Y/N) : ")) {
                if (foodController.deleteFood(food.getFoodId())) {
                    System.out.println("Food deleted successfully");
                }
            } else {
                System.out.println("==== You have cancel to delete id :" + food.getFoodId() + " !!!!! ===\n");
            }
        } catch (UserCancelledException e) {
            System.out.println("\nOperation cancelled by user.\n");
        }
    }
    
    //Handle display all foods
    public void handleDisplayAllFoods() {
        List<Food> foods = foodController.getAllFoods();
        MenuDisplay.displayAllFoods(foods);
        waitForExit();
    }

     //Read and validate food name from user input
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

    //Read and validate food price from user input & ensures price is positive and greater than zero
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

    /*
     * Read and validate food type from user input
     * Accepts shortcuts: S for Set, A for A la carte
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

    /*
     * Convert food type shortcuts to full names
     * S or s -> Set
     * A or a -> A la carte
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

    private void waitForExit() {
        char input;
        do {
            input = inputHandler.readChar("Press X to go back: ");
        } while (input != 'X' && input != 'x');
    }

    //Display edit menu
    private void printEditMenu() {
        System.out.println("[]======================[]");
        System.out.println("[]         EDIT         []");
        System.out.println("[]======================[]");
        for (FoodEditOption option : FoodEditOption.values()) {
            System.out.printf("      %d.%s%n", option.getCode(), option.getLabel());
        }
        System.out.println("[]======================[]");
    }

    //Display food details
    private void printFoodDetails(Food food) {
        System.out.println();
        System.out.println("[]========================================[]");
        System.out.println("[]              Food Details              []");
        System.out.println("[]========================================[]");
        System.out.println("    Id         : " + food.getFoodId() + "");
        System.out.println("    Name       : " + food.getFoodName() + "");
        System.out.println("    Food Price : " + food.getFoodPrice() + "");
        System.out.println("    Food type  : " + food.getFoodType() + "");
        System.out.println("[]========================================[]\n");
    }

    //Search food by ID or name
    private Food searchFoodByIdOrName(String prompt) throws UserCancelledException {
        String input = inputHandler.readString(prompt);
        
        if (input.equalsIgnoreCase("X")) {
            throw new UserCancelledException();
        }
        
        try {
            int foodId = Integer.parseInt(input.trim());
            return foodController.getFoodById(foodId);
        } catch (NumberFormatException e) {
            return foodController.getFoodByName(input.trim());
        }
    }
}

