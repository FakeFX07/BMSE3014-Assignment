package service.impl;

import java.util.List;
import java.util.Optional;

import model.Food;
import repository.interfaces.IFoodRepository;
import service.interfaces.IFoodService;

/**
 * Food Service Implementation
 * Contains business logic for food operations
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle
 */
public class FoodService implements IFoodService {
    
    private final IFoodRepository foodRepository;
    
    // Validation constants
    private static final double MIN_PRICE = 0.01;
    
    public FoodService(IFoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }
    
    @Override
    public Food registerFood(Food food) throws IllegalArgumentException {
        // Validate all fields
        if (!validateFoodName(food.getFoodName())) {
            throw new IllegalArgumentException("Food name must contain only letters");
        }
        if (!validateFoodPrice(food.getFoodPrice())) {
            throw new IllegalArgumentException("Food price must be at least RM " + MIN_PRICE);
        }
        if (!validateFoodType(food.getFoodType())) {
            throw new IllegalArgumentException("Food type must be 'Set' or 'A la carte'");
        }
        
        // Generate food ID
        food.setFoodId(generateUniqueFoodId());
        
        // Save food
        return foodRepository.save(food);
    }
    
    @Override
    public Food updateFood(Food food) throws IllegalArgumentException {
        // Validate food exists
        if (!foodRepository.existsById(food.getFoodId())) {
            throw new IllegalArgumentException("Food with ID " + food.getFoodId() + " not found");
        }
        
        // Validate all fields
        if (!validateFoodName(food.getFoodName())) {
            throw new IllegalArgumentException("Food name must contain only letters");
        }
        if (!validateFoodPrice(food.getFoodPrice())) {
            throw new IllegalArgumentException("Food price must be at least RM " + MIN_PRICE);
        }
        if (!validateFoodType(food.getFoodType())) {
            throw new IllegalArgumentException("Food type must be 'Set' or 'A la carte'");
        }
        
        // Update food
        return foodRepository.update(food);
    }

    private int generateUniqueFoodId() {
        int nextId = foodRepository.getNextFoodId();
        // Ensure uniqueness in case of concurrent inserts
        while (foodRepository.existsById(nextId)) {
            nextId++;
        }
        return nextId;
    }
    
    @Override
    public boolean deleteFood(int foodId) {
        if (!foodRepository.existsById(foodId)) {
            return false;
        }
        return foodRepository.deleteById(foodId);
    }
    
    @Override
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }
    
    @Override
    public Optional<Food> getFoodById(int foodId) {
        return foodRepository.findById(foodId);
    }
    
    @Override
    public boolean validateFoodName(String foodName) {
        if (foodName == null || foodName.trim().isEmpty()) {
            return false;
        }
        // Check if food name contains only letters and spaces
        return foodName.matches("^[a-zA-Z\\s]+$");
    }
    
    @Override
    public boolean validateFoodPrice(double foodPrice) {
        return foodPrice >= MIN_PRICE;
    }
    
    @Override
    public boolean validateFoodType(String foodType) {
        if (foodType == null) {
            return false;
        }
        return "Set".equalsIgnoreCase(foodType) || "A la carte".equalsIgnoreCase(foodType);
    }
    
    @Override
    public boolean isFoodNameUnique(String foodName) {
        if (foodName == null || foodName.trim().isEmpty()) {
            return false;
        }
        return !foodRepository.existsByName(foodName);
    }
}
