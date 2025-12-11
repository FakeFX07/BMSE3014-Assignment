package presentation.Food;

import controller.FoodController;
import model.Food;
import presentation.General.UserInputHandler;

import java.util.List;

/**
 * Food Handler Class
 * Handles all food CRUD operations (Create, Read, Update, Delete)
 * Follows SOLID: Single Responsibility Principle
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
        if (!inputHandler.readYesNo("Are you sure want to add new food (Y/N) : ")) {
            System.out.println("====== Quit From Register New Food ====== \n");
            return;
        }
        
        String foodName;
        do {
            foodName = inputHandler.readString("Enter your food name: ");
            if (!foodController.validateFoodName(foodName)) {
                System.out.println("Enter letters only!\n");
            }
        } while (!foodController.validateFoodName(foodName));
        
        double foodPrice;
        do {
            foodPrice = inputHandler.readDouble("Enter your food price : RM ");
            if (!foodController.validateFoodPrice(foodPrice)) {
                System.out.println("Price are not able to be 0 or negative and not more than RM 70 !!!\n");
            }
        } while (!foodController.validateFoodPrice(foodPrice));
        
        String foodType;
        do {
            foodType = inputHandler.readString("Enter your food type (Set / A la carte) : ");
            if (!foodController.validateFoodType(foodType)) {
                System.out.println("Type can be only Set or A la carte\n");
            }
        } while (!foodController.validateFoodType(foodType));
        
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
     * Handle food editing
     */
    public void handleEditFood() {
        if (!inputHandler.readYesNo("Are you sure want to edit Y(YES) / N (No): ")) {
            System.out.println("....Quit From Edit....\n");
            return;
        }
        
        int editFoodId = inputHandler.readInt("Enter the food id that want to edit : ");
        Food food = foodController.getFoodById(editFoodId);
        
        if (food == null) {
            System.out.println("Unable to find id in the database\n");
            return;
        }
        
        System.out.println("===========================");
        System.out.println("[]        DETAILS        []");
        System.out.println("===========================");
        System.out.println("         ID : " + food.getFoodId());
        System.out.println("         Name : " + food.getFoodName());
        System.out.println("         Price : " + food.getFoodPrice());
        System.out.println("         Type : " + food.getFoodType());
        System.out.println("===========================\n");
        
        char continueEdit = 'Y';
        do {
            System.out.println("=====================");
            System.out.println("[]       EDIT      []");
            System.out.println("=====================");
            System.out.println("      1.Food Name    ");
            System.out.println("      2.Food Price   ");
            System.out.println("      3.Food Type    ");
            System.out.println("=====================");
            
            int editChoice = inputHandler.readInt("Select your choice : ");
            
            switch (editChoice) {
                case 1:
                    String foodName;
                    do {
                        foodName = inputHandler.readString("Enter food name : ");
                        if (!foodController.validateFoodName(foodName)) {
                            System.out.println("Enter letters only!\n");
                        }
                    } while (!foodController.validateFoodName(foodName));
                    food.setFoodName(foodName);
                    break;
                case 2:
                    double foodPrice;
                    do {
                        foodPrice = inputHandler.readDouble("Enter food price : ");
                        if (!foodController.validateFoodPrice(foodPrice)) {
                            System.out.println("Price are not able to be 0 or negative and not more than RM 70 !!!\n");
                        }
                    } while (!foodController.validateFoodPrice(foodPrice));
                    food.setFoodPrice(foodPrice);
                    break;
                case 3:
                    String foodType;
                    do {
                        foodType = inputHandler.readString("Enter food type (Set / A la carte): ");
                        if (!foodController.validateFoodType(foodType)) {
                            System.out.println("Type can be only Set or A la carte\n");
                        }
                    } while (!foodController.validateFoodType(foodType));
                    food.setFoodType(foodType);
                    break;
                default:
                    System.out.println("Other than 1 and 3 is invalid !!!! \n");
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
            System.out.println("====== Quit From Delete Function ======\n");
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
    }

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

