package presentation.Admin;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class AdminMenuOptionTest {

    @Test
    @DisplayName("Test Enum Properties (Getters)")
    void testEnumProperties() {
        //Test FOOD_MANAGEMENT
        assertEquals(1, AdminMenuOption.FOOD_MANAGEMENT.getOptionNumber());
        assertEquals("Food Management", AdminMenuOption.FOOD_MANAGEMENT.getDisplayText());

        //Test ORDER_REPORT
        assertEquals(2, AdminMenuOption.ORDER_REPORT.getOptionNumber());
        assertEquals("Order Report", AdminMenuOption.ORDER_REPORT.getDisplayText());

        //Test BACK_MAIN_MENU
        assertEquals(0, AdminMenuOption.BACK_MAIN_MENU.getOptionNumber());
        assertEquals("Back Main Menu", AdminMenuOption.BACK_MAIN_MENU.getDisplayText());
    }

    @Test
    @DisplayName("Test getByOptionNumber - Valid Inputs")
    void testGetByOptionNumber_Valid() {
        assertEquals(AdminMenuOption.FOOD_MANAGEMENT, AdminMenuOption.getByOptionNumber(1));
        assertEquals(AdminMenuOption.ORDER_REPORT, AdminMenuOption.getByOptionNumber(2));
        assertEquals(AdminMenuOption.BACK_MAIN_MENU, AdminMenuOption.getByOptionNumber(0));
    }

    @Test
    @DisplayName("Test getByOptionNumber - Invalid Inputs")
    void testGetByOptionNumber_Invalid() {
        assertNull(AdminMenuOption.getByOptionNumber(999), "Should return null for non-existent option");
        assertNull(AdminMenuOption.getByOptionNumber(-1), "Should return null for negative option");
    }

    @Test
    @DisplayName("Test displayMenu - Runs without exception")
    void testDisplayMenu() {
        assertDoesNotThrow(() -> AdminMenuOption.displayMenu());
    }

    @Test
    @DisplayName("Test valueOf - Standard Enum method")
    void testValueOf() {
        assertEquals(AdminMenuOption.FOOD_MANAGEMENT, AdminMenuOption.valueOf("FOOD_MANAGEMENT"));
    }
}