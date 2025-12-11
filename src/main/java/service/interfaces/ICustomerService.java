package service.interfaces;

import java.util.Optional;

import model.Customer;

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
     * Check customer name validity
     * 
     * @param name Name to check
     * @throws IllegalArgumentException if name is invalid
     */
    void checkName(String name) throws IllegalArgumentException;
    
    /**
     * Validate customer age
     * 
     * @param age Age to validate
     * @return true if valid, false otherwise
     */
    boolean validateAge(int age);
    
    /**
     * Check customer age validity
     * 
     * @param age Age to check
     * @throws IllegalArgumentException if age is invalid
     */
    void checkAge(int age) throws IllegalArgumentException;
    
    /**
     * Validate phone number
     * 
     * @param phoneNumber Phone number to validate
     * @return true if valid, false otherwise
     */
    boolean validatePhoneNumber(String phoneNumber);
    
    /**
     * Check phone number validity and availability
     * 
     * @param phoneNumber Phone number to check
     * @throws IllegalArgumentException if phone number is invalid or already registered
     */
    void checkPhoneNumber(String phoneNumber) throws IllegalArgumentException;
    
    /**
     * Validate gender
     * 
     * @param gender Gender to validate
     * @return true if valid, false otherwise
     */
    boolean validateGender(String gender);
    
    /**
     * Check gender validity
     * 
     * @param gender Gender to check
     * @throws IllegalArgumentException if gender is invalid
     */
    void checkGender(String gender) throws IllegalArgumentException;
    
    /**
     * Validate password
     * 
     * @param password Password to validate
     * @return true if valid, false otherwise
     */
    boolean validatePassword(String password);
    
    /**
     * Check password validity
     * 
     * @param password Password to check
     * @throws IllegalArgumentException if password is invalid
     */
    void checkPassword(String password) throws IllegalArgumentException;
    
    /**
     * Check if password matches confirmation
     * 
     * @param password Password
     * @param confirmPassword Confirmation password
     * @return true if matches, false otherwise
     */
    boolean validatePasswordConfirmation(String password, String confirmPassword);
    
    /**
     * Check if password matches confirmation
     * 
     * @param password Password
     * @param confirmPassword Confirmation password
     * @throws IllegalArgumentException if passwords don't match
     */
    void checkPasswordConfirmation(String password, String confirmPassword) throws IllegalArgumentException;
    
    /**
     * Check if phone number is already registered
     * 
     * @param phoneNumber Phone number to check
     * @return true if phone number exists in database
     */
    boolean isPhoneNumberRegistered(String phoneNumber);
}