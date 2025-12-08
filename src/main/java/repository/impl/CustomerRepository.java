package repository.impl;

import java.sql.*;
import java.util.Optional;

import config.ConnectionProvider;
import config.DatabaseConnection;
import model.Customer;
import repository.interfaces.ICustomerRepository;

/**
 * Customer Repository Implementation
 * Handles database operations for Customer entity
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle
 */
public class CustomerRepository implements ICustomerRepository {
    
    private static final String FIND_BY_ID = "SELECT * FROM customers WHERE customer_id = ?";
    private static final String FIND_BY_PHONE = "SELECT * FROM customers WHERE phone_number = ?";
    private static final String AUTHENTICATE = "SELECT * FROM customers WHERE customer_id = ? AND password = ?";
    private static final String INSERT = "INSERT INTO customers (name, age, phone_number, gender, password) VALUES (?, ?, ?, ?, ?)";
    private static final String GET_MAX_ID = "SELECT MAX(customer_id) as max_id FROM customers";
    private static final String EXISTS_BY_PHONE = "SELECT COUNT(*) FROM customers WHERE phone_number = ?";
    
    private final ConnectionProvider connectionProvider;
    
    /**
     * Constructor with ConnectionProvider for dependency injection
     * 
     * @param connectionProvider Connection provider
     */
    public CustomerRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
    
    /**
     * Default constructor using singleton DatabaseConnection
     * Maintains backward compatibility
     */
    public CustomerRepository() {
        this(DatabaseConnection.getInstance());
    }
    
    @Override
    public Optional<Customer> findById(int customerId) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {
            
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding customer by ID: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_PHONE)) {
            
            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding customer by phone: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<Customer> authenticate(int customerId, String password) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AUTHENTICATE)) {
            
            stmt.setInt(1, customerId);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating customer: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public Customer save(Customer customer) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, customer.getName());
            stmt.setInt(2, customer.getAge());
            stmt.setString(3, customer.getPhoneNumber());
            stmt.setString(4, customer.getGender());
            stmt.setString(5, customer.getPassword());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    customer.setCustomerId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving customer: " + e.getMessage());
            throw new RuntimeException("Failed to save customer", e);
        }
        return customer;
    }
    
    @Override
    public int getNextCustomerId() {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_MAX_ID);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                return maxId >= 1000 ? maxId + 1 : 1000;
            }
        } catch (SQLException e) {
            System.err.println("Error getting next customer ID: " + e.getMessage());
        }
        return 1000;
    }
    
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_PHONE)) {
            
            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking phone number existence: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Map ResultSet to Customer object
     * Follows DRY principle - single method for mapping
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setName(rs.getString("name"));
        customer.setAge(rs.getInt("age"));
        customer.setPhoneNumber(rs.getString("phone_number"));
        customer.setGender(rs.getString("gender"));
        customer.setPassword(rs.getString("password"));
        return customer;
    }
}
