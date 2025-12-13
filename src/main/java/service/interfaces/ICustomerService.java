package service.interfaces;

import java.util.Optional;

import model.Customer;

public interface ICustomerService {
    
    //register new customer
    Customer registerCustomer(Customer customer) throws IllegalArgumentException;
    
    // Authenticate customer login
    Optional<Customer> login(int customerId, String password);
    
    //validate name
    boolean validateName(String name);
    
    //check customer name validity
    void checkName(String name) throws IllegalArgumentException;

    //validate age
    boolean validateAge(int age);
    
    //Check age validity
    void checkAge(int age) throws IllegalArgumentException;

    //validate phone number
    boolean validatePhoneNumber(String phoneNumber);
    
    //check phone number validity
    void checkPhoneNumber(String phoneNumber) throws IllegalArgumentException;
    
    //validate gender
    boolean validateGender(String gender);
    
    //check gender validity
    void checkGender(String gender) throws IllegalArgumentException;
    
    //validate password
    boolean validatePassword(String password);
    
    //Check password validity
    void checkPassword(String password) throws IllegalArgumentException;
    

    //check password match confirmation password or not
    boolean validatePasswordConfirmation(String password, String confirmPassword);
    
    void checkPasswordConfirmation(String password, String confirmPassword) throws IllegalArgumentException;
    
    //Check phone number is already registered or not
    boolean isPhoneNumberRegistered(String phoneNumber);
}