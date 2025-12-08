package model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Order Model Class
 * Represents an order entity in the system
 * Follows OOP principles: Encapsulation, Composition
 */
public class Order {
    
    private int orderId;
    private Date orderDate;
    private Customer customer;
    private List<OrderDetails> orderDetails;
    private BigDecimal totalPrice;
    private PaymentMethod paymentMethod;
    private String status;
    
    // Default constructor
    public Order() {
        this.orderDate = new Date();
        this.status = "PENDING";
    }
    
    // Constructor for creating new order
    public Order(Date orderDate, Customer customer, List<OrderDetails> orderDetails, 
                 double totalPrice, PaymentMethod paymentMethod) {
        this.orderDate = orderDate != null ? orderDate : new Date();
        this.customer = customer;
        this.orderDetails = orderDetails;
        this.totalPrice = BigDecimal.valueOf(totalPrice);
        this.paymentMethod = paymentMethod;
        this.status = "COMPLETED";
    }
    
    // Getters
    public int getOrderId() {
        return orderId;
    }
    
    public Date getOrderDate() {
        return orderDate;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public List<OrderDetails> getOrderDetails() {
        return orderDetails;
    }
    
    public double getTotalPrice() {
        return totalPrice.doubleValue();
    }
    
    public BigDecimal getTotalPriceDecimal() {
        return totalPrice;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public String getStatus() {
        return status;
    }
    
    // Setters
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public void setOrderDetails(List<OrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
    }
    
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = BigDecimal.valueOf(totalPrice);
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId == order.orderId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", customer=" + customer +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                '}';
    }
}
