package model;  

import java.math.BigDecimal;
import java.util.Objects;

/**
 * OrderDetails Model Class
 * Represents order detail (line item) entity
 * Follows OOP principles: Encapsulation, Composition
 */
public class OrderDetails {
    
    private int orderDetailId;
    private Food food;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    
    // Constructor
    public OrderDetails(Food food, int quantity) {
        this.food = food;
        this.quantity = quantity;
        this.unitPrice = food != null ? food.getFoodPriceDecimal() : BigDecimal.ZERO;
        calculateSubtotal();
    }
    
    // Full constructor
    public OrderDetails(int orderDetailId, Food food, int quantity, BigDecimal unitPrice) {
        this.orderDetailId = orderDetailId;
        this.food = food;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }
    
    /**
     * Calculate subtotal for this order detail
     * Follows DRY principle - single method for calculation
     */
    public void calculateSubtotal() {
        if (food != null && quantity > 0) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }
    
    /**
     * Get calculated total price
     * 
     * @return total price as double
     */
    public double calculateEachTotalPrice() {
        return subtotal.doubleValue();
    }
    
    // Getters
    public int getOrderDetailId() {
        return orderDetailId;
    }
    
    public Food getFood() {
        return food;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public double getUnitPrice() {
        return unitPrice.doubleValue();
    }
    
    public BigDecimal getUnitPriceDecimal() {
        return unitPrice;
    }
    
    public double getSubtotal() {
        return subtotal.doubleValue();
    }
    
    public BigDecimal getSubtotalDecimal() {
        return subtotal;
    }
    
    // Setters
    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }
    
    public void setFood(Food food) {
        this.food = food;
        if (food != null) {
            this.unitPrice = food.getFoodPriceDecimal();
            calculateSubtotal();
        }
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetails that = (OrderDetails) o;
        return orderDetailId == that.orderDetailId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderDetailId);
    }
    
    @Override
    public String toString() {
        if (food == null || quantity < 1) {
            return "";
        }
        return String.format("%d\t %s\t\t %.2f\t %d\t\t %.2f",
                food.getFoodId(),
                food.getFoodName(),
                unitPrice.doubleValue(),
                quantity,
                subtotal.doubleValue());
    }
}
