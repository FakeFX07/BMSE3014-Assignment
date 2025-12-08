package repository.interfaces;

import java.util.Optional;

import model.Customer;

/**
 * Customer Repository Interface
 * Defines contract for customer data access operations
 * Follows SOLID: Interface Segregation Principle, Dependency Inversion Principle
 */
public interface ICustomerRepository {
    
    /**
     * Find customer by ID
     * 
     * @param customerId Customer ID
     * @return Optional containing customer if found
     */
    Optional<Customer> findById(int customerId);
    
    /**
     * Find customer by phone number
     * 
     * @param phoneNumber Phone number
     * @return Optional containing customer if found
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    
    /**
     * Authenticate customer with ID and password
     * 
     * @param customerId Customer ID
     * @param password Password
     * @return Optional containing customer if authenticated
     */
    Optional<Customer> authenticate(int customerId, String password);
    
    /**
     * Save customer (create or update)
     * 
     * @param customer Customer to save
     * @return Saved customer with generated ID
     */
    Customer save(Customer customer);
    
    /**
     * Get next available customer ID
     * 
     * @return Next customer ID
     */
    int getNextCustomerId();
    
    /**
     * Check if phone number exists
     * 
     * @param phoneNumber Phone number to check
     * @return true if exists, false otherwise
     */
    boolean existsByPhoneNumber(String phoneNumber);
}
