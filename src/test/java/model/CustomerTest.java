package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void testDefaultConstructorAndSetters() {
        Customer c = new Customer();
        
        //Setup data
        c.setCustomerId(1);
        c.setName("John");
        c.setAge(25);
        c.setPhoneNumber("0123456789");
        c.setGender("Male");
        c.setPassword("pass123");

        //Verify values
        assertEquals(1, c.getCustomerId());
        assertEquals("John", c.getName());
        assertEquals(25, c.getAge());
        assertEquals("0123456789", c.getPhoneNumber());
        assertEquals("Male", c.getGender());
        assertEquals("pass123", c.getPassword());
    }

    // Constructor Tests
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

    @Test
    void testHashCode() {
        Customer c1 = new Customer(1, "John");
        Customer c2 = new Customer(1, "John");

        // Consistent objects should have same hash
        assertEquals(c1.hashCode(), c2.hashCode());
        
        // Different objects should ideally have different hash
        Customer c3 = new Customer(2, "Jane");
        assertNotEquals(c1.hashCode(), c3.hashCode());
    }

    @Test
    void testToString() {
        Customer c = new Customer(5, "TestName", 20, "0123", "Male", "pass");
        String result = c.toString();
        
        // Verify key info is present in the string
        assertNotNull(result);
        assertTrue(result.contains("customerId=5"));
        assertTrue(result.contains("name='TestName'"));
        assertTrue(result.contains("phoneNumber='0123'"));
    }
}