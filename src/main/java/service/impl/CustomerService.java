package service.impl;

import java.util.Optional;

import model.Customer;
import repository.interfaces.ICustomerRepository;
import service.interfaces.ICustomerService;

/**
 * Customer Service Implementation
 * Contains business logic for customer operations
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle
 */
public class CustomerService implements ICustomerService {
    
    private final ICustomerRepository customerRepository;
    
    // Validation constants
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 79;
    private static final int MIN_PASSWORD_LENGTH = 5;
    private static final int PHONE_MIN_LENGTH = 10;
    private static final int PHONE_MAX_LENGTH = 11;
    
    public CustomerService(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Override
    public Customer registerCustomer(Customer customer) throws IllegalArgumentException {
        // Validate all fields
        if (!validateName(customer.getName())) {
            throw new IllegalArgumentException("Name must contain only letters");
        }
        if (!validateAge(customer.getAge())) {
            throw new IllegalArgumentException("Age must be between " + MIN_AGE + " and " + MAX_AGE);
        }
        if (!validatePhoneNumber(customer.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number must be " + PHONE_MIN_LENGTH + " or " + PHONE_MAX_LENGTH + " digits");
        }
        if (!validateGender(customer.getGender())) {
            throw new IllegalArgumentException("Gender must be 'Male' or 'Female'");
        }
        if (!validatePassword(customer.getPassword())) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        
        // Check if phone number already exists
        if (customerRepository.existsByPhoneNumber(customer.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already registered");
        }
        
        // Generate customer ID
        customer.setCustomerId(customerRepository.getNextCustomerId());
        
        // Save customer
        return customerRepository.save(customer);
    }
    
    @Override
    public Optional<Customer> login(int customerId, String password) {
        return customerRepository.authenticate(customerId, password);
    }
    
    @Override
    public boolean validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        // Check if name contains only letters and spaces
        return name.matches("^[a-zA-Z\\s]+$");
    }
    
    @Override
    public boolean validateAge(int age) {
        return age >= MIN_AGE && age <= MAX_AGE;
    }
    
    @Override
    public boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        int length = phoneNumber.length();
        return length == PHONE_MIN_LENGTH || length == PHONE_MAX_LENGTH;
    }
    
    @Override
    public boolean validateGender(String gender) {
        if (gender == null) {
            return false;
        }
        return "Male".equalsIgnoreCase(gender) || "Female".equalsIgnoreCase(gender);
    }
    
    @Override
    public boolean validatePassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }
    
    @Override
    public boolean validatePasswordConfirmation(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }
}
