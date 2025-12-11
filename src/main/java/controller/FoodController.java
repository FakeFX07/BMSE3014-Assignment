package controller;

import java.util.List;
import java.util.Optional;

import model.Food;
import service.interfaces.IFoodService;

/**
 * Food Controller
 * Handles food-related user interactions
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle
 */
public class FoodController {
    
    private final IFoodService foodService;
    
    /**
     * Default constructor - handles dependency injection internally
     * Follows same pattern as CustomerController and AdminController
     */
    public FoodController() {
        this.foodService = new service.impl.FoodService(new repository.impl.FoodRepository());
    }
    
    /**
     * Constructor with dependency injection (for testing)
     * 
     * @param foodService Food service implementation (injected)
     */
    public FoodController(IFoodService foodService) {
        this.foodService = foodService;
    }
    
    /**
     * Register a new food item
     * 
     * @param food Food to register
     * @return Registered food if successful, null if validation fails
     */
    public Food registerFood(Food food) {
        return executeFoodOperation(() -> foodService.registerFood(food), "Registration");
    }
    
    /**
     * Update an existing food item
     * 
     * @param food Food to update
     * @return Updated food if successful, null if validation fails
     */
    public Food updateFood(Food food) {
        return executeFoodOperation(() -> foodService.updateFood(food), "Update");
    }

    /**
     * Execute food operation with error handling
     * 
     * @param operation Food operation to execute
     * @param operationName Name of the operation for error messages
     * @return Result of operation or null if failed
     */
    private Food executeFoodOperation(FoodOperation operation, String operationName) {
        try {
            return operation.execute();
        } catch (IllegalArgumentException e) {
            System.out.println(operationName + " failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Functional interface for food operations
     */
    @FunctionalInterface
    private interface FoodOperation {
        Food execute() throws IllegalArgumentException;
    }
    
    /**
     * Delete a food item
     * 
     * @param foodId Food ID to delete
     * @return true if deleted successfully
     */
    public boolean deleteFood(int foodId) {
        boolean deleted = foodService.deleteFood(foodId);
        if (!deleted) {
            System.out.println("Food with ID " + foodId + " not found or could not be deleted");
        }
        return deleted;
    }
    
    /**
     * Get all food items
     * 
     * @return List of all foods
     */
    public List<Food> getAllFoods() {
        return foodService.getAllFoods();
    }
    
    /**
     * Get food by ID
     * 
     * @param foodId Food ID
     * @return Food if found, null otherwise
     */
    public Food getFoodById(int foodId) {
        Optional<Food> foodOpt = foodService.getFoodById(foodId);
        return foodOpt.orElse(null);
    }
    
    /**
     * Get food by name (case-insensitive)
     * 
     * @param foodName Food name
     * @return Food if found, null otherwise
     */
    public Food getFoodByName(String foodName) {
        Optional<Food> foodOpt = foodService.getFoodByName(foodName);
        return foodOpt.orElse(null);
    }
    
    /**
     * Validate food name
     * 
     * @param foodName Food name to validate
     * @return true if valid
     */
    public boolean validateFoodName(String foodName) {
        return foodService.validateFoodName(foodName);
    }
    
    /**
     * Validate food price
     * 
     * @param foodPrice Food price to validate
     * @return true if valid
     */
    public boolean validateFoodPrice(double foodPrice) {
        return foodService.validateFoodPrice(foodPrice);
    }
    
    /**
     * Validate food type
     * 
     * @param foodType Food type to validate
     * @return true if valid
     */
    public boolean validateFoodType(String foodType) {
        return foodService.validateFoodType(foodType);
    }
    
    /**
     * Check if food name is unique (case-insensitive)
     * 
     * @param foodName Food name to check
     * @return true if unique, false if already exists
     */
    public boolean isFoodNameUnique(String foodName) {
        return foodService.isFoodNameUnique(foodName);
    }
}
