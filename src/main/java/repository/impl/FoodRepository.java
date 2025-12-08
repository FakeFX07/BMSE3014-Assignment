package main.java.repository.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import main.java.config.DatabaseConnection;
import main.java.model.Food;
import main.java.repository.interfaces.IFoodRepository;

/**
 * Food Repository Implementation
 * Handles database operations for Food entity
 * Follows SOLID: Single Responsibility Principle, Dependency Inversion Principle
 */
public class FoodRepository implements IFoodRepository {
    
    private static final String FIND_BY_ID = "SELECT * FROM foods WHERE food_id = ?";
    private static final String FIND_ALL = "SELECT * FROM foods ORDER BY food_id";
    private static final String INSERT = "INSERT INTO foods (food_name, food_price, food_type) VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE foods SET food_name = ?, food_price = ?, food_type = ? WHERE food_id = ?";
    private static final String DELETE = "DELETE FROM foods WHERE food_id = ?";
    private static final String GET_MAX_ID = "SELECT MAX(food_id) as max_id FROM foods";
    private static final String EXISTS = "SELECT COUNT(*) FROM foods WHERE food_id = ?";
    
    @Override
    public Optional<Food> findById(int foodId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {
            
            stmt.setInt(1, foodId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToFood(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding food by ID: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public List<Food> findAll() {
        List<Food> foods = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                foods.add(mapResultSetToFood(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all foods: " + e.getMessage());
        }
        return foods;
    }
    
    @Override
    public Food save(Food food) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, food.getFoodName());
            stmt.setBigDecimal(2, food.getFoodPriceDecimal());
            stmt.setString(3, food.getFoodType());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    food.setFoodId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving food: " + e.getMessage());
            throw new RuntimeException("Failed to save food", e);
        }
        return food;
    }
    
    @Override
    public Food update(Food food) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            
            stmt.setString(1, food.getFoodName());
            stmt.setBigDecimal(2, food.getFoodPriceDecimal());
            stmt.setString(3, food.getFoodType());
            stmt.setInt(4, food.getFoodId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating food: " + e.getMessage());
            throw new RuntimeException("Failed to update food", e);
        }
        return food;
    }
    
    @Override
    public boolean deleteById(int foodId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {
            
            stmt.setInt(1, foodId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting food: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public int getNextFoodId() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_MAX_ID);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                return maxId >= 2000 ? maxId + 1 : 2000;
            }
        } catch (SQLException e) {
            System.err.println("Error getting next food ID: " + e.getMessage());
        }
        return 2000;
    }
    
    @Override
    public boolean existsById(int foodId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS)) {
            
            stmt.setInt(1, foodId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking food existence: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Map ResultSet to Food object
     * Follows DRY principle - single method for mapping
     */
    private Food mapResultSetToFood(ResultSet rs) throws SQLException {
        Food food = new Food();
        food.setFoodId(rs.getInt("food_id"));
        food.setFoodName(rs.getString("food_name"));
        food.setFoodPrice(rs.getBigDecimal("food_price").doubleValue());
        food.setFoodType(rs.getString("food_type"));
        return food;
    }
}
