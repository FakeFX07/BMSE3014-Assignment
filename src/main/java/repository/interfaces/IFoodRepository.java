package repository.interfaces;

import java.util.List;
import java.util.Optional;

import model.Food;

public interface IFoodRepository {
    //Find food by ID
    Optional<Food> findById(int foodId);
    
    //Find food by name (case-insensitive)
    Optional<Food> findByName(String foodName);
    
    //Find all foods
    List<Food> findAll();
    
    //Save food (create or update)
    Food save(Food food);
    
    //Update food
    Food update(Food food);
    
    //Delete food by ID
    boolean deleteById(int foodId);
    
    //Get next available food ID
    int getNextFoodId();
    
    //Check if food exists
    boolean existsById(int foodId);
    
    //Check if food name exists
    boolean existsByName(String foodName);
    
    //Decrement food quantity when order is placed
    boolean decrementQuantity(int foodId, int quantityToDeduct);
}
