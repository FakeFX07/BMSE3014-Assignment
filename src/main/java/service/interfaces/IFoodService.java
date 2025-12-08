package service.interfaces;

import java.util.List;
import java.util.Optional;

import model.Food;

/**
 * Food Service Interface
 * Defines contract for food business operations
 * Follows SOLID: Interface Segregation Principle, Dependency Inversion Principle
 */
public interface IFoodService {
    
    /**
     * Register a new food item
     * 
     * @param food Food to register
     * @return Registered food with generated ID
     * @throws IllegalArgumentException if validation fails
     */
    Food registerFood(Food food) throws IllegalArgumentException;
    
    /**
     * Update an existing food item
     * 
     * @param food Food to update
     * @return Updated food
     * @throws IllegalArgumentException if validation fails or food not found
     */
    Food updateFood(Food food) throws IllegalArgumentException;
    
    /**
     * Delete a food item
     * 
     * @param foodId Food ID to delete
     * @return true if deleted, false otherwise
     */
    boolean deleteFood(int foodId);
    
    /**
     * Get all food items
     * 
     * @return List of all foods
     */
    List<Food> getAllFoods();
    
    /**
     * Get food by ID
     * 
     * @param foodId Food ID
     * @return Optional containing food if found
     */
    Optional<Food> getFoodById(int foodId);
    
    /**
     * Validate food name
     * 
     * @param foodName Food name to validate
     * @return true if valid, false otherwise
     */
    boolean validateFoodName(String foodName);
    
    /**
     * Validate food price
     * 
     * @param foodPrice Food price to validate
     * @return true if valid, false otherwise
     */
    boolean validateFoodPrice(double foodPrice);
    
    /**
     * Validate food type
     * 
     * @param foodType Food type to validate
     * @return true if valid, false otherwise
     */
    boolean validateFoodType(String foodType);
}
