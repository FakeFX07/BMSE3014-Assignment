package controller;

import java.util.List;
import java.util.Optional;

import model.Food;
import repository.impl.FoodRepository;
import service.impl.FoodService;
import service.interfaces.IFoodService;

/**
 * Food Controller
 * Handles food-related user interactions
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle
 */
public class FoodController {
    
    private final IFoodService foodService;
    
    public FoodController(IFoodService foodService) {
        this.foodService = foodService;
    }
    
    // Default constructor wiring service and repository
    public FoodController() {
        this(new FoodService(new FoodRepository()));
    }
    
    /**
     * Register a new food item
     * 
     * @param food Food to register
     * @return Registered food if successful, null if validation fails
     */
    public Food registerFood(Food food) {
        try {
            return foodService.registerFood(food);
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Update an existing food item
     * 
     * @param food Food to update
     * @return Updated food if successful, null if validation fails
     */
    public Food updateFood(Food food) {
        try {
            return foodService.updateFood(food);
        } catch (IllegalArgumentException e) {
            System.out.println("Update failed: " + e.getMessage());
            return null;
        }
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
