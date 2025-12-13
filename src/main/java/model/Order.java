package model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Order {
    
    private int orderId;
    private Date orderDate;
    private Customer customer;
    private List<OrderDetails> orderDetails;
    private BigDecimal totalPrice;
    private PaymentMethod paymentMethod;
    private String status;
    
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
    /**
     * Builder for Order to support fluent construction.
     */
    public static class Builder {
        private final Order o;
        public Builder() { o = new Order(); }
        public Builder orderId(int id) { o.setOrderId(id); return this; }
        public Builder orderDate(Date d) { o.setOrderDate(d); return this; }
        public Builder customer(Customer c) { o.setCustomer(c); return this; }
        public Builder orderDetails(List<OrderDetails> details) { o.setOrderDetails(details); return this; }
        public Builder totalPrice(BigDecimal total) { o.totalPrice = total; return this; }
        public Builder totalPrice(double total) { o.setTotalPrice(total); return this; }
        public Builder paymentMethod(PaymentMethod pm) { o.setPaymentMethod(pm); return this; }
        public Builder status(String s) { o.setStatus(s); return this; }
        public Order build() { return o; }
    }
}
