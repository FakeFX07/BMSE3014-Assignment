package model;

import java.util.Objects;

/**
 * Customer Model Class
 * Represents a customer entity in the system
 * Follows OOP principles: Encapsulation, Data Hiding
 */
public class Customer {
    
    private int customerId;
    private String name;
    private int age;
    private String phoneNumber;
    private String gender;
    private String password;
    
    // Default constructor
    public Customer() {
    }
    
    // Constructor with ID only (for order processing)
    public Customer(int customerId) {
        this.customerId = customerId;
    }
    
    // Full constructor
    public Customer(int customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }
    
    // Complete constructor
    public Customer(int customerId, String name, int age, String phoneNumber, String gender, String password) {
        this.customerId = customerId;
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.password = password;
    }
    
    // Getters
    public int getCustomerId() {
        return customerId;
    }
    
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public String getGender() {
        return gender;
    }
    
    public String getPassword() {
        return password;
    }
    
    // Setters
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customerId == customer.customerId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
