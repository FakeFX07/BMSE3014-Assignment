package service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.Food;
import repository.interfaces.IFoodRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FoodService
 * Tests business logic for food operations
 */
public class FoodServiceTest {
    
    private FoodService foodService;
    private MockFoodRepository mockRepository;
    
    @BeforeEach
    void setUp() {
        mockRepository = new MockFoodRepository();
        foodService = new FoodService(mockRepository);
    }
    
    // ============= registerFood Tests =============
    
    @Test
    @DisplayName("registerFood - Valid food - Should register successfully")
    void testRegisterFood_ValidFood_ReturnsRegisteredFood() {
        Food food = new Food("Chicken Rice", 10.50, "Set");
        
        Food result = foodService.registerFood(food);
        
        assertNotNull(result);
        assertEquals("Chicken Rice", result.getFoodName());
        assertTrue(result.getFoodId() >= 2000);
    }
    
    @Test
    @DisplayName("registerFood - Invalid name with numbers - Should throw exception")
    void testRegisterFood_InvalidNameWithNumbers_ThrowsException() {
        Food food = new Food("Food123", 10.50, "Set");
        
        assertThrows(IllegalArgumentException.class, () -> foodService.registerFood(food));
    }
    
    @Test
    @DisplayName("registerFood - Invalid price zero - Should throw exception")
    void testRegisterFood_InvalidPriceZero_ThrowsException() {
        Food food = new Food("Chicken Rice", 0.0, "Set");
        
        assertThrows(IllegalArgumentException.class, () -> foodService.registerFood(food));
    }
    
    @Test
    @DisplayName("registerFood - Invalid price negative - Should throw exception")
    void testRegisterFood_InvalidPriceNegative_ThrowsException() {
        Food food = new Food("Chicken Rice", -5.0, "Set");
        
        assertThrows(IllegalArgumentException.class, () -> foodService.registerFood(food));
    }
    
    @Test
    @DisplayName("registerFood - Invalid type - Should throw exception")
    void testRegisterFood_InvalidType_ThrowsException() {
        Food food = new Food("Chicken Rice", 10.50, "Invalid");
        
        assertThrows(IllegalArgumentException.class, () -> foodService.registerFood(food));
    }
    
    @Test
    @DisplayName("registerFood - Minimum valid price - Should register successfully")
    void testRegisterFood_MinimumValidPrice_ReturnsRegisteredFood() {
        Food food = new Food("Cheap Food", 0.01, "Set");
        
        Food result = foodService.registerFood(food);
        
        assertNotNull(result);
        assertEquals(0.01, result.getFoodPrice(), 0.001);
    }
    
    // ============= updateFood Tests =============
    
    @Test
    @DisplayName("updateFood - Valid existing food - Should update successfully")
    void testUpdateFood_ValidExistingFood_ReturnsUpdatedFood() {
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        mockRepository.addFood(food);
        
        food.setFoodPrice(12.00);
        Food result = foodService.updateFood(food);
        
        assertNotNull(result);
        assertEquals(12.00, result.getFoodPrice(), 0.01);
    }
    
    @Test
    @DisplayName("updateFood - Non-existent food - Should throw exception")
    void testUpdateFood_NonExistentFood_ThrowsException() {
        Food food = new Food(9999, "Non-existent", 10.50, "Set");
        
        assertThrows(IllegalArgumentException.class, () -> foodService.updateFood(food));
    }
    
    @Test
    @DisplayName("updateFood - Invalid name - Should throw exception")
    void testUpdateFood_InvalidName_ThrowsException() {
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        mockRepository.addFood(food);
        
        food.setFoodName("Food123");
        
        assertThrows(IllegalArgumentException.class, () -> foodService.updateFood(food));
    }
    
    // ============= deleteFood Tests =============
    
    @Test
    @DisplayName("deleteFood - Existing food - Should delete successfully")
    void testDeleteFood_ExistingFood_ReturnsTrue() {
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        mockRepository.addFood(food);
        
        boolean result = foodService.deleteFood(2000);
        
        assertTrue(result);
        assertFalse(mockRepository.existsById(2000));
    }
    
    @Test
    @DisplayName("deleteFood - Non-existent food - Should return false")
    void testDeleteFood_NonExistentFood_ReturnsFalse() {
        boolean result = foodService.deleteFood(9999);
        
        assertFalse(result);
    }
    
    // ============= getFoodById Tests =============
    
    @Test
    @DisplayName("getFoodById - Existing food - Should return food")
    void testGetFoodById_ExistingFood_ReturnsFood() {
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        mockRepository.addFood(food);
        
        Optional<Food> result = foodService.getFoodById(2000);
        
        assertTrue(result.isPresent());
        assertEquals("Chicken Rice", result.get().getFoodName());
    }
    
    @Test
    @DisplayName("getFoodById - Non-existent food - Should return empty")
    void testGetFoodById_NonExistentFood_ReturnsEmpty() {
        Optional<Food> result = foodService.getFoodById(9999);
        
        assertFalse(result.isPresent());
    }
    
    // ============= getFoodByName Tests =============
    
    @Test
    @DisplayName("getFoodByName - Existing food - Should return food")
    void testGetFoodByName_ExistingFood_ReturnsFood() {
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        mockRepository.addFood(food);
        
        Optional<Food> result = foodService.getFoodByName("Chicken Rice");
        
        assertTrue(result.isPresent());
        assertEquals(2000, result.get().getFoodId());
    }
    
    @Test
    @DisplayName("getFoodByName - Case insensitive - Should return food")
    void testGetFoodByName_CaseInsensitive_ReturnsFood() {
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        mockRepository.addFood(food);
        
        Optional<Food> result = foodService.getFoodByName("CHICKEN RICE");
        
        assertTrue(result.isPresent());
    }
    
    @Test
    @DisplayName("getFoodByName - Non-existent food - Should return empty")
    void testGetFoodByName_NonExistentFood_ReturnsEmpty() {
        Optional<Food> result = foodService.getFoodByName("Non-existent");
        
        assertFalse(result.isPresent());
    }
    
    // ============= getAllFoods Tests =============
    
    @Test
    @DisplayName("getAllFoods - Multiple foods - Should return all")
    void testGetAllFoods_MultipleFoods_ReturnsAll() {
        mockRepository.addFood(new Food(2000, "Food 1", 10.00, "Set"));
        mockRepository.addFood(new Food(2001, "Food 2", 15.00, "A la carte"));
        
        List<Food> result = foodService.getAllFoods();
        
        assertEquals(2, result.size());
    }
    
    @Test
    @DisplayName("getAllFoods - Empty repository - Should return empty list")
    void testGetAllFoods_EmptyRepository_ReturnsEmptyList() {
        List<Food> result = foodService.getAllFoods();
        
        assertTrue(result.isEmpty());
    }
    
    // ============= Validation Tests =============
    
    @Test
    @DisplayName("validateFoodName - Valid name - Should return true")
    void testValidateFoodName_ValidName_ReturnsTrue() {
        assertTrue(foodService.validateFoodName("Chicken Rice"));
        assertTrue(foodService.validateFoodName("Nasi Lemak"));
    }
    
    @Test
    @DisplayName("validateFoodName - Name with numbers - Should return false")
    void testValidateFoodName_NameWithNumbers_ReturnsFalse() {
        assertFalse(foodService.validateFoodName("Food123"));
        assertFalse(foodService.validateFoodName("123"));
    }
    
    @Test
    @DisplayName("validateFoodName - Null name - Should return false")
    void testValidateFoodName_NullName_ReturnsFalse() {
        assertFalse(foodService.validateFoodName(null));
    }
    
    @Test
    @DisplayName("validateFoodName - Empty name - Should return false")
    void testValidateFoodName_EmptyName_ReturnsFalse() {
        assertFalse(foodService.validateFoodName(""));
        assertFalse(foodService.validateFoodName("   "));
    }
    
    @Test
    @DisplayName("validateFoodPrice - Valid price - Should return true")
    void testValidateFoodPrice_ValidPrice_ReturnsTrue() {
        assertTrue(foodService.validateFoodPrice(10.50));
        assertTrue(foodService.validateFoodPrice(0.01));
    }
    
    @Test
    @DisplayName("validateFoodPrice - Zero price - Should return false")
    void testValidateFoodPrice_ZeroPrice_ReturnsFalse() {
        assertFalse(foodService.validateFoodPrice(0.0));
    }
    
    @Test
    @DisplayName("validateFoodPrice - Negative price - Should return false")
    void testValidateFoodPrice_NegativePrice_ReturnsFalse() {
        assertFalse(foodService.validateFoodPrice(-10.0));
    }
    
    @Test
    @DisplayName("validateFoodType - Valid type Set - Should return true")
    void testValidateFoodType_ValidTypeSet_ReturnsTrue() {
        assertTrue(foodService.validateFoodType("Set"));
        assertTrue(foodService.validateFoodType("set"));
        assertTrue(foodService.validateFoodType("SET"));
    }
    
    @Test
    @DisplayName("validateFoodType - Valid type A la carte - Should return true")
    void testValidateFoodType_ValidTypeAlaCarte_ReturnsTrue() {
        assertTrue(foodService.validateFoodType("A la carte"));
        assertTrue(foodService.validateFoodType("a la carte"));
    }
    
    @Test
    @DisplayName("validateFoodType - Invalid type - Should return false")
    void testValidateFoodType_InvalidType_ReturnsFalse() {
        assertFalse(foodService.validateFoodType("Other"));
        assertFalse(foodService.validateFoodType(""));
    }
    
    @Test
    @DisplayName("validateFoodType - Null type - Should return false")
    void testValidateFoodType_NullType_ReturnsFalse() {
        assertFalse(foodService.validateFoodType(null));
    }
    
    // ============= isFoodNameUnique Tests =============
    
    @Test
    @DisplayName("isFoodNameUnique - Unique name - Should return true")
    void testIsFoodNameUnique_UniqueName_ReturnsTrue() {
        mockRepository.addFood(new Food(2000, "Existing Food", 10.00, "Set"));
        
        assertTrue(foodService.isFoodNameUnique("New Food"));
    }
    
    @Test
    @DisplayName("isFoodNameUnique - Duplicate name - Should return false")
    void testIsFoodNameUnique_DuplicateName_ReturnsFalse() {
        mockRepository.addFood(new Food(2000, "Existing Food", 10.00, "Set"));
        
        assertFalse(foodService.isFoodNameUnique("Existing Food"));
    }
    
    @Test
    @DisplayName("isFoodNameUnique - Null name - Should return false")
    void testIsFoodNameUnique_NullName_ReturnsFalse() {
        assertFalse(foodService.isFoodNameUnique(null));
    }
    
    @Test
    @DisplayName("isFoodNameUnique - Empty name - Should return false")
    void testIsFoodNameUnique_EmptyName_ReturnsFalse() {
        assertFalse(foodService.isFoodNameUnique(""));
    }
    
    // ============= Mock Repository =============
    
    private static class MockFoodRepository implements IFoodRepository {
        private java.util.Map<Integer, Food> foods = new java.util.HashMap<>();
        
        @Override
        public Optional<Food> findById(int foodId) {
            return Optional.ofNullable(foods.get(foodId));
        }
        
        @Override
        public Optional<Food> findByName(String foodName) {
            return foods.values().stream()
                    .filter(f -> f.getFoodName().equalsIgnoreCase(foodName))
                    .findFirst();
        }
        
        @Override
        public List<Food> findAll() {
            return new ArrayList<>(foods.values());
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
        
        @Override
        public boolean decrementQuantity(int foodId, int quantityToDeduct) {
            Food food = foods.get(foodId);
            if (food != null && food.getQuantity() >= quantityToDeduct) {
                food.setQuantity(food.getQuantity() - quantityToDeduct);
                return true;
            }
            return false;
        }
        
        public void addFood(Food food) {
            foods.put(food.getFoodId(), food);
        }
    }
}

