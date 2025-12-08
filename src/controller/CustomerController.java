package controller;

import model.Customer;
import service.interfaces.ICustomerService;

import java.util.Optional;

/**
 * Customer Controller
 * Handles customer-related user interactions
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle
 */
public class CustomerController {
    
    private final ICustomerService customerService;
    
    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }
    
    /**
     * Register a new customer
     * 
     * @param customer Customer to register
     * @return Registered customer if successful, null if validation fails
     */
    public Customer registerCustomer(Customer customer) {
        try {
            return customerService.registerCustomer(customer);
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Login customer
     * 
     * @param customerId Customer ID
     * @param password Password
     * @return Customer if authenticated, null otherwise
     */
    public Customer login(int customerId, String password) {
        Optional<Customer> customerOpt = customerService.login(customerId, password);
        return customerOpt.orElse(null);
    }
    
    /**
     * Validate customer name
     * 
     * @param name Name to validate
     * @return true if valid
     */
    public boolean validateName(String name) {
        return customerService.validateName(name);
    }
    
    /**
     * Validate customer age
     * 
     * @param age Age to validate
     * @return true if valid
     */
    public boolean validateAge(int age) {
        return customerService.validateAge(age);
    }
    
    /**
     * Validate phone number
     * 
     * @param phoneNumber Phone number to validate
     * @return true if valid
     */
    public boolean validatePhoneNumber(String phoneNumber) {
        return customerService.validatePhoneNumber(phoneNumber);
    }
    
    /**
     * Validate gender
     * 
     * @param gender Gender to validate
     * @return true if valid
     */
    public boolean validateGender(String gender) {
        return customerService.validateGender(gender);
    }
    
    /**
     * Validate password
     * 
     * @param password Password to validate
     * @return true if valid
     */
    public boolean validatePassword(String password) {
        return customerService.validatePassword(password);
    }
    
    /**
     * Validate password confirmation
     * 
     * @param password Password
     * @param confirmPassword Confirmation password
     * @return true if matches
     */
    public boolean validatePasswordConfirmation(String password, String confirmPassword) {
        return customerService.validatePasswordConfirmation(password, confirmPassword);
    }
}
