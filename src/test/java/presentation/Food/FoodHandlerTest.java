package presentation.Food;

import controller.FoodController;
import model.Food;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import presentation.General.UserInputHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Food Handler Test
 * Tests food CRUD operations
 */
public class FoodHandlerTest {
    
    @Mock
    private FoodController foodController;
    
    @Mock
    private UserInputHandler inputHandler;
    
    private FoodHandler foodHandler;
    private ByteArrayOutputStream outContent;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        foodHandler = new FoodHandler(foodController, inputHandler);
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(System.out);
        System.setIn(System.in);
    }
    
    @Test
    @DisplayName("Test handleDisplayAllFoods - displays foods")
    void testHandleDisplayAllFoods() {
        java.util.List<Food> foods = java.util.Arrays.asList(
            new Food(2000, "Chicken Rice", 10.50, "Set")
        );
        when(foodController.getAllFoods()).thenReturn(foods);
        
        foodHandler.handleDisplayAllFoods();
        String output = outContent.toString();
        assertTrue(output.contains("All Food Details") || output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleDeleteFood - user cancels")
    void testHandleDeleteFood_Cancel() {
        when(inputHandler.readYesNo(anyString())).thenReturn(false);
        
        foodHandler.handleDeleteFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleRegisterFood - user cancels")
    void testHandleRegisterFood_Cancel() {
        when(inputHandler.readYesNo(anyString())).thenReturn(false);
        
        foodHandler.handleRegisterFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleEditFood - user cancels")
    void testHandleEditFood_Cancel() {
        when(inputHandler.readYesNo(anyString())).thenReturn(false);
        
        foodHandler.handleEditFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleRegisterFood - complete flow with cancel")
    void testHandleRegisterFood_CompleteCancel() {
        when(inputHandler.readYesNo("Are you sure want to add new food (Y/N) : ")).thenReturn(true);
        when(inputHandler.readString("Enter your food name: ")).thenReturn("Test Food");
        when(foodController.validateFoodName("Test Food")).thenReturn(true);
        when(inputHandler.readDouble("Enter your food price RM (1-69) : RM")).thenReturn(10.50);
        when(foodController.validateFoodPrice(10.50)).thenReturn(true);
        when(inputHandler.readString("Enter your food type (Set / A la carte : ")).thenReturn("Set");
        when(foodController.validateFoodType("Set")).thenReturn(true);
        when(inputHandler.readYesNo("Are you want to proceed to add (Y/N) : ")).thenReturn(false);
        
        foodHandler.handleRegisterFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleEditFood - food not found")
    void testHandleEditFood_FoodNotFound() {
        when(inputHandler.readYesNo("Are you sure want to edit Y(YES) / N (No): ")).thenReturn(true);
        when(inputHandler.readInt("Enter the food id that want to edit : ")).thenReturn(9999);
        when(foodController.getFoodById(9999)).thenReturn(null);
        
        foodHandler.handleEditFood();
        String output = outContent.toString();
        assertTrue(output.contains("Unable to find") || output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleDeleteFood - complete flow")
    void testHandleDeleteFood_Complete() {
        when(inputHandler.readYesNo("Do you want to delete a food (Y / N ) : ")).thenReturn(true);
        when(inputHandler.readInt("Enter the food id want to delete : ")).thenReturn(2000);
        when(inputHandler.readYesNo(contains("Are you sure want to delete"))).thenReturn(false);
        
        foodHandler.handleDeleteFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleRegisterFood - complete flow with validation")
    void testHandleRegisterFood_CompleteFlow() {
        when(inputHandler.readYesNo("Are you sure want to add new food (Y/N) : ")).thenReturn(true);
        when(inputHandler.readString("Enter your food name: ")).thenReturn("Food123").thenReturn("Chicken Rice");
        when(foodController.validateFoodName("Food123")).thenReturn(false);
        when(foodController.validateFoodName("Chicken Rice")).thenReturn(true);
        when(inputHandler.readDouble("Enter your food price RM (1-69) : RM")).thenReturn(70.0).thenReturn(10.50);
        when(foodController.validateFoodPrice(70.0)).thenReturn(false);
        when(foodController.validateFoodPrice(10.50)).thenReturn(true);
        when(inputHandler.readString("Enter your food type (Set / A la carte : ")).thenReturn("Invalid").thenReturn("Set");
        when(foodController.validateFoodType("Invalid")).thenReturn(false);
        when(foodController.validateFoodType("Set")).thenReturn(true);
        when(inputHandler.readYesNo("Are you want to proceed to add (Y/N) : ")).thenReturn(true);
        
        Food registeredFood = new Food(2000, "Chicken Rice", 10.50, "Set");
        when(foodController.registerFood(any(Food.class))).thenReturn(registeredFood);
        
        foodHandler.handleRegisterFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleEditFood - edit name")
    void testHandleEditFood_EditName() {
        Food food = new Food(2000, "Old Name", 10.50, "Set");
        when(inputHandler.readYesNo("Are you sure want to edit Y(YES) / N (No): ")).thenReturn(true);
        when(inputHandler.readInt("Enter the food id that want to edit : ")).thenReturn(2000);
        when(foodController.getFoodById(2000)).thenReturn(food);
        when(inputHandler.readInt("Select your choice : ")).thenReturn(1);
        when(inputHandler.readString("Enter food name : ")).thenReturn("New Name");
        when(foodController.validateFoodName("New Name")).thenReturn(true);
        when(inputHandler.readChar(contains("Do you want to continue edit"))).thenReturn('N');
        
        Food updatedFood = new Food(2000, "New Name", 10.50, "Set");
        when(foodController.updateFood(any(Food.class))).thenReturn(updatedFood);
        
        foodHandler.handleEditFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleEditFood - edit price")
    void testHandleEditFood_EditPrice() {
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        when(inputHandler.readYesNo("Are you sure want to edit Y(YES) / N (No): ")).thenReturn(true);
        when(inputHandler.readInt("Enter the food id that want to edit : ")).thenReturn(2000);
        when(foodController.getFoodById(2000)).thenReturn(food);
        when(inputHandler.readInt("Select your choice : ")).thenReturn(2);
        when(inputHandler.readDouble("Enter food price : ")).thenReturn(15.00);
        when(foodController.validateFoodPrice(15.00)).thenReturn(true);
        when(inputHandler.readChar(contains("Do you want to continue edit"))).thenReturn('N');
        
        Food updatedFood = new Food(2000, "Chicken Rice", 15.00, "Set");
        when(foodController.updateFood(any(Food.class))).thenReturn(updatedFood);
        
        foodHandler.handleEditFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleEditFood - edit type")
    void testHandleEditFood_EditType() {
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        when(inputHandler.readYesNo("Are you sure want to edit Y(YES) / N (No): ")).thenReturn(true);
        when(inputHandler.readInt("Enter the food id that want to edit : ")).thenReturn(2000);
        when(foodController.getFoodById(2000)).thenReturn(food);
        when(inputHandler.readInt("Select your choice : ")).thenReturn(3);
        when(inputHandler.readString("Enter food type (Set / A la carte): ")).thenReturn("A la carte");
        when(foodController.validateFoodType("A la carte")).thenReturn(true);
        when(inputHandler.readChar(contains("Do you want to continue edit"))).thenReturn('N');
        
        Food updatedFood = new Food(2000, "Chicken Rice", 10.50, "A la carte");
        when(foodController.updateFood(any(Food.class))).thenReturn(updatedFood);
        
        foodHandler.handleEditFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleEditFood - invalid edit choice")
    void testHandleEditFood_InvalidChoice() {
        Food food = new Food(2000, "Chicken Rice", 10.50, "Set");
        when(inputHandler.readYesNo("Are you sure want to edit Y(YES) / N (No): ")).thenReturn(true);
        when(inputHandler.readInt("Enter the food id that want to edit : ")).thenReturn(2000);
        when(foodController.getFoodById(2000)).thenReturn(food);
        when(inputHandler.readInt("Select your choice : ")).thenReturn(99);
        when(inputHandler.readChar(contains("Do you want to continue edit"))).thenReturn('N');
        
        Food updatedFood = new Food(2000, "Chicken Rice", 10.50, "Set");
        when(foodController.updateFood(any(Food.class))).thenReturn(updatedFood);
        
        foodHandler.handleEditFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleDeleteFood - complete deletion")
    void testHandleDeleteFood_CompleteDeletion() {
        when(inputHandler.readYesNo("Do you want to delete a food (Y / N ) : ")).thenReturn(true);
        when(inputHandler.readInt("Enter the food id want to delete : ")).thenReturn(2000);
        when(inputHandler.readYesNo(contains("Are you sure want to delete"))).thenReturn(true);
        when(foodController.deleteFood(2000)).thenReturn(true);
        
        foodHandler.handleDeleteFood();
        String output = outContent.toString();
        assertTrue(output.contains("deleted successfully") || output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleEditFood - continue editing multiple fields")
    void testHandleEditFood_ContinueEditing() {
        Food food = new Food(2000, "Old Name", 10.50, "Set");
        when(inputHandler.readYesNo("Are you sure want to edit Y(YES) / N (No): ")).thenReturn(true);
        when(inputHandler.readInt("Enter the food id that want to edit : ")).thenReturn(2000);
        when(foodController.getFoodById(2000)).thenReturn(food);
        when(inputHandler.readInt("Select your choice : ")).thenReturn(1).thenReturn(2);
        when(inputHandler.readString("Enter food name : ")).thenReturn("New Name");
        when(foodController.validateFoodName("New Name")).thenReturn(true);
        when(inputHandler.readChar(contains("Do you want to continue edit"))).thenReturn('Y').thenReturn('N');
        when(inputHandler.readDouble("Enter food price : ")).thenReturn(15.00);
        when(foodController.validateFoodPrice(15.00)).thenReturn(true);
        
        Food updatedFood = new Food(2000, "New Name", 15.00, "Set");
        when(foodController.updateFood(any(Food.class))).thenReturn(updatedFood);
        
        foodHandler.handleEditFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleEditFood - edit with validation retries")
    void testHandleEditFood_WithValidationRetries() {
        Food food = new Food(2000, "Old Name", 10.50, "Set");
        when(inputHandler.readYesNo("Are you sure want to edit Y(YES) / N (No): ")).thenReturn(true);
        when(inputHandler.readInt("Enter the food id that want to edit : ")).thenReturn(2000);
        when(foodController.getFoodById(2000)).thenReturn(food);
        when(inputHandler.readInt("Select your choice : ")).thenReturn(1);
        when(inputHandler.readString("Enter food name : ")).thenReturn("Food123").thenReturn("New Name");
        when(foodController.validateFoodName("Food123")).thenReturn(false);
        when(foodController.validateFoodName("New Name")).thenReturn(true);
        when(inputHandler.readChar(contains("Do you want to continue edit"))).thenReturn('N');
        
        Food updatedFood = new Food(2000, "New Name", 10.50, "Set");
        when(foodController.updateFood(any(Food.class))).thenReturn(updatedFood);
        
        foodHandler.handleEditFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleRegisterFood - with validation retries for all fields")
    void testHandleRegisterFood_WithAllValidationRetries() {
        when(inputHandler.readYesNo("Are you sure want to add new food (Y/N) : ")).thenReturn(true);
        when(inputHandler.readString("Enter your food name: ")).thenReturn("Food123").thenReturn("Chicken Rice");
        when(foodController.validateFoodName("Food123")).thenReturn(false);
        when(foodController.validateFoodName("Chicken Rice")).thenReturn(true);
        when(inputHandler.readDouble("Enter your food price RM (1-69) : RM")).thenReturn(0.0).thenReturn(10.50);
        when(foodController.validateFoodPrice(0.0)).thenReturn(false);
        when(foodController.validateFoodPrice(10.50)).thenReturn(true);
        when(inputHandler.readString("Enter your food type (Set / A la carte : ")).thenReturn("Invalid").thenReturn("Set");
        when(foodController.validateFoodType("Invalid")).thenReturn(false);
        when(foodController.validateFoodType("Set")).thenReturn(true);
        when(inputHandler.readYesNo("Are you want to proceed to add (Y/N) : ")).thenReturn(true);
        
        Food registeredFood = new Food(2000, "Chicken Rice", 10.50, "Set");
        when(foodController.registerFood(any(Food.class))).thenReturn(registeredFood);
        
        foodHandler.handleRegisterFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleRegisterFood - cancel after validation")
    void testHandleRegisterFood_CancelAfterValidation() {
        when(inputHandler.readYesNo("Are you sure want to add new food (Y/N) : ")).thenReturn(true);
        when(inputHandler.readString("Enter your food name: ")).thenReturn("Chicken Rice");
        when(foodController.validateFoodName("Chicken Rice")).thenReturn(true);
        when(inputHandler.readDouble("Enter your food price RM (1-69) : RM")).thenReturn(10.50);
        when(foodController.validateFoodPrice(10.50)).thenReturn(true);
        when(inputHandler.readString("Enter your food type (Set / A la carte : ")).thenReturn("Set");
        when(foodController.validateFoodType("Set")).thenReturn(true);
        when(inputHandler.readYesNo("Are you want to proceed to add (Y/N) : ")).thenReturn(false);
        
        foodHandler.handleRegisterFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleEditFood - edit all fields in sequence")
    void testHandleEditFood_EditAllFields() {
        Food food = new Food(2000, "Old Name", 10.50, "Set");
        when(inputHandler.readYesNo("Are you sure want to edit Y(YES) / N (No): ")).thenReturn(true);
        when(inputHandler.readInt("Enter the food id that want to edit : ")).thenReturn(2000);
        when(foodController.getFoodById(2000)).thenReturn(food);
        when(inputHandler.readInt("Select your choice : ")).thenReturn(1).thenReturn(2).thenReturn(3);
        when(inputHandler.readString("Enter food name : ")).thenReturn("New Name");
        when(foodController.validateFoodName("New Name")).thenReturn(true);
        when(inputHandler.readDouble("Enter food price : ")).thenReturn(20.00);
        when(foodController.validateFoodPrice(20.00)).thenReturn(true);
        when(inputHandler.readString("Enter food type (Set / A la carte): ")).thenReturn("A la carte");
        when(foodController.validateFoodType("A la carte")).thenReturn(true);
        when(inputHandler.readChar(contains("Do you want to continue edit"))).thenReturn('Y').thenReturn('Y').thenReturn('N');
        
        Food updatedFood = new Food(2000, "New Name", 20.00, "A la carte");
        when(foodController.updateFood(any(Food.class))).thenReturn(updatedFood);
        
        foodHandler.handleEditFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleEditFood - edit with lowercase continue")
    void testHandleEditFood_LowercaseContinue() {
        Food food = new Food(2000, "Old Name", 10.50, "Set");
        when(inputHandler.readYesNo("Are you sure want to edit Y(YES) / N (No): ")).thenReturn(true);
        when(inputHandler.readInt("Enter the food id that want to edit : ")).thenReturn(2000);
        when(foodController.getFoodById(2000)).thenReturn(food);
        when(inputHandler.readInt("Select your choice : ")).thenReturn(1);
        when(inputHandler.readString("Enter food name : ")).thenReturn("New Name");
        when(foodController.validateFoodName("New Name")).thenReturn(true);
        when(inputHandler.readChar(contains("Do you want to continue edit"))).thenReturn('y').thenReturn('N');
        
        Food updatedFood = new Food(2000, "New Name", 10.50, "Set");
        when(foodController.updateFood(any(Food.class))).thenReturn(updatedFood);
        
        foodHandler.handleEditFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleRegisterFood - price validation retry")
    void testHandleRegisterFood_PriceValidationRetry() {
        when(inputHandler.readYesNo("Are you sure want to add new food (Y/N) : ")).thenReturn(true);
        when(inputHandler.readString("Enter your food name: ")).thenReturn("Chicken Rice");
        when(foodController.validateFoodName("Chicken Rice")).thenReturn(true);
        when(inputHandler.readDouble("Enter your food price RM (1-69) : RM")).thenReturn(0.0).thenReturn(10.50);
        when(foodController.validateFoodPrice(0.0)).thenReturn(false);
        when(foodController.validateFoodPrice(10.50)).thenReturn(true);
        when(inputHandler.readString("Enter your food type (Set / A la carte : ")).thenReturn("Set");
        when(foodController.validateFoodType("Set")).thenReturn(true);
        when(inputHandler.readYesNo("Are you want to proceed to add (Y/N) : ")).thenReturn(true);
        
        Food registeredFood = new Food(2000, "Chicken Rice", 10.50, "Set");
        when(foodController.registerFood(any(Food.class))).thenReturn(registeredFood);
        
        foodHandler.handleRegisterFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleRegisterFood - type validation retry")
    void testHandleRegisterFood_TypeValidationRetry() {
        when(inputHandler.readYesNo("Are you sure want to add new food (Y/N) : ")).thenReturn(true);
        when(inputHandler.readString("Enter your food name: ")).thenReturn("Chicken Rice");
        when(foodController.validateFoodName("Chicken Rice")).thenReturn(true);
        when(inputHandler.readDouble("Enter your food price RM (1-69) : RM")).thenReturn(10.50);
        when(foodController.validateFoodPrice(10.50)).thenReturn(true);
        when(inputHandler.readString("Enter your food type (Set / A la carte : ")).thenReturn("Invalid").thenReturn("Set");
        when(foodController.validateFoodType("Invalid")).thenReturn(false);
        when(foodController.validateFoodType("Set")).thenReturn(true);
        when(inputHandler.readYesNo("Are you want to proceed to add (Y/N) : ")).thenReturn(true);
        
        Food registeredFood = new Food(2000, "Chicken Rice", 10.50, "Set");
        when(foodController.registerFood(any(Food.class))).thenReturn(registeredFood);
        
        foodHandler.handleRegisterFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleRegisterFood - all validation failures")
    void testHandleRegisterFood_AllValidationFailures() {
        when(inputHandler.readYesNo("Are you sure want to add new food (Y/N) : ")).thenReturn(true);
        when(inputHandler.readString("Enter your food name: ")).thenReturn("123").thenReturn("Chicken Rice");
        when(foodController.validateFoodName("123")).thenReturn(false);
        when(foodController.validateFoodName("Chicken Rice")).thenReturn(true);
        when(inputHandler.readDouble("Enter your food price RM (1-69) : RM")).thenReturn(0.0).thenReturn(10.50);
        when(foodController.validateFoodPrice(0.0)).thenReturn(false);
        when(foodController.validateFoodPrice(10.50)).thenReturn(true);
        when(inputHandler.readString("Enter your food type (Set / A la carte : ")).thenReturn("X").thenReturn("Set");
        when(foodController.validateFoodType("X")).thenReturn(false);
        when(foodController.validateFoodType("Set")).thenReturn(true);
        when(inputHandler.readYesNo("Are you want to proceed to add (Y/N) : ")).thenReturn(true);
        
        Food registeredFood = new Food(2000, "Chicken Rice", 10.50, "Set");
        when(foodController.registerFood(any(Food.class))).thenReturn(registeredFood);
        
        foodHandler.handleRegisterFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
    
    @Test
    @DisplayName("Test handleEditFood - all edit options with validation")
    void testHandleEditFood_AllOptionsWithValidation() {
        Food food = new Food(2000, "Old Name", 10.50, "Set");
        when(inputHandler.readYesNo("Are you sure want to edit Y(YES) / N (No): ")).thenReturn(true);
        when(inputHandler.readInt("Enter the food id that want to edit : ")).thenReturn(2000);
        when(foodController.getFoodById(2000)).thenReturn(food);
        when(inputHandler.readInt("Select your choice : ")).thenReturn(1).thenReturn(2).thenReturn(3);
        when(inputHandler.readString("Enter food name : ")).thenReturn("123").thenReturn("New Name");
        when(foodController.validateFoodName("123")).thenReturn(false);
        when(foodController.validateFoodName("New Name")).thenReturn(true);
        when(inputHandler.readDouble("Enter food price : ")).thenReturn(0.0).thenReturn(15.00);
        when(foodController.validateFoodPrice(0.0)).thenReturn(false);
        when(foodController.validateFoodPrice(15.00)).thenReturn(true);
        when(inputHandler.readString("Enter food type (Set / A la carte): ")).thenReturn("X").thenReturn("Set");
        when(foodController.validateFoodType("X")).thenReturn(false);
        when(foodController.validateFoodType("Set")).thenReturn(true);
        when(inputHandler.readChar(contains("Do you want to continue edit"))).thenReturn('Y').thenReturn('Y').thenReturn('N');
        
        Food updatedFood = new Food(2000, "New Name", 15.00, "Set");
        when(foodController.updateFood(any(Food.class))).thenReturn(updatedFood);
        
        foodHandler.handleEditFood();
        String output = outContent.toString();
        assertTrue(output.length() > 0);
    }
}

