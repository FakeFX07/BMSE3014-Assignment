// Tests for FoodService

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Food;
import repository.interfaces.IFoodRepository;
import service.impl.FoodService;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

/**
 * Food Service Test
 * Tests food service business logic
 * Follows TDD principles
 */
public class FoodServiceTest {
    
    private FoodService foodService;
    private MockFoodRepository mockRepository;
    
    @BeforeEach
    void setUp() {
        mockRepository = new MockFoodRepository();
        foodService = new FoodService(mockRepository);
    }
    
    @Test
    @DisplayName("Test validateFoodName - valid")
    void testValidateFoodName_Valid() {
        assertTrue(foodService.validateFoodName("Chicken Rice"));
        assertTrue(foodService.validateFoodName("Nasi Lemak"));
    }
    
    @Test
    @DisplayName("Test validateFoodName - invalid with digits")
    void testValidateFoodName_Invalid() {
        assertFalse(foodService.validateFoodName("Food123"));
        assertFalse(foodService.validateFoodName("123"));
    }
    
    @Test
    @DisplayName("Test validateFoodName - null or empty")
    void testValidateFoodName_NullOrEmpty() {
        assertFalse(foodService.validateFoodName(null));
        assertFalse(foodService.validateFoodName(""));
    }

    @Test
    @DisplayName("Test validateFoodPrice - valid")
    void testValidateFoodPrice_Valid() {
        assertTrue(foodService.validateFoodPrice(10.50));
        assertTrue(foodService.validateFoodPrice(50.00));
        assertTrue(foodService.validateFoodPrice(69.99));
    }
    
    @Test
    @DisplayName("Test validateFoodPrice - invalid")
    void testValidateFoodPrice_Invalid() {
        assertFalse(foodService.validateFoodPrice(0.00));
        assertFalse(foodService.validateFoodPrice(70.00));
        assertFalse(foodService.validateFoodPrice(-10.00));
    }
    
    @Test
    @DisplayName("Test validateFoodType - valid")
    void testValidateFoodType_Valid() {
        assertTrue(foodService.validateFoodType("Set"));
        assertTrue(foodService.validateFoodType("A la carte"));
    }
    
    @Test
    @DisplayName("Test validateFoodType - invalid")
    void testValidateFoodType_Invalid() {
        assertFalse(foodService.validateFoodType("Other"));
        assertFalse(foodService.validateFoodType(""));
    }

    @Test
    @DisplayName("Test validateFoodType - null")
    void testValidateFoodType_Null() {
        assertFalse(foodService.validateFoodType(null));
    }
    
    @Test
    @DisplayName("Test registerFood - valid")
    void testRegisterFood_Valid() {
        Food food = new Food("Chicken Rice", 10.50, "Set");
        Food registered = foodService.registerFood(food);
        
        assertNotNull(registered);
        assertEquals("Chicken Rice", registered.getFoodName());
        assertTrue(registered.getFoodId() > 0);
    }
    
    @Test
    @DisplayName("Test registerFood - invalid price")
    void testRegisterFood_InvalidPrice() {
        Food food = new Food("Chicken Rice", 70.00, "Set");
        
        assertThrows(IllegalArgumentException.class, () -> {
            foodService.registerFood(food);
        });
    }
    
    @Test
    @DisplayName("Test updateFood - valid")
    void testUpdateFood_Valid() {
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        mockRepository.addFood(food);
        
        food.setFoodPrice(12.00);
        Food updated = foodService.updateFood(food);
        
        assertNotNull(updated);
        assertEquals(12.00, updated.getFoodPrice(), 0.01);
    }
    
    @Test
    @DisplayName("Test deleteFood - valid")
    void testDeleteFood_Valid() {
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        mockRepository.addFood(food);
        
        boolean deleted = foodService.deleteFood(2000);
        assertTrue(deleted);
    }

    @Test
    @DisplayName("Test deleteFood - not found")
    void testDeleteFood_NotFound() {
        assertFalse(foodService.deleteFood(9999));
    }

    @Test
    @DisplayName("Test getFoodById")
    void testGetFoodById() {
        Food food = new Food(2002, "Mee Goreng", 8.50, "A la carte");
        mockRepository.addFood(food);

        assertTrue(foodService.getFoodById(2002).isPresent());
    }
    
    @Test
    @DisplayName("Test getAllFoods")
    void testGetAllFoods() {
        mockRepository.addFood(new Food(2000, "Food 1", 10.00, "Set"));
        mockRepository.addFood(new Food(2001, "Food 2", 15.00, "A la carte"));
        
        assertEquals(2, foodService.getAllFoods().size());
    }
    
    @Test
    @DisplayName("Test registerFood - invalid name")
    void testRegisterFood_InvalidName() {
        Food food = new Food("Food123", 10.50, "Set");
        
        assertThrows(IllegalArgumentException.class, () -> {
            foodService.registerFood(food);
        });
    }
    
    @Test
    @DisplayName("Test registerFood - invalid type")
    void testRegisterFood_InvalidType() {
        Food food = new Food("Chicken Rice", 10.50, "Invalid");
        
        assertThrows(IllegalArgumentException.class, () -> {
            foodService.registerFood(food);
        });
    }
    
    @Test
    @DisplayName("Test registerFood - boundary price values")
    void testRegisterFood_BoundaryPrices() {
        Food food1 = new Food("Food One", 1.00, "Set");
        Food registered1 = foodService.registerFood(food1);
        assertNotNull(registered1);
        
        Food food2 = new Food("Food Two", 69.99, "Set");
        Food registered2 = foodService.registerFood(food2);
        assertNotNull(registered2);
    }
    
    @Test
    @DisplayName("Test updateFood - not found")
    void testUpdateFood_NotFound() {
        Food food = new Food(9999, "Non-existent", 10.50, "Set");
        
        assertThrows(IllegalArgumentException.class, () -> {
            foodService.updateFood(food);
        });
    }
    
    @Test
    @DisplayName("Test getFoodById - not found")
    void testGetFoodById_NotFound() {
        assertFalse(foodService.getFoodById(9999).isPresent());
    }
    
    @Test
    @DisplayName("Test validateFoodPrice - boundary values")
    void testValidateFoodPrice_BoundaryValues() {
        assertTrue(foodService.validateFoodPrice(1.00));
        assertTrue(foodService.validateFoodPrice(69.99));
        assertFalse(foodService.validateFoodPrice(0.00));
        assertFalse(foodService.validateFoodPrice(-1.00));
        assertFalse(foodService.validateFoodPrice(100.00));
    }
    
    // Mock repository for testing
    private static class MockFoodRepository implements IFoodRepository {
        private java.util.Map<Integer, Food> foods = new java.util.HashMap<>();
        
        @Override
        public Optional<Food> findById(int foodId) {
            return Optional.ofNullable(foods.get(foodId));
        }
        
        @Override
        public java.util.List<Food> findAll() {
            return new java.util.ArrayList<>(foods.values());
        }
        
        @Override
        public Food save(Food food) {
            if (food.getFoodId() == 0) {
                food.setFoodId(getNextFoodId());
            }
            foods.put(food.getFoodId(), food);
            return food;
        }
        
        @Override
        public Food update(Food food) {
            foods.put(food.getFoodId(), food);
            return food;
        }
        
        @Override
        public boolean deleteById(int foodId) {
            return foods.remove(foodId) != null;
        }
        
        @Override
        public int getNextFoodId() {
            return foods.size() > 0 ? 
                    foods.keySet().stream().mapToInt(Integer::intValue).max().orElse(1999) + 1 : 2000;
        }
        
        @Override
        public boolean existsById(int foodId) {
            return foods.containsKey(foodId);
        }
        
        @Override
        public boolean existsByName(String foodName) {
            return foods.values().stream()
                    .anyMatch(f -> f.getFoodName().equalsIgnoreCase(foodName));
        }
        
        public void addFood(Food food) {
            foods.put(food.getFoodId(), food);
        }
    }
}
