package repository.impl;

import java.math.BigDecimal;
import java.sql.*;
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
    private static final String FIND_BY_WALLET_ID = "SELECT * FROM payment_methods WHERE wallet_id = ?";
    private static final String FIND_BY_CARD_NUMBER = "SELECT * FROM payment_methods WHERE card_number = ?";
    private static final String AUTH_BY_WALLET_ID = 
            "SELECT * FROM payment_methods WHERE wallet_id = ? AND password = ?";
    private static final String AUTH_BY_CARD_NUMBER = 
            "SELECT * FROM payment_methods WHERE card_number = ? AND password = ?";
    private static final String INSERT = 
            "INSERT INTO payment_methods (password, payment_type, wallet_id, balance, card_number, expiry_date) VALUES (?, ?, ?, ?, ?, ?)";
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
    public Optional<PaymentMethod> findByWalletId(String walletId) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_WALLET_ID)) {
            
            stmt.setString(1, walletId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPaymentMethod(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding payment method by wallet ID: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<PaymentMethod> findByCardNumber(String cardNumber) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_CARD_NUMBER)) {
            
            stmt.setString(1, cardNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPaymentMethod(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding payment method by card number: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<PaymentMethod> authenticateByWalletId(String walletId, String hashedPassword) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AUTH_BY_WALLET_ID)) {
            
            stmt.setString(1, walletId);
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPaymentMethod(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating by wallet ID: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<PaymentMethod> authenticateByCardNumber(String cardNumber, String hashedPassword) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AUTH_BY_CARD_NUMBER)) {
            
            stmt.setString(1, cardNumber);
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPaymentMethod(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating by card number: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public PaymentMethod save(PaymentMethod paymentMethod) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, paymentMethod.getPassword());
            stmt.setString(2, paymentMethod.getPaymentType());
            stmt.setString(3, paymentMethod.getWalletId());
            stmt.setBigDecimal(4, paymentMethod.getBalanceDecimal());
            stmt.setString(5, paymentMethod.getCardNumber());
            stmt.setString(6, paymentMethod.getExpiryDate());
            
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
        paymentMethod.setPassword(rs.getString("password"));
        paymentMethod.setPaymentType(rs.getString("payment_type"));
        paymentMethod.setWalletId(rs.getString("wallet_id"));
        paymentMethod.setBalance(rs.getBigDecimal("balance").doubleValue());
        paymentMethod.setCardNumber(rs.getString("card_number"));
        paymentMethod.setExpiryDate(rs.getString("expiry_date"));
        return paymentMethod;
    }
}
