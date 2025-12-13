package controller;

import java.util.List;
import java.util.Optional;

import model.Food;
import service.interfaces.IFoodService;

public class FoodController {
    
    private final IFoodService foodService;
    
    public FoodController() {
        this.foodService = new service.impl.FoodService(new repository.impl.FoodRepository());
    }
    
    //Constructor
    public FoodController(IFoodService foodService) {
        this.foodService = foodService;
    }

    //Functional interface for food operations
    @FunctionalInterface
    private interface FoodOperation {
        Food execute() throws IllegalArgumentException;
    }
    
    //Register a new food item
    public Food registerFood(Food food) {
        return executeFoodOperation(() -> foodService.registerFood(food), "Registration");
    }
    
    //Update an existing food item
    public Food updateFood(Food food) {
        return executeFoodOperation(() -> foodService.updateFood(food), "Update");
    }

    //Execute food operation
    private Food executeFoodOperation(FoodOperation operation, String operationName) {
        try {
            return operation.execute();
        } catch (IllegalArgumentException e) {
            System.out.println(operationName + " failed: " + e.getMessage());
            return null;
        }
    }
    
    //Delete a food ite
    public boolean deleteFood(int foodId) {
        boolean deleted = foodService.deleteFood(foodId);
        if (!deleted) {
            System.out.println("Food with ID " + foodId + " not found or could not be deleted");
        }
        return deleted;
    }
    
    //Get all food items
    public List<Food> getAllFoods() {
        return foodService.getAllFoods();
    }
    
    //Get food by ID
    public Food getFoodById(int foodId) {
        Optional<Food> foodOpt = foodService.getFoodById(foodId);
        return foodOpt.orElse(null);
    }
    
    //Get food by name
    public Food getFoodByName(String foodName) {
        Optional<Food> foodOpt = foodService.getFoodByName(foodName);
        return foodOpt.orElse(null);
    }
    
    //Validate food name
    public boolean validateFoodName(String foodName) {
        return foodService.validateFoodName(foodName);
    }
    
    //Validate food price
    public boolean validateFoodPrice(double foodPrice) {
        return foodService.validateFoodPrice(foodPrice);
    }
    
    //Validate food type
    public boolean validateFoodType(String foodType) {
        return foodService.validateFoodType(foodType);
    }
    
    //Check if food name is unique (case-insensitive)
    public boolean isFoodNameUnique(String foodName) {
        return foodService.isFoodNameUnique(foodName);
    }
}
