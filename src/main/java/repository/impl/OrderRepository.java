package repository.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import config.ConnectionProvider;
import config.DatabaseConnection;
import model.Customer;
import model.Food;
import model.Order;
import model.OrderDetails;
import model.PaymentMethod;
import repository.interfaces.IOrderRepository;

/**
 * Order Repository Implementation
 * Handles database operations for Order entity
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle
 */
public class OrderRepository implements IOrderRepository {
    
    private static final String FIND_BY_ID = "SELECT * FROM orders WHERE order_id = ?";
    private static final String FIND_BY_CUSTOMER_ID = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_date DESC";
    private static final String FIND_ALL = "SELECT * FROM orders ORDER BY order_date DESC";
    private static final String INSERT_ORDER = 
            "INSERT INTO orders (customer_id, total_price, payment_method_id, payment_type, status) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_ORDER_DETAIL = 
            "INSERT INTO order_details (order_id, food_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
    private static final String GET_MAX_ID = "SELECT MAX(order_id) as max_id FROM orders";
    private static final String FIND_ORDER_DETAILS = 
            "SELECT od.*, f.food_name, f.food_price, f.food_type FROM order_details od " +
            "INNER JOIN foods f ON od.food_id = f.food_id WHERE od.order_id = ?";
    
    private final ConnectionProvider connectionProvider;
    
    /**
     * Constructor with ConnectionProvider for dependency injection
     * 
     * @param connectionProvider Connection provider
     */
    public OrderRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
    
    /**
     * Default constructor using singleton DatabaseConnection
     * Maintains backward compatibility
     */
    public OrderRepository() {
        this(DatabaseConnection.getInstance());
    }
    
    @Override
    public Optional<Order> findById(int orderId) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {
            
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setOrderDetails(findOrderDetails(orderId));
                return Optional.of(order);
            }
        } catch (SQLException e) {
            System.err.println("Error finding order by ID: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public List<Order> findByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_CUSTOMER_ID)) {
            
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setOrderDetails(findOrderDetails(order.getOrderId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error finding orders by customer ID: " + e.getMessage());
        }
        return orders;
    }
    
    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setOrderDetails(findOrderDetails(order.getOrderId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all orders: " + e.getMessage());
        }
        return orders;
    }
    
    @Override
    public Order save(Order order) {
        Connection conn = null;
        try {
            conn = connectionProvider.getConnection();
            conn.setAutoCommit(false);
            
            // Insert order
            try (PreparedStatement orderStmt = conn.prepareStatement(INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, order.getCustomer().getCustomerId());
                orderStmt.setBigDecimal(2, order.getTotalPriceDecimal());
                orderStmt.setInt(3, order.getPaymentMethod().getPaymentMethodId());
                orderStmt.setString(4, order.getPaymentMethod().getPaymentType());
                orderStmt.setString(5, order.getStatus());
                
                orderStmt.executeUpdate();
                
                ResultSet generatedKeys = orderStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getInt(1));
                }
            }
            
            // Insert order details
            if (order.getOrderDetails() != null) {
                try (PreparedStatement detailStmt = conn.prepareStatement(INSERT_ORDER_DETAIL)) {
                    for (OrderDetails detail : order.getOrderDetails()) {
                        detailStmt.setInt(1, order.getOrderId());
                        detailStmt.setInt(2, detail.getFood().getFoodId());
                        detailStmt.setInt(3, detail.getQuantity());
                        detailStmt.setBigDecimal(4, detail.getUnitPriceDecimal());
                        detailStmt.setBigDecimal(5, detail.getSubtotalDecimal());
                        detailStmt.addBatch();
                    }
                    detailStmt.executeBatch();
                }
            }
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error saving order: " + e.getMessage());
            throw new RuntimeException("Failed to save order", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit: " + e.getMessage());
                }
            }
        }
        return order;
    }
    
    @Override
    public int getNextOrderId() {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_MAX_ID);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                return maxId >= 1 ? maxId + 1 : 1;
            }
        } catch (SQLException e) {
            System.err.println("Error getting next order ID: " + e.getMessage());
        }
        return 1;
    }
    
    /**
     * Map ResultSet to Order object
     * Follows DRY principle - single method for mapping
     */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setOrderDate(rs.getTimestamp("order_date"));
        
        // Set customer (minimal info)
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        order.setCustomer(customer);
        
        order.setTotalPrice(rs.getBigDecimal("total_price").doubleValue());
        order.setStatus(rs.getString("status"));
        
        // Set payment method (minimal info)
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodId(rs.getInt("payment_method_id"));
        paymentMethod.setPaymentType(rs.getString("payment_type"));
        order.setPaymentMethod(paymentMethod);
        
        return order;
    }
    
    /**
     * Find order details for an order
     */
    private List<OrderDetails> findOrderDetails(int orderId) throws SQLException {
        List<OrderDetails> details = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ORDER_DETAILS)) {
            
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                // Build Food domain object from joined columns
                Food food = new Food();
                food.setFoodId(rs.getInt("food_id"));
                food.setFoodName(rs.getString("food_name"));
                food.setFoodPrice(rs.getBigDecimal("food_price").doubleValue());
                food.setFoodType(rs.getString("food_type"));
                
                // Create OrderDetails
                OrderDetails detail = new OrderDetails(
                    rs.getInt("order_detail_id"),
                    food,
                    rs.getInt("quantity"),
                    rs.getBigDecimal("unit_price")
                );
                details.add(detail);
            }
        }
        return details;
    }
}
