package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import model.Food;
import service.interfaces.IFoodService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for FoodController
 * Tests controller logic and interaction with service layer
 */
@ExtendWith(MockitoExtension.class)
public class FoodControllerTest {
    
    @Mock
    private IFoodService foodService;
    
    private FoodController foodController;
    private ByteArrayOutputStream outputStream;
    
    @BeforeEach
    void setUp() {
        foodController = new FoodController(foodService);
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }
    
    // ============= registerFood Tests =============
    
    @Test
    @DisplayName("registerFood - Valid food - Should return registered food")
    void testRegisterFood_ValidFood_ReturnsRegisteredFood() {
        Food food = new Food("Chicken Rice", 10.50, "Set");
        Food registeredFood = new Food(2000, "Chicken Rice", 10.50, "Set");
        when(foodService.registerFood(any(Food.class))).thenReturn(registeredFood);
        
        Food result = foodController.registerFood(food);
        
        assertNotNull(result);
        assertEquals(2000, result.getFoodId());
        verify(foodService).registerFood(food);
    }
    
    @Test
    @DisplayName("registerFood - Invalid food - Should return null and print error")
    void testRegisterFood_InvalidFood_ReturnsNullAndPrintsError() {
        Food food = new Food("Food123", 10.50, "Set");
        when(foodService.registerFood(any(Food.class)))
                .thenThrow(new IllegalArgumentException("Food name must contain only letters"));
        
        Food result = foodController.registerFood(food);
        
        assertNull(result);
        String output = outputStream.toString();
        assertTrue(output.contains("Registration failed"));
        verify(foodService).registerFood(food);
    }
    
    // ============= updateFood Tests =============
    
    @Test
    @DisplayName("updateFood - Valid food - Should return updated food")
    void testUpdateFood_ValidFood_ReturnsUpdatedFood() {
        Food food = new Food(2000, "Chicken Rice", 12.00, "Set");
        when(foodService.updateFood(any(Food.class))).thenReturn(food);
        
        Food result = foodController.updateFood(food);
        
        assertNotNull(result);
        assertEquals(12.00, result.getFoodPrice(), 0.01);
        verify(foodService).updateFood(food);
    }
    
    @Test
    @DisplayName("updateFood - Non-existent food - Should return null and print error")
    void testUpdateFood_NonExistentFood_ReturnsNullAndPrintsError() {
        Food food = new Food(9999, "Non-existent", 10.50, "Set");
        when(foodService.updateFood(any(Food.class)))
                .thenThrow(new IllegalArgumentException("Food with ID 9999 not found"));
        
        Food result = foodController.updateFood(food);
        
        assertNull(result);
        String output = outputStream.toString();
        assertTrue(output.contains("Update failed"));
        verify(foodService).updateFood(food);
    }
    
    // ============= deleteFood Tests =============
    
    @Test
    @DisplayName("deleteFood - Existing food - Should return true")
    void testDeleteFood_ExistingFood_ReturnsTrue() {
        when(foodService.deleteFood(anyInt())).thenReturn(true);
        
        boolean result = foodController.deleteFood(2000);
        
        assertTrue(result);
        verify(foodService).deleteFood(2000);
    }
    
    @Test
    @DisplayName("deleteFood - Non-existent food - Should return false and print error")
    void testDeleteFood_NonExistentFood_ReturnsFalseAndPrintsError() {
        when(foodService.deleteFood(anyInt())).thenReturn(false);
        
        boolean result = foodController.deleteFood(9999);
        
        assertFalse(result);
        String output = outputStream.toString();
        assertTrue(output.contains("not found") || output.contains("could not be deleted"));
        verify(foodService).deleteFood(9999);
    }
    
    // ============= getAllFoods Tests =============
    
    @Test
    @DisplayName("getAllFoods - Multiple foods - Should return all foods")
    void testGetAllFoods_MultipleFoods_ReturnsAllFoods() {
        List<Food> foods = Arrays.asList(
            new Food(2000, "Food 1", 10.00, "Set"),
            new Food(2001, "Food 2", 15.00, "A la carte")
        );
        when(foodService.getAllFoods()).thenReturn(foods);
        
        List<Food> result = foodController.getAllFoods();
        
        assertEquals(2, result.size());
        verify(foodService).getAllFoods();
    }
    
    @Test
    @DisplayName("getAllFoods - Empty list - Should return empty list")
    void testGetAllFoods_EmptyList_ReturnsEmptyList() {
        when(foodService.getAllFoods()).thenReturn(Arrays.asList());
        
        List<Food> result = foodController.getAllFoods();
        
        assertTrue(result.isEmpty());
        verify(foodService).getAllFoods();
    }
    
    // ============= getFoodById Tests =============
    
    @Test
    @DisplayName("getFoodById - Existing food - Should return food")
    void testGetFoodById_ExistingFood_ReturnsFood() {
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        when(foodService.getFoodById(anyInt())).thenReturn(Optional.of(food));
        
        Food result = foodController.getFoodById(2000);
        
        assertNotNull(result);
        assertEquals("Chicken Rice", result.getFoodName());
        verify(foodService).getFoodById(2000);
    }
    
    @Test
    @DisplayName("getFoodById - Non-existent food - Should return null")
    void testGetFoodById_NonExistentFood_ReturnsNull() {
        when(foodService.getFoodById(anyInt())).thenReturn(Optional.empty());
        
        Food result = foodController.getFoodById(9999);
        
        assertNull(result);
        verify(foodService).getFoodById(9999);
    }
    
    // ============= getFoodByName Tests =============
    
    @Test
    @DisplayName("getFoodByName - Existing food - Should return food")
    void testGetFoodByName_ExistingFood_ReturnsFood() {
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        when(foodService.getFoodByName(anyString())).thenReturn(Optional.of(food));
        
        Food result = foodController.getFoodByName("Chicken Rice");
        
        assertNotNull(result);
        assertEquals(2000, result.getFoodId());
        verify(foodService).getFoodByName("Chicken Rice");
    }
    
    @Test
    @DisplayName("getFoodByName - Non-existent food - Should return null")
    void testGetFoodByName_NonExistentFood_ReturnsNull() {
        when(foodService.getFoodByName(anyString())).thenReturn(Optional.empty());
        
        Food result = foodController.getFoodByName("Non-existent");
        
        assertNull(result);
        verify(foodService).getFoodByName("Non-existent");
    }
    
    // ============= Validation Tests =============
    
    @Test
    @DisplayName("validateFoodName - Valid name - Should return true")
    void testValidateFoodName_ValidName_ReturnsTrue() {
        when(foodService.validateFoodName(anyString())).thenReturn(true);
        
        boolean result = foodController.validateFoodName("Chicken Rice");
        
        assertTrue(result);
        verify(foodService).validateFoodName("Chicken Rice");
    }
    
    @Test
    @DisplayName("validateFoodName - Invalid name - Should return false")
    void testValidateFoodName_InvalidName_ReturnsFalse() {
        when(foodService.validateFoodName(anyString())).thenReturn(false);
        
        boolean result = foodController.validateFoodName("Food123");
        
        assertFalse(result);
        verify(foodService).validateFoodName("Food123");
    }
    
    @Test
    @DisplayName("validateFoodPrice - Valid price - Should return true")
    void testValidateFoodPrice_ValidPrice_ReturnsTrue() {
        when(foodService.validateFoodPrice(anyDouble())).thenReturn(true);
        
        boolean result = foodController.validateFoodPrice(10.50);
        
        assertTrue(result);
        verify(foodService).validateFoodPrice(10.50);
    }
    
    @Test
    @DisplayName("validateFoodPrice - Invalid price - Should return false")
    void testValidateFoodPrice_InvalidPrice_ReturnsFalse() {
        when(foodService.validateFoodPrice(anyDouble())).thenReturn(false);
        
        boolean result = foodController.validateFoodPrice(0.0);
        
        assertFalse(result);
        verify(foodService).validateFoodPrice(0.0);
    }
    
    @Test
    @DisplayName("validateFoodType - Valid type - Should return true")
    void testValidateFoodType_ValidType_ReturnsTrue() {
        when(foodService.validateFoodType(anyString())).thenReturn(true);
        
        boolean result = foodController.validateFoodType("Set");
        
        assertTrue(result);
        verify(foodService).validateFoodType("Set");
    }
    
    @Test
    @DisplayName("validateFoodType - Invalid type - Should return false")
    void testValidateFoodType_InvalidType_ReturnsFalse() {
        when(foodService.validateFoodType(anyString())).thenReturn(false);
        
        boolean result = foodController.validateFoodType("Invalid");
        
        assertFalse(result);
        verify(foodService).validateFoodType("Invalid");
    }
    
    @Test
    @DisplayName("isFoodNameUnique - Unique name - Should return true")
    void testIsFoodNameUnique_UniqueName_ReturnsTrue() {
        when(foodService.isFoodNameUnique(anyString())).thenReturn(true);
        
        boolean result = foodController.isFoodNameUnique("New Food");
        
        assertTrue(result);
        verify(foodService).isFoodNameUnique("New Food");
    }
    
    @Test
    @DisplayName("isFoodNameUnique - Duplicate name - Should return false")
    void testIsFoodNameUnique_DuplicateName_ReturnsFalse() {
        when(foodService.isFoodNameUnique(anyString())).thenReturn(false);
        
        boolean result = foodController.isFoodNameUnique("Existing Food");
        
        assertFalse(result);
        verify(foodService).isFoodNameUnique("Existing Food");
    }
    
    // ============= Constructor Test =============
    
    @Test
    @DisplayName("Constructor with dependency injection - Should create instance")
    void testConstructor_WithDependencyInjection_ShouldCreateInstance() {
        FoodController controller = new FoodController(foodService);
        
        assertNotNull(controller);
    }
    
    @Test
    @DisplayName("Constructor - Should accept IFoodService interface")
    void testConstructor_ShouldAcceptIFoodServiceInterface() {
        // Verify constructor accepts interface (not concrete class)
        FoodController controller = new FoodController(foodService);
        
        assertNotNull(controller);
        // This test verifies dependency inversion principle is followed
    }
}
