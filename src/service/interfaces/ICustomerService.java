package service.interfaces;

import model.Customer;
import java.util.Optional;

/**
 * Customer Service Interface
 * Defines contract for customer business operations
 * Follows SOLID: Interface Segregation Principle, Dependency Inversion Principle
 */
public interface ICustomerService {
    
    /**
     * Register a new customer
     * 
     * @param customer Customer to register
     * @return Registered customer with generated ID
     * @throws IllegalArgumentException if validation fails
     */
    Customer registerCustomer(Customer customer) throws IllegalArgumentException;
    
    /**
     * Authenticate customer login
     * 
     * @param customerId Customer ID
     * @param password Password
     * @return Optional containing customer if authenticated
     */
    Optional<Customer> login(int customerId, String password);
    
    /**
     * Validate customer name
     * 
     * @param name Name to validate
     * @return true if valid, false otherwise
     */
    boolean validateName(String name);
    
    /**
     * Validate customer age
     * 
     * @param age Age to validate
     * @return true if valid, false otherwise
     */
    boolean validateAge(int age);
    
    /**
     * Validate phone number
     * 
     * @param phoneNumber Phone number to validate
     * @return true if valid, false otherwise
     */
    boolean validatePhoneNumber(String phoneNumber);
    
    /**
     * Validate gender
     * 
     * @param gender Gender to validate
     * @return true if valid, false otherwise
     */
    boolean validateGender(String gender);
    
    /**
     * Validate password
     * 
     * @param password Password to validate
     * @return true if valid, false otherwise
     */
    boolean validatePassword(String password);
    
    /**
     * Check if password matches confirmation
     * 
     * @param password Password
     * @param confirmPassword Confirmation password
     * @return true if matches, false otherwise
     */
    boolean validatePasswordConfirmation(String password, String confirmPassword);
}
