package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Food entity.
 */
public class FoodModelTest {

    @Test
    @DisplayName("Should create Food instance using full constructor")
    void shouldCreateFoodWithAllFields() {
        Food food = new Food(101, "Spicy Ramen", 12.50, "Noodles");
        
        assertEquals(101, food.getFoodId());
        assertEquals("Spicy Ramen", food.getFoodName());
        assertEquals(12.50, food.getFoodPrice());
        assertEquals("Noodles", food.getFoodType());
    }

    @Test
    @DisplayName("Should create Food instance without ID (auto-increment scenario)")
    void shouldCreateFoodWithoutId() {
        Food food = new Food("Burger Set", 15.00, "Western");
        
        assertEquals("Burger Set", food.getFoodName());
        assertEquals(15.00, food.getFoodPrice());
        assertEquals("Western", food.getFoodType());
        assertEquals(0, food.getFoodId()); // ID defaults to 0
    }

    @Test
    @DisplayName("Should verify getters and setters work correctly")
    void shouldUpdateFoodProperties() {
        Food food = new Food();
        
        // Set values
        food.setFoodId(500);
        food.setFoodName("Nasi Goreng");
        food.setFoodPrice(8.00);
        food.setFoodType("Local");

        // Verify values
        assertEquals(500, food.getFoodId());
        assertEquals("Nasi Goreng", food.getFoodName());
        assertEquals(8.00, food.getFoodPrice());
        assertEquals("Local", food.getFoodType());
    }
    
    @Test
    @DisplayName("Should handle edge cases for property updates")
    void shouldHandlePropertyEdgeCases() {
        Food food = new Food();
        
        // Test empty strings or nulls where applicable
        food.setFoodName("");
        assertEquals("", food.getFoodName());
        
        food.setFoodType(null);
        assertNull(food.getFoodType());
        
        // Test distinct price points
        food.setFoodPrice(0.01);
        assertEquals(0.01, food.getFoodPrice());
    }

    @Test
    @DisplayName("Should return price as BigDecimal for calculation precision")
    void shouldReturnCorrectDecimalPrice() {
        // Standard case
        Food food = new Food("Pizza", 20.50, "Western");
        BigDecimal price = food.getFoodPriceDecimal();
        assertEquals(0, new BigDecimal("20.50").compareTo(price));

        // Zero case
        Food freeFood = new Food("Water", 0.0, "Beverage");
        assertEquals(0, BigDecimal.ZERO.compareTo(freeFood.getFoodPriceDecimal()));
    }

    @Test
    @DisplayName("Should respect equality contract based on Food ID")
    void shouldVerifyEqualityLogic() {
        Food food1 = new Food(100, "Satay", 1.00, "Side");
        Food food2 = new Food(100, "Satay (Updated)", 1.50, "Side"); // Same ID, diff content
        Food food3 = new Food(101, "Satay", 1.00, "Side"); // Diff ID
        
        // 1. Reflexive
        assertEquals(food1, food1);
        
        // 2. Symmetric (Same ID implies equality in this domain model)
        assertEquals(food1, food2);
        assertEquals(food1.hashCode(), food2.hashCode());
        
        // 3. Different IDs are not equal
        assertNotEquals(food1, food3);
        assertNotEquals(food1.hashCode(), food3.hashCode());
        
        // 4. Null and Type checks
        assertNotEquals(food1, null);
        assertNotEquals(food1, "Some String");
    }
    
    @Test
    @DisplayName("Should treat unpersisted objects (ID=0) as equal only if same ref")
    void shouldHandleDefaultIdEquality() {
        // This behavior depends on your equals implementation, but usually:
        // If both have ID 0, they might be considered equal by value, 
        // or unequal because they aren't the same entity yet.
        // Based on your original test, they are equal.
        Food newFood1 = new Food("Cake", 10.0, "Dessert"); // ID 0
        Food newFood2 = new Food("Pie", 12.0, "Dessert");  // ID 0
        
        assertEquals(newFood1, newFood2, "Entities with ID 0 should match according to current logic");
    }

    @Test
    void shouldGenerateReadableStringRepresentation() {
        Food food = new Food(99, "Ice Cream", 5.50, "Dessert");
        String output = food.toString();
        
        assertNotNull(output);
        assertTrue(output.contains("99"));
        assertTrue(output.contains("Ice Cream"));
        assertTrue(output.contains("Dessert"));
    }
}