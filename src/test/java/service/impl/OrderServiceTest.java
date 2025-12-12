package service.impl;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.interfaces.ICustomerRepository;
import repository.interfaces.IFoodRepository;
import repository.interfaces.IOrderRepository;
import repository.interfaces.IPaymentMethodRepository;
import service.interfaces.IPaymentService;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTestCoverage {

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

        orderService = new OrderService(orderRepository, customerRepository, paymentMethodRepository, paymentService, foodRepository);
    }
@Test
@DisplayName("createOrder handles null subtotal correctly")
void testCreateOrderNullSubtotal() {
    Customer customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
    customerRepository.addCustomer(customer);

    // Store password as hashed
    String hashedPassword = util.PasswordUtil.hashPassword("tng123");
    PaymentMethod pm = new PaymentMethod("TNG001", "TNG", hashedPassword, 100.0);
    pm.setPaymentMethodId(1);
    paymentMethodRepository.addPaymentMethod(pm);

    Food food = new Food(2000, "Food", 10.0, "Set", 10); // Add quantity
    foodRepository.addFood(food);
    OrderDetails detail = new OrderDetails(food, 1);
    detail.setSubtotal(null);  // force null subtotal

        // This should throw subtotal mismatch
        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, List.of(detail), "TNG", "TNG001", "tng123"));
}

    @Test
    @DisplayName("calculateTotalPrice returns correct total")
    void testCalculateTotalPrice() {
        List<OrderDetails> details = new ArrayList<>();
        Food food1 = new Food(2000, "Food1", 10.0, "Set", 10);
        Food food2 = new Food(2001, "Food2", 15.0, "A la carte", 10);

        details.add(new OrderDetails(food1, 2)); // 20
        details.add(new OrderDetails(food2, 1)); // 15

        double total = orderService.calculateTotalPrice(details);
        assertEquals(35.0, total, 0.01);
    }

    @Test
    @DisplayName("createOrder succeeds with valid input")
    void testCreateOrderValid() {
        Customer customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
        customerRepository.addCustomer(customer);

        // Store password as hashed
        String hashedPassword = util.PasswordUtil.hashPassword("tng123");
        PaymentMethod pm = new PaymentMethod("TNG001", "TNG", hashedPassword, 100.0);
        pm.setPaymentMethodId(1);
        paymentMethodRepository.addPaymentMethod(pm);

        Food food = new Food(2000, "Food", 10.0, "Set", 10);
        foodRepository.addFood(food);
        List<OrderDetails> details = List.of(new OrderDetails(food, 2));

        Order order = orderService.createOrder(1000, details, "TNG", "TNG001", "tng123");

        assertNotNull(order);
        assertEquals(20.0, order.getTotalPrice(), 0.01);
        assertEquals("COMPLETED", order.getStatus());
        assertEquals(1, orderRepository.findAll().size());
    }

    @Test
    @DisplayName("createOrder throws if customer not found")
    void testCreateOrderCustomerNotFound() {
        Food food = new Food(2000, "Food", 10.0, "Set", 10);
        foodRepository.addFood(food);
        List<OrderDetails> details = List.of(new OrderDetails(food, 1));
        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(9999, details, "TNG", "TNG001", "tng123"));
    }

    @Test
    @DisplayName("createOrder throws if orderDetails null or empty")
    void testCreateOrderEmptyDetails() {
        Customer customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
        customerRepository.addCustomer(customer);

        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, new ArrayList<>(), "TNG", "TNG001", "tng123"));

        List<OrderDetails> details = new ArrayList<>();
        details.add(null);
        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, details, "TNG", "TNG001", "tng123"));
    }

    @Test
    @DisplayName("createOrder throws if quantity <= 0 or > 100")
    void testCreateOrderInvalidQuantity() {
        Customer customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
        customerRepository.addCustomer(customer);

        Food food = new Food(2000, "Food", 10.0, "Set", 10);
        foodRepository.addFood(food);
        OrderDetails zeroQty = new OrderDetails(food, 0);
        zeroQty.setUnitPrice(BigDecimal.valueOf(10));
        zeroQty.calculateSubtotal();
        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, List.of(zeroQty), "TNG", "TNG001", "tng123"));

        OrderDetails tooLarge = new OrderDetails(food, 101);
        tooLarge.setUnitPrice(BigDecimal.valueOf(10));
        tooLarge.calculateSubtotal();
        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, List.of(tooLarge), "TNG", "TNG001", "tng123"));
    }

    @Test
    @DisplayName("createOrder throws if subtotal mismatch")
    void testCreateOrderSubtotalMismatch() {
        Customer customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
        customerRepository.addCustomer(customer);

        Food food = new Food(2000, "Food", 10.0, "Set", 10);
        foodRepository.addFood(food);
        OrderDetails detail = new OrderDetails(food, 2);
        detail.setUnitPrice(BigDecimal.valueOf(10));
        detail.setSubtotal(BigDecimal.valueOf(25)); // mismatch
        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, List.of(detail), "TNG", "TNG001", "tng123"));
    }

    @Test
    @DisplayName("createOrder throws if payment method not found")
    void testCreateOrderPaymentMethodNotFound() {
        Customer customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
        customerRepository.addCustomer(customer);

        Food food = new Food(2000, "Food", 10.0, "Set", 10);
        foodRepository.addFood(food);
        OrderDetails detail = new OrderDetails(food, 1);

        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, List.of(detail), "Grab", "GRAB001", "grab456"));
    }

    @Test
    @DisplayName("createOrder throws if insufficient balance")
    void testCreateOrderInsufficientBalance() {
        Customer customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
        customerRepository.addCustomer(customer);

        PaymentMethod pm = new PaymentMethod("TNG001", "TNG", "tng123", 5.0);
        pm.setPaymentMethodId(1);
        pm.setPaymentMethodId(1);
        paymentMethodRepository.addPaymentMethod(pm);

        Food food = new Food(2000, "Food", 10.0, "Set", 10);
        foodRepository.addFood(food);
        OrderDetails detail = new OrderDetails(food, 2);

        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, List.of(detail), "TNG", "TNG001", "tng123"));
    }

    // ------------------------
    // Mock repositories & services
    // ------------------------

    private static class MockOrderRepository implements IOrderRepository {
        private final java.util.Map<Integer, Order> orders = new java.util.HashMap<>();
        private int nextId = 1;

        @Override
        public List<Order> findAll() { return new ArrayList<>(orders.values()); }

        @Override
        public List<Order> findByCustomerId(int customerId) {
            List<Order> result = new ArrayList<>();
            for (Order o : orders.values()) {
                if (o.getCustomer().getCustomerId() == customerId)
                    result.add(o);
            }
            return result;
        }

        @Override
        public Order save(Order order) {
            order.setOrderId(nextId++);
            orders.put(order.getOrderId(), order);
            return order;
        }

        @Override
        public int getNextOrderId() { return nextId; }

        @Override
        public Optional<Order> findById(int orderId) { return Optional.ofNullable(orders.get(orderId)); }
    }

    private static class MockCustomerRepository implements ICustomerRepository {
        private final java.util.Map<Integer, Customer> customers = new java.util.HashMap<>();

        @Override
        public Optional<Customer> findById(int customerId) { return Optional.ofNullable(customers.get(customerId)); }

        @Override
        public Optional<Customer> findByPhoneNumber(String phoneNumber) { return Optional.empty(); }

        @Override
        public Optional<Customer> authenticate(int customerId, String password) { return Optional.empty(); }

        @Override
        public Customer save(Customer customer) { customers.put(customer.getCustomerId(), customer); return customer; }

        @Override
        public int getNextCustomerId() { return 1000; }

        @Override
        public boolean existsByPhoneNumber(String phoneNumber) { return false; }

        void addCustomer(Customer customer) { customers.put(customer.getCustomerId(), customer); }
    }

    private static class MockPaymentMethodRepository implements IPaymentMethodRepository {
        private final java.util.Map<Integer, PaymentMethod> methods = new java.util.HashMap<>();
        private final java.util.Map<String, PaymentMethod> byWalletId = new java.util.HashMap<>();
        private final java.util.Map<String, PaymentMethod> byCardNumber = new java.util.HashMap<>();

        @Override
        public Optional<PaymentMethod> findById(int paymentMethodId) { 
            return Optional.ofNullable(methods.get(paymentMethodId)); 
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
            methods.put(paymentMethod.getPaymentMethodId(), paymentMethod);
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
            PaymentMethod pm = methods.get(paymentMethodId);
            if (pm != null) {
                pm.setBalance(newBalance);
                return true;
            }
            return false;
        }

        void addPaymentMethod(PaymentMethod pm) { 
            methods.put(pm.getPaymentMethodId(), pm);
            if (pm.getWalletId() != null) {
                byWalletId.put(pm.getWalletId(), pm);
            }
            if (pm.getCardNumber() != null) {
                byCardNumber.put(pm.getCardNumber(), pm);
            }
        }
    }

    private static class MockPaymentService implements IPaymentService {
        private final IPaymentMethodRepository repository;

        MockPaymentService(IPaymentMethodRepository repository) { this.repository = repository; }

        @Override
        public Payment processPayment(String paymentType, String identifier, String password, double amount) {
            // Hash password as PaymentService does
            String hashedPassword = util.PasswordUtil.hashPassword(password);
            
            Optional<PaymentMethod> pmOpt;
            if ("Bank".equalsIgnoreCase(paymentType)) {
                pmOpt = repository.authenticateByCardNumber(identifier, hashedPassword);
            } else {
                pmOpt = repository.authenticateByWalletId(identifier, hashedPassword);
            }
            
            PaymentMethod pm = pmOpt.orElseThrow(() -> new IllegalArgumentException("Invalid wallet ID or password"));

            if (pm.getBalance() < amount) throw new IllegalArgumentException("Insufficient balance");
            pm.setBalance(pm.getBalance() - amount);
            repository.updateBalance(pm.getPaymentMethodId(), pm.getBalance());

            return new TNGPayment(pm.getBalance());
        }

        @Override
        public Payment createPayment(PaymentMethod paymentMethod) { return new TNGPayment(paymentMethod.getBalance()); }
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
        
        void addFood(Food food) {
            foods.put(food.getFoodId(), food);
        }
    }
}
