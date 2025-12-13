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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodControllerTest {
    
    @Mock
    private IFoodService foodService;
    
    private FoodController foodController;
    private final ByteArrayOutputStream outputCaptor = new ByteArrayOutputStream();
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputCaptor));
        foodController = new FoodController(foodService);
    }
    
    //Initialization Sanity Check
    @Test
    void shouldInitializeControllerCorrectly() {
        assertNotNull(foodController, "Controller should be instantiated with service dependency");
    }

    // Registration Tests
    @Test
    @DisplayName("Should return registered food object when input is valid")
    void shouldRegisterFoodSuccessfully() {
        Food newFood = new Food("Nasi Lemak", 5.50, "A la carte");
        Food savedFood = new Food(101, "Nasi Lemak", 5.50, "A la carte");
        
        when(foodService.registerFood(any(Food.class))).thenReturn(savedFood);
        
        Food result = foodController.registerFood(newFood);
        
        assertNotNull(result);
        assertEquals(101, result.getFoodId());
        verify(foodService).registerFood(newFood);
    }
    
    @Test
    @DisplayName("Should handle invalid food registration gracefully")
    void shouldFailRegistration_WhenServiceThrowsException() {
        Food invalidFood = new Food("Food123", 10.50, "Set");
        
        when(foodService.registerFood(any(Food.class)))
                .thenThrow(new IllegalArgumentException("Invalid format"));
        
        Food result = foodController.registerFood(invalidFood);
        
        assertNull(result);
        assertTrue(outputCaptor.toString().contains("Registration failed"), "Should print failure message to console");
    }

    // Update & Delete Operations
    @Test
    void shouldUpdateFoodSuccessfully() {
        Food updateRequest = new Food(101, "Nasi Lemak Special", 12.00, "Set");
        
        when(foodService.updateFood(any(Food.class))).thenReturn(updateRequest);
        
        Food result = foodController.updateFood(updateRequest);
        
        assertNotNull(result);
        assertEquals(12.00, result.getFoodPrice());
        verify(foodService).updateFood(updateRequest);
    }
    
    @Test
    void shouldHandleUpdateForNonExistentFood() {
        Food nonExistent = new Food(999, "Ghost Food", 10.50, "Set");
        
        when(foodService.updateFood(any(Food.class)))
                .thenThrow(new IllegalArgumentException("Not found"));
        
        Food result = foodController.updateFood(nonExistent);
        
        assertNull(result);
        assertTrue(outputCaptor.toString().contains("Update failed"));
    }
    
    @Test
    void shouldDeleteExistingFood() {
        when(foodService.deleteFood(101)).thenReturn(true);
        
        boolean isDeleted = foodController.deleteFood(101);
        
        assertTrue(isDeleted);
        verify(foodService).deleteFood(101);
    }
    
    @Test
    void shouldFailDeleteForMissingFood() {
        when(foodService.deleteFood(anyInt())).thenReturn(false);
        
        boolean isDeleted = foodController.deleteFood(999);
        
        assertFalse(isDeleted);
        // Verify console error output
        String logs = outputCaptor.toString();
        assertTrue(logs.contains("not found") || logs.contains("could not be deleted"));
    }

    // Retrieval Tests (Get All, By ID, By Name)
    @Test
    void shouldRetrieveAllFoods() {
        List<Food> mockList = Arrays.asList(
            new Food(1, "Fries", 5.00, "Side"),
            new Food(2, "Burger", 15.00, "Main")
        );
        
        when(foodService.getAllFoods()).thenReturn(mockList);
        
        List<Food> result = foodController.getAllFoods();
        assertEquals(2, result.size());
    }
    
    @Test
    void shouldReturnEmptyListExample() {
        when(foodService.getAllFoods()).thenReturn(Collections.emptyList());
        assertTrue(foodController.getAllFoods().isEmpty());
    }
    
    @Test
    void shouldFindFoodById() {
        Food mockFood = new Food(50, "Steak", 55.00, "Set");
        when(foodService.getFoodById(50)).thenReturn(Optional.of(mockFood));
        
        Food result = foodController.getFoodById(50);
        
        assertNotNull(result);
        assertEquals("Steak", result.getFoodName());
    }
    
    @Test
    void shouldReturnNull_WhenFoodIdNotFound() {
        when(foodService.getFoodById(anyInt())).thenReturn(Optional.empty());
        assertNull(foodController.getFoodById(404));
    }
    
    @Test
    void shouldFindFoodByName() {
        Food mockFood = new Food(1, "Pasta", 18.00, "A la carte");
        when(foodService.getFoodByName("Pasta")).thenReturn(Optional.of(mockFood));
        
        Food result = foodController.getFoodByName("Pasta");
        assertEquals(1, result.getFoodId());
    }

    @Test
    void shouldReturnNull_WhenFoodNameNotFound() {
        when(foodService.getFoodByName("Unknown")).thenReturn(Optional.empty());
        assertNull(foodController.getFoodByName("Unknown"));
    }

    // Field Validations
    @Test
    void testNameValidation() {
        when(foodService.validateFoodName("Valid Name")).thenReturn(true);
        when(foodService.validateFoodName("Inv@lid")).thenReturn(false);

        assertTrue(foodController.validateFoodName("Valid Name"));
        assertFalse(foodController.validateFoodName("Inv@lid"));
    }
    
    @Test
    void testPriceValidation() {
        when(foodService.validateFoodPrice(10.0)).thenReturn(true);
        when(foodService.validateFoodPrice(-5.0)).thenReturn(false);

        assertTrue(foodController.validateFoodPrice(10.0));
        assertFalse(foodController.validateFoodPrice(-5.0));
    }
    
    @Test
    void testTypeValidation() {
        when(foodService.validateFoodType("Set")).thenReturn(true);
        when(foodService.validateFoodType("Junk")).thenReturn(false);

        assertTrue(foodController.validateFoodType("Set"));
        assertFalse(foodController.validateFoodType("Junk"));
    }

    @Test
    void testNameUniqueness() {
        when(foodService.isFoodNameUnique("Unique")).thenReturn(true);
        when(foodService.isFoodNameUnique("Duplicate")).thenReturn(false);

        assertTrue(foodController.isFoodNameUnique("Unique"));
        assertFalse(foodController.isFoodNameUnique("Duplicate"));
    }
}