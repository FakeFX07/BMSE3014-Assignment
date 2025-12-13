package service.interfaces;

import java.util.List;
import java.util.Optional;

import model.Food;

public interface IFoodService {
    
    //Register a new food item
    Food registerFood(Food food) throws IllegalArgumentException;
    
    //Update an existing food item
    Food updateFood(Food food) throws IllegalArgumentException;
    
    //Delete a food item
    boolean deleteFood(int foodId);
    
    //Get all food items
    List<Food> getAllFoods();
    
    //Get food by ID
    Optional<Food> getFoodById(int foodId);
    
    //Get food by name (case-insensitive)
    Optional<Food> getFoodByName(String foodName);
    
    //Validate food name
    boolean validateFoodName(String foodName);
    
    //Validate food price
    boolean validateFoodPrice(double foodPrice);
    
    //Validate food type
    boolean validateFoodType(String foodType);
    
    //Check if food name is unique
    boolean isFoodNameUnique(String foodName);
}
