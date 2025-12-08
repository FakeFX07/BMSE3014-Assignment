package test;

import model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional coverage tests for core domain models and payments.
 * Keeps coverage high without touching I/O heavy layers.
 */
public class ModelCoverageTest {

    @Test
    void tngPaymentFlow() {
        Payment payment = new TNGPayment(30.0);
        assertTrue(payment.checkAmount(10.0));
        assertEquals(20.0, payment.makePayment(10.0), 0.0001);
        assertEquals("TNG", payment.paymentName());
    }

    @Test
    void grabPaymentFlow() {
        Payment payment = new GrabPayment(15.0);
        assertTrue(payment.checkAmount(15.0));
        assertEquals(0.0, payment.makePayment(15.0), 0.0001);
        assertEquals(0.0, payment.getBalance(), 0.0001);
    }

    @Test
    void bankPaymentIncludesFee() {
        BankPayment payment = new BankPayment(50.0);
        assertFalse(payment.checkAmount(49.5)); // needs amount + fee

        assertTrue(payment.checkAmount(20.0));
        assertEquals(29.0, payment.makePayment(20.0), 0.0001);
        assertEquals(1.0, payment.getTransactionFee(), 0.0001);
    }

    @Test
    void orderDetailsRecalculatesTotals() {
        Food food = new Food(10, "Burger", 12.50, "Set");
        OrderDetails details = new OrderDetails(food, 2);

        assertEquals(25.0, details.getSubtotal(), 0.0001);
        assertEquals(12.50, details.getUnitPrice(), 0.0001);
        assertEquals(25.0, details.calculateEachTotalPrice(), 0.0001);

        details.setQuantity(3);
        assertEquals(37.50, details.getSubtotal(), 0.0001);

        details.setUnitPrice(BigDecimal.valueOf(5.0));
        assertEquals(15.0, details.getSubtotal(), 0.0001);

        details.setFood(new Food(11, "Noodles", 8.0, "A la carte"));
        assertEquals(24.0, details.getSubtotal(), 0.0001);
        assertTrue(details.toString().contains("Noodles"));
    }

    @Test
    void orderDetailsHandlesInvalidData() {
        OrderDetails details = new OrderDetails(null, 0);
        details.calculateSubtotal();

        assertEquals(0.0, details.getSubtotal(), 0.0001);
        assertEquals("", details.toString());

        details.setOrderDetailId(101);
        assertEquals(101, details.getOrderDetailId());
    }

    @Test
    void ordersSupportDefaultsAndMutators() {
        Order defaultOrder = new Order();
        assertEquals("PENDING", defaultOrder.getStatus());
        assertNotNull(defaultOrder.getOrderDate());

        Food food = new Food(21, "Rice", 5.0, "Set");
        OrderDetails details = new OrderDetails(food, 4);
        PaymentMethod method = new PaymentMethod(1, 5, "Bank", 100.0, "1234567890123456", "1225");
        Customer customer = new Customer(5, "Alice");

        Order completed = new Order(new Date(), customer, List.of(details), details.getSubtotal(), method);
        assertEquals("COMPLETED", completed.getStatus());
        assertEquals(20.0, completed.getTotalPrice(), 0.0001);

        completed.setStatus("REFUNDED");
        completed.setOrderId(99);
        assertEquals(99, completed.getOrderId());
        assertEquals("REFUNDED", completed.getStatus());
    }

    @Test
    void orderAccessorsAndToString() {
        Order order = new Order();
        OrderDetails details = new OrderDetails(new Food(30, "Pasta", 8.0, "Set"), 1);
        PaymentMethod method = new PaymentMethod(8, 40, "Grab", 12.5, null, null);

        order.setOrderId(5);
        order.setOrderDate(new Date(0));
        order.setCustomer(new Customer(40, "Charlie"));
        order.setOrderDetails(Arrays.asList(details));
        order.setTotalPrice(8.0);
        order.setPaymentMethod(method);

        Order sameId = new Order();
        sameId.setOrderId(5);

        assertEquals(order, sameId);
        assertEquals(order.hashCode(), sameId.hashCode());
        assertEquals(1, order.getOrderDetails().size());
        assertEquals(method, order.getPaymentMethod());
        assertTrue(order.toString().contains("orderId=5"));
    }

    @Test
    void paymentMethodEqualityAndSetters() {
        PaymentMethod method = new PaymentMethod(7, 99, "Grab", 45.0, null, null);
        assertEquals(45.0, method.getBalance(), 0.0001);
        assertEquals("Grab", method.getPaymentType());

        method.setBalance(60.0);
        method.setPaymentType("TNG");
        method.setCardNumber("9999888877776666");
        method.setExpiryDate("0728");

        assertEquals(60.0, method.getBalance(), 0.0001);
        assertEquals("TNG", method.getPaymentType());
        assertEquals("9999888877776666", method.getCardNumber());
        assertEquals("0728", method.getExpiryDate());

        PaymentMethod duplicateId = new PaymentMethod();
        duplicateId.setPaymentMethodId(7);
        assertEquals(method, duplicateId);
        assertEquals(method.hashCode(), duplicateId.hashCode());
    }

    @Test
    void paymentMethodDecimalSupport() {
        PaymentMethod method = new PaymentMethod();
        method.setCustomerId(55);
        method.setPaymentMethodId(12);
        method.setPaymentType("Bank");
        method.setBalanceDecimal(BigDecimal.valueOf(22.22));

        assertEquals(BigDecimal.valueOf(22.22), method.getBalanceDecimal());
        assertTrue(method.toString().contains("paymentMethodId=12"));
    }

    @Test
    void foodModelMutators() {
        Food food = new Food("Soup", 4.5, "A la carte");
        food.setFoodId(33);
        food.setFoodPrice(5.0);
        food.setFoodName("Laksa");
        food.setFoodType("Set");

        assertEquals(33, food.getFoodId());
        assertEquals("Laksa", food.getFoodName());
        assertEquals(5.0, food.getFoodPrice(), 0.0001);
        assertEquals("Set", food.getFoodType());
        assertTrue(food.toString().contains("Laksa"));

        Food sameId = new Food(33, "Anything", 1.0, "Set");
        assertEquals(food, sameId);
    }

    @Test
    void foodDecimalAndToString() {
        Food food = new Food(44, "Curry", 6.75, "Set");
        assertEquals(BigDecimal.valueOf(6.75), food.getFoodPriceDecimal());
        assertTrue(food.toString().contains("Curry"));
    }

    @Test
    void customerModelMutators() {
        Customer customer = new Customer(10, "Bob");
        customer.setAge(28);
        customer.setPhoneNumber("0123456789");
        customer.setGender("Male");
        customer.setPassword("secret");

        assertEquals(10, customer.getCustomerId());
        assertEquals("Bob", customer.getName());
        assertEquals(28, customer.getAge());
        assertEquals("0123456789", customer.getPhoneNumber());
        assertEquals("Male", customer.getGender());
        assertEquals("secret", customer.getPassword());

        Customer sameId = new Customer(10);
        assertEquals(customer, sameId);
        assertEquals(customer.hashCode(), sameId.hashCode());
    }

    @Test
    void customerToStringAndEquality() {
        Customer customer = new Customer(20, "Dana", 22, "0120000000", "Female", "pwd");
        Customer same = new Customer(20, "SomeoneElse");
        Customer different = new Customer(21, "Dana");

        assertEquals(customer, same);
        assertNotEquals(customer, different);
        assertTrue(customer.toString().contains("Dana"));
    }
}

