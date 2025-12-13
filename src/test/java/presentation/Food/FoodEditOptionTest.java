package presentation.Food;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FoodEditOptionTest {
    
    @Test
    @DisplayName("Should define correct codes and labels for UI")
    void shouldHaveCorrectEnumProperties() {
        // verify count
        assertEquals(3, FoodEditOption.values().length);

        // verify properties using assertAll for cleaner failure reporting
        assertAll("Enum Definitions",
            () -> {
                assertEquals(1, FoodEditOption.NAME.getCode());
                assertEquals("Food Name", FoodEditOption.NAME.getLabel());
            },
            () -> {
                assertEquals(2, FoodEditOption.PRICE.getCode());
                assertEquals("Food Price", FoodEditOption.PRICE.getLabel());
            },
            () -> {
                assertEquals(3, FoodEditOption.TYPE.getCode());
                assertEquals("Food Type", FoodEditOption.TYPE.getLabel());
            }
        );
    }
    
    @Test
    @DisplayName("Should map integer codes to correct Enum constants")
    void shouldFindOptionByCode() {
        assertEquals(FoodEditOption.NAME, FoodEditOption.fromCode(1));
        assertEquals(FoodEditOption.PRICE, FoodEditOption.fromCode(2));
        assertEquals(FoodEditOption.TYPE, FoodEditOption.fromCode(3));
    }
    
    @Test
    @DisplayName("Should return null when code does not exist")
    void shouldHandleInvalidLookupCodes() {
        assertNull(FoodEditOption.fromCode(0));
        assertNull(FoodEditOption.fromCode(99));
        assertNull(FoodEditOption.fromCode(-1));
    }
}