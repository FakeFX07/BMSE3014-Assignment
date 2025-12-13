package repository.impl;
// Tests for OrderService

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.*;
import repository.interfaces.ICustomerRepository;
import repository.interfaces.IFoodRepository;
import repository.interfaces.IOrderRepository;
import repository.interfaces.IPaymentMethodRepository;
import service.impl.OrderService;
import service.interfaces.IPaymentService;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Order Service Test

public class OrderServiceTest {
    
    private OrderService orderService;
    private MockOrderRepository orderRepository;
    private MockCustomerRepository customerRepository;
    private MockPaymentMethodRepository paymentMethodRepository;
    private MockPaymentService paymentService;
    private MockFoodRepository foodRepository;
    
    @BeforeEach
    void setUp() {
        orderRepository = new MockOrderRepository();
        customerRepository = new MockCustomerRepository();
        paymentMethodRepository = new MockPaymentMethodRepository();
        paymentService = new MockPaymentService(paymentMethodRepository);
        foodRepository = new MockFoodRepository();
        
        orderService = new OrderService(orderRepository, customerRepository, 
                                       paymentMethodRepository, paymentService, foodRepository);
    }
    
    @Test
    @DisplayName("Test calculateTotalPrice")
    void testCalculateTotalPrice() {
        List<OrderDetails> details = new ArrayList<>();
        Food food1 = new Food(2000, "Food 1", 10.00, "Set");
        Food food2 = new Food(2001, "Food 2", 15.00, "A la carte");
        
        details.add(new OrderDetails(food1, 2));
        details.add(new OrderDetails(food2, 1));
        
        double total = orderService.calculateTotalPrice(details);
        assertEquals(35.00, total, 0.01);
    }
    
    @Test
    @DisplayName("Test createOrder - valid order")
    void testCreateOrder_Valid() {
        Customer customer = new Customer(1000, "John Doe", 25, "0123456789", "Male", "password");
        customerRepository.addCustomer(customer);
        
        // Store password as hashed
        String hashedPassword = util.PasswordUtil.hashPassword("tng123");
        PaymentMethod pm = new PaymentMethod("TNG001", "TNG", hashedPassword, 100.00);
        pm.setPaymentMethodId(1);
        paymentMethodRepository.addPaymentMethod(pm);
        
        List<OrderDetails> details = new ArrayList<>();
        Food food = new Food(2000, "Food 1", 10.00, "Set", 10); // Add quantity
        foodRepository.addFood(food);
        details.add(new OrderDetails(food, 2));
        
        Order order = orderService.createOrder(1000, details, "TNG", "TNG001", "tng123");
        
        assertNotNull(order);
        assertEquals(20.00, order.getTotalPrice(), 0.01);
        assertEquals("COMPLETED", order.getStatus());
    }
    
    @Test
    @DisplayName("Test createOrder - customer not found")
    void testCreateOrder_CustomerNotFound() {
        List<OrderDetails> details = new ArrayList<>();
        Food food = new Food(2000, "Food 1", 10.00, "Set", 10); // Add quantity
        foodRepository.addFood(food);
        details.add(new OrderDetails(food, 1));
        
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(9999, details, "TNG", "TNG001", "tng123");
        });
    }
    
    @Test
    @DisplayName("Test createOrder - empty order details")
    void testCreateOrder_EmptyDetails() {
        Customer customer = new Customer(1000, "John Doe", 25, "0123456789", "Male", "password");
        customerRepository.addCustomer(customer);
        
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(1000, new ArrayList<>(), "TNG", "TNG001", "tng123");
        });
    }

    @Test
    @DisplayName("Test get orders by customer and all orders")
    void testGetOrdersRetrieval() {
        Customer customer = new Customer(1000, "John Doe", 25, "0123456789", "Male", "password");
        customerRepository.addCustomer(customer);

        // Store password as hashed
        String hashedPassword = util.PasswordUtil.hashPassword("tng123");
        PaymentMethod pm = new PaymentMethod("TNG001", "TNG", hashedPassword, 100.00);
        pm.setPaymentMethodId(1);
        paymentMethodRepository.addPaymentMethod(pm);

        List<OrderDetails> details = new ArrayList<>();
        Food food = new Food(2000, "Food 1", 10.00, "Set", 10); // Add quantity
        foodRepository.addFood(food);
        details.add(new OrderDetails(food, 1));

        orderService.createOrder(1000, details, "TNG", "TNG001", "tng123");

        assertEquals(1, orderService.getOrdersByCustomerId(1000).size());
        assertEquals(1, orderService.getAllOrders().size());
    }
    
    // Mock repositories and services
    private static class MockOrderRepository implements IOrderRepository {
        private java.util.Map<Integer, Order> orders = new java.util.HashMap<>();
        private int nextId = 1;
        
        @Override
        public Optional<Order> findById(int orderId) {
            return Optional.ofNullable(orders.get(orderId));
        }
        
        @Override
        public List<Order> findByCustomerId(int customerId) {
            return orders.values().stream()
                    .filter(o -> o.getCustomer().getCustomerId() == customerId)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        @Override
        public List<Order> findAll() {
            return new ArrayList<>(orders.values());
        }
        
        @Override
        public Order save(Order order) {
            order.setOrderId(nextId++);
            orders.put(order.getOrderId(), order);
            return order;
        }
        
        @Override
        public int getNextOrderId() {
            return nextId;
        }
    }
    
    private static class MockCustomerRepository implements ICustomerRepository {
        private java.util.Map<Integer, Customer> customers = new java.util.HashMap<>();
        
        @Override
        public Optional<Customer> findById(int customerId) {
            return Optional.ofNullable(customers.get(customerId));
        }
        
        @Override
        public Optional<Customer> findByPhoneNumber(String phoneNumber) {
            return Optional.empty();
        }
        
        @Override
        public Optional<Customer> authenticate(int customerId, String password) {
            return Optional.empty();
        }
        
        @Override
        public Customer save(Customer customer) {
            customers.put(customer.getCustomerId(), customer);
            return customer;
        }
        
        @Override
        public int getNextCustomerId() {
            return 1000;
        }
        
        @Override
        public boolean existsByPhoneNumber(String phoneNumber) {
            return false;
        }
        
        public void addCustomer(Customer customer) {
            customers.put(customer.getCustomerId(), customer);
        }
    }
    
    private static class MockPaymentMethodRepository implements IPaymentMethodRepository {
        private java.util.Map<Integer, PaymentMethod> paymentMethods = new java.util.HashMap<>();
        private java.util.Map<String, PaymentMethod> byWalletId = new java.util.HashMap<>();
        private java.util.Map<String, PaymentMethod> byCardNumber = new java.util.HashMap<>();
        
        @Override
        public Optional<PaymentMethod> findById(int paymentMethodId) {
            return Optional.ofNullable(paymentMethods.get(paymentMethodId));
        }
        
        @Override
        public Optional<PaymentMethod> findByWalletId(String walletId) {
            return Optional.ofNullable(byWalletId.get(walletId));
        }
        
        @Override
        public Optional<PaymentMethod> findByCardNumber(String cardNumber) {
            return Optional.ofNullable(byCardNumber.get(cardNumber));
        }
        
        @Override
        public Optional<PaymentMethod> authenticateByWalletId(String walletId, String hashedPassword) {
            PaymentMethod pm = byWalletId.get(walletId);
            if (pm != null && pm.getPassword().equals(hashedPassword)) {
                return Optional.of(pm);
            }
            return Optional.empty();
        }
        
        @Override
        public Optional<PaymentMethod> authenticateByCardNumber(String cardNumber, String hashedPassword) {
            PaymentMethod pm = byCardNumber.get(cardNumber);
            if (pm != null && pm.getPassword().equals(hashedPassword)) {
                return Optional.of(pm);
            }
            return Optional.empty();
        }
        
        @Override
        public PaymentMethod save(PaymentMethod paymentMethod) {
            paymentMethods.put(paymentMethod.getPaymentMethodId(), paymentMethod);
            if (paymentMethod.getWalletId() != null) {
                byWalletId.put(paymentMethod.getWalletId(), paymentMethod);
            }
            if (paymentMethod.getCardNumber() != null) {
                byCardNumber.put(paymentMethod.getCardNumber(), paymentMethod);
            }
            return paymentMethod;
        }
        
        @Override
        public boolean updateBalance(int paymentMethodId, double newBalance) {
            PaymentMethod pm = paymentMethods.get(paymentMethodId);
            if (pm != null) {
                pm.setBalance(newBalance);
                return true;
            }
            return false;
        }
        
        public void addPaymentMethod(PaymentMethod pm) {
            paymentMethods.put(pm.getPaymentMethodId(), pm);
            if (pm.getWalletId() != null) {
                byWalletId.put(pm.getWalletId(), pm);
            }
            if (pm.getCardNumber() != null) {
                byCardNumber.put(pm.getCardNumber(), pm);
            }
        }
    }
    
    private static class MockPaymentService implements IPaymentService {
        private IPaymentMethodRepository repository;
        
        public MockPaymentService(IPaymentMethodRepository repository) {
            this.repository = repository;
        }
        
        @Override
        public Payment processPayment(String paymentType, String identifier, String password, double amount) {
            // Hash password as PaymentService does
            String hashedPassword = util.PasswordUtil.hashPassword(password);
            
            // Authenticate and get payment method
            Optional<PaymentMethod> pmOpt;
            if ("Bank".equalsIgnoreCase(paymentType)) {
                pmOpt = repository.authenticateByCardNumber(identifier, hashedPassword);
            } else {
                pmOpt = repository.authenticateByWalletId(identifier, hashedPassword);
            }
            
            if (pmOpt.isEmpty()) {
                throw new IllegalArgumentException("Invalid wallet ID or password");
            }
            
            PaymentMethod pm = pmOpt.get();
            Payment payment = new TNGPayment(pm.getBalance());
            
            if (!payment.checkAmount(amount)) {
                throw new IllegalArgumentException("Insufficient balance");
            }
            
            double newBalance = payment.makePayment(amount);
            repository.updateBalance(pm.getPaymentMethodId(), newBalance);
            
            return payment;
        }
        
        @Override
        public Payment createPayment(PaymentMethod paymentMethod) {
            return new TNGPayment(paymentMethod.getBalance());
        }
    }
    
    private static class MockFoodRepository implements IFoodRepository {
        private java.util.Map<Integer, Food> foods = new java.util.HashMap<>();
        
        @Override
        public Optional<Food> findById(int foodId) {
            return Optional.ofNullable(foods.get(foodId));
        }
        
        @Override
        public Optional<Food> findByName(String foodName) {
            return foods.values().stream()
                    .filter(f -> f.getFoodName().equalsIgnoreCase(foodName))
                    .findFirst();
        }
        
        @Override
        public List<Food> findAll() {
            return new ArrayList<>(foods.values());
        }
        
        @Override
        public Food save(Food food) {
            foods.put(food.getFoodId(), food);
            return food;
        }
        
        @Override
        public Food update(Food food) {
            foods.put(food.getFoodId(), food);
            return food;
        }
        
        @Override
        public boolean deleteById(int foodId) {
            return foods.remove(foodId) != null;
        }
        
        @Override
        public int getNextFoodId() {
            return 2000;
        }
        
        @Override
        public boolean existsById(int foodId) {
            return foods.containsKey(foodId);
        }
        
        @Override
        public boolean existsByName(String foodName) {
            return foods.values().stream()
                    .anyMatch(f -> f.getFoodName().equalsIgnoreCase(foodName));
        }
        
        @Override
        public boolean decrementQuantity(int foodId, int quantityToDeduct) {
            Food food = foods.get(foodId);
            if (food != null && food.getQuantity() >= quantityToDeduct) {
                food.setQuantity(food.getQuantity() - quantityToDeduct);
                return true;
            }
            return false;
        }
        
        public void addFood(Food food) {
            foods.put(food.getFoodId(), food);
        }
    }
}
