package service.impl;

import model.Food;
import repository.interfaces.IFoodRepository;
import service.interfaces.IFoodService;

import java.util.List;
import java.util.Optional;

/**
 * Food Service Implementation
 * Contains business logic for food operations
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle
 */
public class FoodService implements IFoodService {
    
    private final IFoodRepository foodRepository;
    
    // Validation constants
    private static final double MIN_PRICE = 0.01;
    private static final double MAX_PRICE = 69.99;
    
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
            throw new IllegalArgumentException("Food price must be between RM " + MIN_PRICE + " and RM " + MAX_PRICE);
        }
        if (!validateFoodType(food.getFoodType())) {
            throw new IllegalArgumentException("Food type must be 'Set' or 'A la carte'");
        }
        
        // Generate food ID
        food.setFoodId(foodRepository.getNextFoodId());
        
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
            throw new IllegalArgumentException("Food price must be between RM " + MIN_PRICE + " and RM " + MAX_PRICE);
        }
        if (!validateFoodType(food.getFoodType())) {
            throw new IllegalArgumentException("Food type must be 'Set' or 'A la carte'");
        }
        
        // Update food
        return foodRepository.update(food);
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
        // Allow boundary prices within the configured range
        return foodPrice >= MIN_PRICE && foodPrice <= MAX_PRICE;
    }
    
    @Override
    public boolean validateFoodType(String foodType) {
        if (foodType == null) {
            return false;
        }
        return "Set".equalsIgnoreCase(foodType) || "A la carte".equalsIgnoreCase(foodType);
    }
}
