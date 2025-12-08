package main.java.repository.interfaces;

import java.util.List;
import java.util.Optional;

import main.java.model.Food;

/**
 * Food Repository Interface
 * Defines contract for food data access operations
 * Follows SOLID: Interface Segregation Principle, Dependency Inversion Principle
 */
public interface IFoodRepository {
    
    /**
     * Find food by ID
     * 
     * @param foodId Food ID
     * @return Optional containing food if found
     */
    Optional<Food> findById(int foodId);
    
    /**
     * Find all foods
     * 
     * @return List of all foods
     */
    List<Food> findAll();
    
    /**
     * Save food (create or update)
     * 
     * @param food Food to save
     * @return Saved food with generated ID
     */
    Food save(Food food);
    
    /**
     * Update food
     * 
     * @param food Food to update
     * @return Updated food
     */
    Food update(Food food);
    
    /**
     * Delete food by ID
     * 
     * @param foodId Food ID to delete
     * @return true if deleted, false otherwise
     */
    boolean deleteById(int foodId);
    
    /**
     * Get next available food ID
     * 
     * @return Next food ID
     */
    int getNextFoodId();
    
    /**
     * Check if food exists
     * 
     * @param foodId Food ID to check
     * @return true if exists, false otherwise
     */
    boolean existsById(int foodId);
}
