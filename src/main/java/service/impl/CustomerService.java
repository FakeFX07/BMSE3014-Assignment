package service.impl;

import java.util.Optional;

import model.Customer;
import repository.interfaces.ICustomerRepository;
import service.interfaces.ICustomerService;

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
        checkName(customer.getName());
        checkAge(customer.getAge());
        checkPhoneNumber(customer.getPhoneNumber()); 
        checkGender(customer.getGender());
        checkPassword(customer.getPassword());
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
        return name.matches("^[a-zA-Z\\s]+$");
    }

    @Override
    public void checkName(String name) throws IllegalArgumentException {
        if (!validateName(name)) {
            throw new IllegalArgumentException("Name must contain only letters and spaces!");
        }
    }

    @Override
    public boolean validateAge(int age) {
        return age >= MIN_AGE && age <= MAX_AGE;
    }

    @Override
    public void checkAge(int age) throws IllegalArgumentException {
        if (!validateAge(age)) {
            throw new IllegalArgumentException("Age must be between " + MIN_AGE + " and " + MAX_AGE + "!");
        }
    }

    @Override
    public boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        return phoneNumber.matches("^01\\d{8,9}$");
    }

    @Override
    public void checkPhoneNumber(String phoneNumber) throws IllegalArgumentException {
        if (!validatePhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Phone number invalid! Must start with 01 and be " + PHONE_MIN_LENGTH + " to " + PHONE_MAX_LENGTH + " digits.");
        }
        
        if (customerRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Phone number already registered! Please use a different number.");
        }
    }

    @Override
    public boolean validateGender(String gender) {
        if (gender == null) {
            return false;
        }
        return "Male".equalsIgnoreCase(gender) || "Female".equalsIgnoreCase(gender);
    }

    @Override
    public void checkGender(String gender) throws IllegalArgumentException {
        if (!validateGender(gender)) {
            throw new IllegalArgumentException("Gender must be 'Male' or 'Female'!");
        }
    }

    @Override
    public boolean validatePassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }

    @Override
    public void checkPassword(String password) throws IllegalArgumentException {
        if (!validatePassword(password)) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters!");
        }
    }

    @Override
    public boolean validatePasswordConfirmation(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }

    @Override
    public void checkPasswordConfirmation(String password, String confirmPassword) throws IllegalArgumentException {
        if (!validatePasswordConfirmation(password, confirmPassword)) {
            throw new IllegalArgumentException("Password and Confirm Password must be the same!");
        }
    }

    @Override
    public boolean isPhoneNumberRegistered(String phoneNumber) {
        return customerRepository.existsByPhoneNumber(phoneNumber);
    }
}