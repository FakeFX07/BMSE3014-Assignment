package repository.interfaces;

import java.util.Optional;

import model.Customer;

public interface ICustomerRepository {
    
    //find customer by ID
    Optional<Customer> findById(int customerId);
    
    //find customer by phone number
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    
    //authenticate customer
    Optional<Customer> authenticate(int customerId, String password);
    
    //save customer
    Customer save(Customer customer);
    
    //get next customer ID
    int getNextCustomerId();
    
    //check phone number exists or not
    boolean existsByPhoneNumber(String phoneNumber);
}
