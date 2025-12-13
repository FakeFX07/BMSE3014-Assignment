package controller;

import java.util.Optional;

import model.Customer;
import repository.impl.CustomerRepository;
import service.impl.CustomerService;
import service.interfaces.ICustomerService;

public class CustomerController {

    private final ICustomerService customerService;

    public CustomerController() {
        this.customerService = new CustomerService(new CustomerRepository());
    }

    public Customer registerCustomer(Customer customer) {
        try {
            return customerService.registerCustomer(customer);
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return null;
        }
    }

    public Customer login(int customerId, String password) {
        Optional<Customer> customerOpt = customerService.login(customerId, password);
        return customerOpt.orElse(null);
    }

    public boolean validateName(String name) {
        return customerService.validateName(name);
    }

    //Check name validity
    public void checkName(String name) throws IllegalArgumentException {
        customerService.checkName(name);
    }

    public boolean validateAge(int age) {
        return customerService.validateAge(age);
    }

    //Check age validity
    public void checkAge(int age) throws IllegalArgumentException {
        customerService.checkAge(age);
    }

    public boolean validatePhoneNumber(String phoneNumber) {
        return customerService.validatePhoneNumber(phoneNumber);
    }

    //check phone number validity
    public void checkPhoneNumber(String phoneNumber) throws IllegalArgumentException {
        customerService.checkPhoneNumber(phoneNumber);
    }

    public boolean isPhoneNumberRegistered(String phoneNumber) {
        return customerService.isPhoneNumberRegistered(phoneNumber);
    }

    public boolean validateGender(String gender) {
        return customerService.validateGender(gender);
    }

    //check gender validity
    public void checkGender(String gender) throws IllegalArgumentException {
        customerService.checkGender(gender);
    }

    public boolean validatePassword(String password) {
        return customerService.validatePassword(password);
    }

    //Check password validity
    public void checkPassword(String password) throws IllegalArgumentException {
        customerService.checkPassword(password);
    }

    public boolean validatePasswordConfirmation(String password, String confirmPassword) {
        return customerService.validatePasswordConfirmation(password, confirmPassword);
    }

    //Check password confirmation validity
    public void checkPasswordConfirmation(String password, String confirmPassword) throws IllegalArgumentException {
        customerService.checkPasswordConfirmation(password, confirmPassword);
    }
}