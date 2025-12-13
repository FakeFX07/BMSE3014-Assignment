package presentation.Food;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FoodManagementOptionTest {
    
    @Test
    @DisplayName("Should define correct menu codes and labels")
    void shouldHaveCorrectEnumDefinitions() {
        assertEquals(5, FoodManagementOption.values().length);

        assertAll("Menu Configuration",
            () -> {
                assertEquals(1, FoodManagementOption.REGISTER_FOOD.getCode());
                assertEquals("Register New Food", FoodManagementOption.REGISTER_FOOD.getLabel());
            },
            () -> {
                assertEquals(2, FoodManagementOption.EDIT_FOOD.getCode());
                assertEquals("Edit Food", FoodManagementOption.EDIT_FOOD.getLabel());
            },
            () -> {
                assertEquals(3, FoodManagementOption.DELETE_FOOD.getCode());
                assertEquals("Delete Food", FoodManagementOption.DELETE_FOOD.getLabel());
            },
            () -> {
                assertEquals(4, FoodManagementOption.VIEW_ALL_FOOD.getCode());
                assertEquals("View All Food", FoodManagementOption.VIEW_ALL_FOOD.getLabel());
            },
            () -> {
                assertEquals(0, FoodManagementOption.EXIT.getCode());
                assertEquals("Back to Admin Menu", FoodManagementOption.EXIT.getLabel());
            }
        );
    }
    
    @Test
    @DisplayName("Should correctly map integer inputs to Enum constants")
    void shouldFindOptionByCode() {
        assertEquals(FoodManagementOption.REGISTER_FOOD, FoodManagementOption.fromCode(1));
        assertEquals(FoodManagementOption.EDIT_FOOD, FoodManagementOption.fromCode(2));
        assertEquals(FoodManagementOption.DELETE_FOOD, FoodManagementOption.fromCode(3));
        assertEquals(FoodManagementOption.VIEW_ALL_FOOD, FoodManagementOption.fromCode(4));
        assertEquals(FoodManagementOption.EXIT, FoodManagementOption.fromCode(0));
    }
    
    @Test
    @DisplayName("Should return null for invalid menu codes")
    void shouldHandleInvalidCodes() {
        assertNull(FoodManagementOption.fromCode(99));
        assertNull(FoodManagementOption.fromCode(-1));
        assertNull(FoodManagementOption.fromCode(10));
    }
}