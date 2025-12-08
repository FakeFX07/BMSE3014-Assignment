package repository.impl;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import config.ConnectionProvider;
import config.DatabaseConnection;
import model.PaymentMethod;
import repository.interfaces.IPaymentMethodRepository;

/**
 * Payment Method Repository Implementation
 * Handles database operations for PaymentMethod entity
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle
 */
public class PaymentMethodRepository implements IPaymentMethodRepository {
    
    private static final String FIND_BY_ID = "SELECT * FROM payment_methods WHERE payment_method_id = ?";
    private static final String FIND_BY_CUSTOMER_ID = "SELECT * FROM payment_methods WHERE customer_id = ?";
    private static final String FIND_BY_CUSTOMER_AND_TYPE = 
            "SELECT * FROM payment_methods WHERE customer_id = ? AND payment_type = ?";
    private static final String INSERT = 
            "INSERT INTO payment_methods (customer_id, payment_type, balance, card_number, expiry_date) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_BALANCE = 
            "UPDATE payment_methods SET balance = ? WHERE payment_method_id = ?";
    
    private final ConnectionProvider connectionProvider;
    
    /**
     * Constructor with ConnectionProvider for dependency injection
     * 
     * @param connectionProvider Connection provider
     */
    public PaymentMethodRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
    
    /**
     * Default constructor using singleton DatabaseConnection
     * Maintains backward compatibility
     */
    public PaymentMethodRepository() {
        this(DatabaseConnection.getInstance());
    }
    
    @Override
    public Optional<PaymentMethod> findById(int paymentMethodId) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {
            
            stmt.setInt(1, paymentMethodId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPaymentMethod(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding payment method by ID: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public List<PaymentMethod> findByCustomerId(int customerId) {
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_CUSTOMER_ID)) {
            
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                paymentMethods.add(mapResultSetToPaymentMethod(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding payment methods by customer ID: " + e.getMessage());
        }
        return paymentMethods;
    }
    
    @Override
    public Optional<PaymentMethod> findByCustomerIdAndType(int customerId, String paymentType) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_CUSTOMER_AND_TYPE)) {
            
            stmt.setInt(1, customerId);
            stmt.setString(2, paymentType);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPaymentMethod(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding payment method by customer and type: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public PaymentMethod save(PaymentMethod paymentMethod) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, paymentMethod.getCustomerId());
            stmt.setString(2, paymentMethod.getPaymentType());
            stmt.setBigDecimal(3, paymentMethod.getBalanceDecimal());
            stmt.setString(4, paymentMethod.getCardNumber());
            stmt.setString(5, paymentMethod.getExpiryDate());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    paymentMethod.setPaymentMethodId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving payment method: " + e.getMessage());
            throw new RuntimeException("Failed to save payment method", e);
        }
        return paymentMethod;
    }
    
    @Override
    public boolean updateBalance(int paymentMethodId, double newBalance) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_BALANCE)) {
            
            stmt.setBigDecimal(1, BigDecimal.valueOf(newBalance));
            stmt.setInt(2, paymentMethodId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment method balance: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Map ResultSet to PaymentMethod object
     * Follows DRY principle - single method for mapping
     */
    private PaymentMethod mapResultSetToPaymentMethod(ResultSet rs) throws SQLException {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodId(rs.getInt("payment_method_id"));
        paymentMethod.setCustomerId(rs.getInt("customer_id"));
        paymentMethod.setPaymentType(rs.getString("payment_type"));
        paymentMethod.setBalance(rs.getBigDecimal("balance").doubleValue());
        paymentMethod.setCardNumber(rs.getString("card_number"));
        paymentMethod.setExpiryDate(rs.getString("expiry_date"));
        return paymentMethod;
    }
}
