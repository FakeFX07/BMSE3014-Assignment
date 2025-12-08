package main.java.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Food Model Class
 * Represents a food item entity in the system
 * Follows OOP principles: Encapsulation, Data Hiding
 */
public class Food {
    
    private int foodId;
    private String foodName;
    private BigDecimal foodPrice;
    private String foodType;
    
    // Default constructor
    public Food() {
    }
    
    // Constructor without ID (for registration)
    public Food(String foodName, double foodPrice, String foodType) {
        this.foodName = foodName;
        this.foodPrice = BigDecimal.valueOf(foodPrice);
        this.foodType = foodType;
    }
    
    // Full constructor
    public Food(int foodId, String foodName, double foodPrice, String foodType) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodPrice = BigDecimal.valueOf(foodPrice);
        this.foodType = foodType;
    }
    
    // Getters
    public int getFoodId() {
        return foodId;
    }
    
    public String getFoodName() {
        return foodName;
    }
    
    public double getFoodPrice() {
        return foodPrice.doubleValue();
    }
    
    public BigDecimal getFoodPriceDecimal() {
        return foodPrice;
    }
    
    public String getFoodType() {
        return foodType;
    }
    
    // Setters
    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }
    
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
    
    public void setFoodPrice(double foodPrice) {
        this.foodPrice = BigDecimal.valueOf(foodPrice);
    }
    
    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Food food = (Food) o;
        return foodId == food.foodId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(foodId);
    }
    
    @Override
    public String toString() {
        return "Food{" +
                "foodId=" + foodId +
                ", foodName='" + foodName + '\'' +
                ", foodPrice=" + foodPrice +
                ", foodType='" + foodType + '\'' +
                '}';
    }
}
