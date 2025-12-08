package controller;

import model.Food;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import service.interfaces.IFoodService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Food Controller Test
 */
public class FoodControllerTest {
    
    private FoodController controller;
    private IFoodService mockService;
    
    @BeforeEach
    void setUp() {
        mockService = mock(IFoodService.class);
        controller = new FoodController(mockService);
    }
    
    @Test
    @DisplayName("Test registerFood - success")
    void testRegisterFood_Success() {
        Food food = new Food("Chicken Rice", 10.50, "Set");
        Food registered = new Food(2000, "Chicken Rice", 10.50, "Set");
        when(mockService.registerFood(food)).thenReturn(registered);
        
        Food result = controller.registerFood(food);
        assertNotNull(result);
        assertEquals(2000, result.getFoodId());
    }
    
    @Test
    @DisplayName("Test registerFood - validation failure")
    void testRegisterFood_ValidationFailure() {
        Food food = new Food("Food123", 70.00, "Set");
        when(mockService.registerFood(food)).thenThrow(new IllegalArgumentException("Invalid price"));
        
        Food result = controller.registerFood(food);
        assertNull(result);
    }
    
    @Test
    @DisplayName("Test updateFood - success")
    void testUpdateFood_Success() {
        Food food = new Food(2000, "Updated Food", 12.00, "Set");
        when(mockService.updateFood(food)).thenReturn(food);
        
        Food result = controller.updateFood(food);
        assertNotNull(result);
        assertEquals(12.00, result.getFoodPrice(), 0.01);
    }
    
    @Test
    @DisplayName("Test deleteFood - success")
    void testDeleteFood_Success() {
        when(mockService.deleteFood(2000)).thenReturn(true);
        assertTrue(controller.deleteFood(2000));
    }
    
    @Test
    @DisplayName("Test deleteFood - not found")
    void testDeleteFood_NotFound() {
        when(mockService.deleteFood(9999)).thenReturn(false);
        assertFalse(controller.deleteFood(9999));
    }
    
    @Test
    @DisplayName("Test getAllFoods - returns list")
    void testGetAllFoods() {
        List<Food> foods = Arrays.asList(
            new Food(2000, "Food 1", 10.00, "Set"),
            new Food(2001, "Food 2", 15.00, "A la carte")
        );
        when(mockService.getAllFoods()).thenReturn(foods);
        
        List<Food> result = controller.getAllFoods();
        assertEquals(2, result.size());
    }
    
    @Test
    @DisplayName("Test getFoodById - existing")
    void testGetFoodById_Existing() {
        Food food = new Food(2000, "Food", 10.00, "Set");
        when(mockService.getFoodById(2000)).thenReturn(Optional.of(food));
        
        Food result = controller.getFoodById(2000);
        assertNotNull(result);
        assertEquals(2000, result.getFoodId());
    }
    
    @Test
    @DisplayName("Test getFoodById - not found")
    void testGetFoodById_NotFound() {
        when(mockService.getFoodById(9999)).thenReturn(Optional.empty());
        assertNull(controller.getFoodById(9999));
    }
    
    @Test
    @DisplayName("Test validateFoodName - delegates to service")
    void testValidateFoodName() {
        when(mockService.validateFoodName("Chicken Rice")).thenReturn(true);
        assertTrue(controller.validateFoodName("Chicken Rice"));
    }
    
    @Test
    @DisplayName("Test validateFoodPrice - delegates to service")
    void testValidateFoodPrice() {
        when(mockService.validateFoodPrice(10.50)).thenReturn(true);
        assertTrue(controller.validateFoodPrice(10.50));
    }
    
    @Test
    @DisplayName("Test validateFoodType - delegates to service")
    void testValidateFoodType() {
        when(mockService.validateFoodType("Set")).thenReturn(true);
        assertTrue(controller.validateFoodType("Set"));
    }
}

