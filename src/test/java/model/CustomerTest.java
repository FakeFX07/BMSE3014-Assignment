package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CustomerTest {

    // ==========================================
    // 1. Test All Constructors & Getters/Setters
    // ==========================================

    @Test
    void testDefaultConstructorAndSetters() {
        Customer c = new Customer();
        
        // Test Setters
        c.setCustomerId(1);
        c.setName("John");
        c.setAge(25);
        c.setPhoneNumber("0123456789");
        c.setGender("Male");
        c.setPassword("pass123");

        // Test Getters
        assertEquals(1, c.getCustomerId());
        assertEquals("John", c.getName());
        assertEquals(25, c.getAge());
        assertEquals("0123456789", c.getPhoneNumber());
        assertEquals("Male", c.getGender());
        assertEquals("pass123", c.getPassword());
    }

    @Test
    void testConstructorWithId() {
        Customer c = new Customer(10);
        assertEquals(10, c.getCustomerId());
    }

    @Test
    void testConstructorWithIdAndName() {
        Customer c = new Customer(20, "Alice");
        assertEquals(20, c.getCustomerId());
        assertEquals("Alice", c.getName());
    }

    @Test
    void testFullConstructor() {
        Customer c = new Customer(30, "Bob", 40, "011222333", "Male", "secret");
        
        assertEquals(30, c.getCustomerId());
        assertEquals("Bob", c.getName());
        assertEquals(40, c.getAge());
        assertEquals("011222333", c.getPhoneNumber());
        assertEquals("Male", c.getGender());
        assertEquals("secret", c.getPassword());
    }

    // ==========================================
    // 2. Test equals() Logic (Crucial for Coverage)
    // ==========================================

    @Test
    void testEquals() {
        Customer c1 = new Customer(1, "John");
        Customer c2 = new Customer(1, "John"); // Same ID as c1
        Customer c3 = new Customer(2, "Jane"); // Different ID

        // 1. Test comparison with itself (this == o)
        assertTrue(c1.equals(c1));

        // 2. Test comparison with null
        assertFalse(c1.equals(null));

        // 3. Test comparison with different class
        assertFalse(c1.equals("Some String"));

        // 4. Test comparison with same ID (Should be true based on your code)
        assertTrue(c1.equals(c2));

        // 5. Test comparison with different ID
        assertFalse(c1.equals(c3));
    }

    // ==========================================
    // 3. Test hashCode()
    // ==========================================

    @Test
    void testHashCode() {
        Customer c1 = new Customer(1, "John");
        Customer c2 = new Customer(1, "John");

        // Objects that are equal must have the same hash code
        assertEquals(c1.hashCode(), c2.hashCode());
        
        // Different objects usually have different hash codes
        Customer c3 = new Customer(2, "Jane");
        assertNotEquals(c1.hashCode(), c3.hashCode());
    }

    // ==========================================
    // 4. Test toString()
    // ==========================================

    @Test
    void testToString() {
        Customer c = new Customer(5, "TestName", 20, "0123", "Male", "pass");
        String result = c.toString();
        
        // Ensure the string contains key information
        assertNotNull(result);
        assertTrue(result.contains("customerId=5"));
        assertTrue(result.contains("name='TestName'"));
        assertTrue(result.contains("phoneNumber='0123'"));
    }
}