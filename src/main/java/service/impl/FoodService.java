package service.impl;

import java.util.List;
import java.util.Optional;

import model.Food;
import repository.interfaces.IFoodRepository;
import service.interfaces.IFoodService;

public class FoodService implements IFoodService {
    
    private final IFoodRepository foodRepository;
    
    // Validation constants
    private static final double MIN_PRICE = 0.01;
    
    // Initialize FoodService with repository dependency
    public FoodService(IFoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }
    
    // Register a new food
    @Override
    public Food registerFood(Food food) throws IllegalArgumentException {
        validateAllFields(food);
        
        // Generate food ID
        food.setFoodId(generateUniqueFoodId());
        
        // Save food
        return foodRepository.save(food);
    }
    
    // Update existing food details
    @Override
    public Food updateFood(Food food) throws IllegalArgumentException {
        // Validate food exists
        if (!foodRepository.existsById(food.getFoodId())) {
            throw new IllegalArgumentException("Food with ID " + food.getFoodId() + " not found");
        }
        
        validateAllFields(food);
        
        // Update food
        return foodRepository.update(food);
    }

    // Validate all food fields
    private void validateAllFields(Food food) throws IllegalArgumentException {
        if (!validateFoodName(food.getFoodName())) {
            throw new IllegalArgumentException("Food name must contain only letters");
        }
        if (!validateFoodPrice(food.getFoodPrice())) {
            throw new IllegalArgumentException("Food price must be at least RM " + MIN_PRICE);
        }
        if (!validateFoodType(food.getFoodType())) {
            throw new IllegalArgumentException("Food type must be 'Set' or 'A la carte'");
        }
    }

    // Generate unique food ID from repository
    private int generateUniqueFoodId() {
        int nextId = foodRepository.getNextFoodId();
        while (foodRepository.existsById(nextId)) {
            nextId++;
        }
        return nextId;
    }
    
    // Delete food by ID if it exists
    @Override
    public boolean deleteFood(int foodId) {
        if (!foodRepository.existsById(foodId)) {
            return false;
        }
        return foodRepository.deleteById(foodId);
    }
    
    // Retrieve all food records
    @Override
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }
    
     // Retrieve food by ID
    @Override
    public Optional<Food> getFoodById(int foodId) {
        return foodRepository.findById(foodId);
    }
    
    // Retrieve food by name
    @Override
    public Optional<Food> getFoodByName(String foodName) {
        return foodRepository.findByName(foodName);
    }
    
     // Validate food name format
    @Override
    public boolean validateFoodName(String foodName) {
        if (foodName == null || foodName.trim().isEmpty()) {
            return false;
        }
        // Check if food name contains only letters and spaces
        return foodName.matches("^[a-zA-Z\\s]+$");
    }
    
     // Validate food price value
    @Override
    public boolean validateFoodPrice(double foodPrice) {
        return foodPrice >= MIN_PRICE;
    }
    
    // Validate food type value
    @Override
    public boolean validateFoodType(String foodType) {
        if (foodType == null) {
            return false;
        }
        return "Set".equalsIgnoreCase(foodType) || "A la carte".equalsIgnoreCase(foodType);
    }
    
    // Check food name is unique or not
    @Override
    public boolean isFoodNameUnique(String foodName) {
        if (foodName == null || foodName.trim().isEmpty()) {
            return false;
        }
        return !foodRepository.existsByName(foodName);
    }
}
