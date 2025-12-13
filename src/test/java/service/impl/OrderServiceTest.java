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

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

//Unit tests for OrderService
 
class OrderServiceTest {

    private OrderService orderService;
    private MockOrderRepository orderRepository;
    private MockCustomerRepository customerRepository;
    private MockPaymentMethodRepository paymentMethodRepository;
    private MockPaymentService paymentService;
    private MockFoodRepository foodRepository;

    @BeforeEach
    void setUp() {
        // Prepare in-memory mock dependencies
        orderRepository = new MockOrderRepository();
        customerRepository = new MockCustomerRepository();
        paymentMethodRepository = new MockPaymentMethodRepository();
        paymentService = new MockPaymentService(paymentMethodRepository);
        foodRepository = new MockFoodRepository();

        // Inject mocks into OrderService
        orderService = new OrderService(
                orderRepository,
                customerRepository,
                paymentMethodRepository,
                paymentService,
                foodRepository
        );
    }

    
    // Price calculation
    

    @Test
    @DisplayName("Total price is calculated correctly for multiple items")
    void calculateTotalPrice_shouldReturnCorrectSum() {
        // Given multiple order items
        Food food1 = new Food(2000, "Food1", 10.0, "Set", 10);
        Food food2 = new Food(2001, "Food2", 15.0, "A la carte", 10);

        List<OrderDetails> details = List.of(
                new OrderDetails(food1, 2), // 20
                new OrderDetails(food2, 1)  // 15
        );

        // When calculating total price
        double total = orderService.calculateTotalPrice(details);

        // Then the sum should be correct
        assertEquals(35.0, total, 0.01);
    }

    
    // Successful order creation
    

    @Test
    @DisplayName("Order is successfully created with valid customer, items and payment")
    void createOrder_shouldSucceed_withValidInput() {
        // Given an existing customer
        Customer customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
        customerRepository.addCustomer(customer);

        // And a valid payment method with sufficient balance
        String hashedPassword = util.PasswordUtil.hashPassword("tng123");
        PaymentMethod pm = new PaymentMethod("TNG001", "TNG", hashedPassword, 100.0);
        pm.setPaymentMethodId(1);
        paymentMethodRepository.addPaymentMethod(pm);

        // And a valid food item
        Food food = new Food(2000, "Food", 10.0, "Set", 10);
        foodRepository.addFood(food);

        List<OrderDetails> details = List.of(new OrderDetails(food, 2));

        // When creating the order
        Order order = orderService.createOrder(1000, details, "TNG", "TNG001", "tng123");

        // Then order should be completed and saved
        assertNotNull(order);
        assertEquals(20.0, order.getTotalPrice(), 0.01);
        assertEquals("COMPLETED", order.getStatus());
        assertEquals(1, orderRepository.findAll().size());
    }

    
    // Validation: customer & order details
    

    @Test
    @DisplayName("Create order fails when customer does not exist")
    void createOrder_shouldFail_whenCustomerNotFound() {
        Food food = new Food(2000, "Food", 10.0, "Set", 10);
        foodRepository.addFood(food);

        List<OrderDetails> details = List.of(new OrderDetails(food, 1));

        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(9999, details, "TNG", "TNG001", "tng123"));
    }

    @Test
    @DisplayName("Create order fails when order details are empty or contain null")
    void createOrder_shouldFail_whenOrderDetailsInvalid() {
        Customer customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
        customerRepository.addCustomer(customer);

        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, new ArrayList<>(), "TNG", "TNG001", "tng123"));

        List<OrderDetails> detailsWithNull = new ArrayList<>();
        detailsWithNull.add(null);

        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, detailsWithNull, "TNG", "TNG001", "tng123"));
    }

    
    // Validation: quantity & subtotal
    

    @Test
    @DisplayName("Create order fails when item quantity is zero or exceeds limit")
    void createOrder_shouldFail_whenQuantityInvalid() {
        Customer customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
        customerRepository.addCustomer(customer);

        Food food = new Food(2000, "Food", 10.0, "Set", 10);
        foodRepository.addFood(food);

        // Quantity = 0
        OrderDetails zeroQty = new OrderDetails(food, 0);
        zeroQty.setUnitPrice(BigDecimal.valueOf(10));
        zeroQty.calculateSubtotal();

        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, List.of(zeroQty), "TNG", "TNG001", "tng123"));

        // Quantity > 100
        OrderDetails tooLargeQty = new OrderDetails(food, 101);
        tooLargeQty.setUnitPrice(BigDecimal.valueOf(10));
        tooLargeQty.calculateSubtotal();

        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, List.of(tooLargeQty), "TNG", "TNG001", "tng123"));
    }

    @Test
    @DisplayName("Create order fails when subtotal does not match unit price × quantity")
    void createOrder_shouldFail_whenSubtotalMismatch() {
        Customer customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
        customerRepository.addCustomer(customer);

        Food food = new Food(2000, "Food", 10.0, "Set", 10);
        foodRepository.addFood(food);

        OrderDetails detail = new OrderDetails(food, 2);
        detail.setUnitPrice(BigDecimal.valueOf(10));
        detail.setSubtotal(BigDecimal.valueOf(25)); // incorrect subtotal

        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, List.of(detail), "TNG", "TNG001", "tng123"));
    }

    @Test
    @DisplayName("Create order fails when subtotal is null")
    void createOrder_shouldFail_whenSubtotalIsNull() {
        Customer customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
        customerRepository.addCustomer(customer);

        String hashedPassword = util.PasswordUtil.hashPassword("tng123");
        PaymentMethod pm = new PaymentMethod("TNG001", "TNG", hashedPassword, 100.0);
        pm.setPaymentMethodId(1);
        paymentMethodRepository.addPaymentMethod(pm);

        Food food = new Food(2000, "Food", 10.0, "Set", 10);
        foodRepository.addFood(food);

        OrderDetails detail = new OrderDetails(food, 1);
        detail.setSubtotal(null);

        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, List.of(detail), "TNG", "TNG001", "tng123"));
    }

    
    // Validation: payment
    

    @Test
    @DisplayName("Create order fails when payment method does not exist")
    void createOrder_shouldFail_whenPaymentMethodNotFound() {
        Customer customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
        customerRepository.addCustomer(customer);

        Food food = new Food(2000, "Food", 10.0, "Set", 10);
        foodRepository.addFood(food);

        OrderDetails detail = new OrderDetails(food, 1);

        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, List.of(detail), "Grab", "GRAB001", "grab456"));
    }

    @Test
    @DisplayName("Create order fails when payment balance is insufficient")
    void createOrder_shouldFail_whenInsufficientBalance() {
        Customer customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
        customerRepository.addCustomer(customer);

        PaymentMethod pm = new PaymentMethod("TNG001", "TNG", "tng123", 5.0);
        pm.setPaymentMethodId(1);
        paymentMethodRepository.addPaymentMethod(pm);

        Food food = new Food(2000, "Food", 10.0, "Set", 10);
        foodRepository.addFood(food);

        OrderDetails detail = new OrderDetails(food, 2);

        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(1000, List.of(detail), "TNG", "TNG001", "tng123"));
    }

   
    // Mock repositories & services (in-memory implementations)
    

   
    private static class MockOrderRepository implements IOrderRepository {
        private final Map<Integer, Order> orders = new HashMap<>();
        private int nextId = 1;

        @Override
        public List<Order> findAll() {
            return new ArrayList<>(orders.values());
        }

        @Override
        public List<Order> findByCustomerId(int customerId) {
            List<Order> result = new ArrayList<>();
            for (Order o : orders.values()) {
                if (o.getCustomer().getCustomerId() == customerId) {
                    result.add(o);
                }
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
        public int getNextOrderId() {
            return nextId;
        }

        @Override
        public Optional<Order> findById(int orderId) {
            return Optional.ofNullable(orders.get(orderId));
        }
    }

    
    //In-memory customer repository for testing

    private static class MockCustomerRepository implements ICustomerRepository {
        private final Map<Integer, Customer> customers = new HashMap<>();

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

        void addCustomer(Customer customer) {
            customers.put(customer.getCustomerId(), customer);
        }
    }
@Test
@DisplayName("Create order fails when food is null")
void createOrder_shouldFail_whenFoodIsNull() {
    Customer customer = new Customer(1000, "John", 25, "0123", "M", "pass");
    customerRepository.addCustomer(customer);

    OrderDetails detail = new OrderDetails(null, 1);
    detail.setUnitPrice(BigDecimal.TEN);
    detail.setSubtotal(BigDecimal.TEN);

    assertThrows(IllegalArgumentException.class, () ->
            orderService.createOrder(1000, List.of(detail), "TNG", "TNG001", "tng123"));
}
@Test
@DisplayName("Create order fails when food does not exist in repository")
void createOrder_shouldFail_whenFoodNotFound() {
    Customer customer = new Customer(1000, "John", 25, "0123", "M", "pass");
    customerRepository.addCustomer(customer);

    Food fakeFood = new Food(9999, "GhostFood", 10.0, "Set", 5);

    OrderDetails detail = new OrderDetails(fakeFood, 1);
    detail.setUnitPrice(BigDecimal.TEN);
    detail.setSubtotal(BigDecimal.TEN);

    assertThrows(IllegalArgumentException.class, () ->
            orderService.createOrder(1000, List.of(detail), "TNG", "TNG001", "tng123"));
}
@Test
@DisplayName("Create order fails when food stock is insufficient")
void createOrder_shouldFail_whenStockInsufficient() {
    Customer customer = new Customer(1000, "John", 25, "0123", "M", "pass");
    customerRepository.addCustomer(customer);

    Food food = new Food(2000, "Food", 10.0, "Set", 1);
    foodRepository.addFood(food);

    OrderDetails detail = new OrderDetails(food, 2);
    detail.setUnitPrice(BigDecimal.TEN);
    detail.setSubtotal(BigDecimal.valueOf(20));

    assertThrows(IllegalArgumentException.class, () ->
            orderService.createOrder(1000, List.of(detail), "TNG", "TNG001", "tng123"));
}
@Test
@DisplayName("Create order succeeds with Bank payment")
void createOrder_shouldSucceed_withBankPayment() {
    Customer customer = new Customer(1000, "John", 25, "0123", "M", "pass");
    customerRepository.addCustomer(customer);

    String hashed = util.PasswordUtil.hashPassword("bank123");
    PaymentMethod bank = new PaymentMethod(null, "Bank", hashed, 200.0);
    bank.setCardNumber("1234567812345678");
    bank.setPaymentMethodId(2);
    paymentMethodRepository.addPaymentMethod(bank);

    Food food = new Food(2000, "Food", 10.0, "Set", 5);
    foodRepository.addFood(food);

    OrderDetails detail = new OrderDetails(food, 2);

    Order order = orderService.createOrder(
            1000,
            List.of(detail),
            "Bank",
            "1234567812345678",
            "bank123"
    );

    assertEquals("COMPLETED", order.getStatus());
}
@Test
@DisplayName("Create order fails when payment throws exception and is wrapped")
void createOrder_shouldFail_whenPaymentThrows() {
    Customer customer = new Customer(1000, "John", 25, "0123", "M", "pass");
    customerRepository.addCustomer(customer);

    Food food = new Food(2000, "Food", 10.0, "Set", 5);
    foodRepository.addFood(food);

    OrderDetails detail = new OrderDetails(food, 1);
    detail.setUnitPrice(BigDecimal.TEN);
    detail.setSubtotal(BigDecimal.TEN);

    assertThrows(IllegalArgumentException.class, () ->
            orderService.createOrder(1000, List.of(detail), "TNG", "NO_SUCH_WALLET", "wrong"));
}
@Test
@DisplayName("Create order fails when food quantity update fails")
void createOrder_shouldFail_whenDecrementQuantityFails() {
    foodRepository = new MockFoodRepository() {
        @Override
        public boolean decrementQuantity(int foodId, int quantityToDeduct) {
            return false;
        }
    };

    orderService = new OrderService(
            orderRepository,
            customerRepository,
            paymentMethodRepository,
            paymentService,
            foodRepository
    );

    Customer customer = new Customer(1000, "John", 25, "0123", "M", "pass");
    customerRepository.addCustomer(customer);

    String hashed = util.PasswordUtil.hashPassword("tng123");
    PaymentMethod pm = new PaymentMethod("TNG001", "TNG", hashed, 100.0);
    pm.setPaymentMethodId(1);
    paymentMethodRepository.addPaymentMethod(pm);

    Food food = new Food(2000, "Food", 10.0, "Set", 5);
    foodRepository.addFood(food);

    OrderDetails detail = new OrderDetails(food, 1);

    assertThrows(IllegalArgumentException.class, () ->
            orderService.createOrder(1000, List.of(detail), "TNG", "TNG001", "tng123"));
}


     // In-memory payment method repository
     
    private static class MockPaymentMethodRepository implements IPaymentMethodRepository {
        private final Map<Integer, PaymentMethod> methods = new HashMap<>();
        private final Map<String, PaymentMethod> byWalletId = new HashMap<>();
        private final Map<String, PaymentMethod> byCardNumber = new HashMap<>();

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
            return (pm != null && pm.getPassword().equals(hashedPassword))
                    ? Optional.of(pm)
                    : Optional.empty();
        }

        @Override
        public Optional<PaymentMethod> authenticateByCardNumber(String cardNumber, String hashedPassword) {
            PaymentMethod pm = byCardNumber.get(cardNumber);
            return (pm != null && pm.getPassword().equals(hashedPassword))
                    ? Optional.of(pm)
                    : Optional.empty();
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
            save(pm);
        }
    }

    
     // Simplified payment service used for testing
    
    private static class MockPaymentService implements IPaymentService {
        private final IPaymentMethodRepository repository;

        MockPaymentService(IPaymentMethodRepository repository) {
            this.repository = repository;
        }

        @Override
        public Payment processPayment(String paymentType, String identifier, String password, double amount) {
            String hashedPassword = util.PasswordUtil.hashPassword(password);

            Optional<PaymentMethod> pmOpt =
                    "Bank".equalsIgnoreCase(paymentType)
                            ? repository.authenticateByCardNumber(identifier, hashedPassword)
                            : repository.authenticateByWalletId(identifier, hashedPassword);

            PaymentMethod pm = pmOpt.orElseThrow(() ->
                    new IllegalArgumentException("Invalid payment credentials"));

            if (pm.getBalance() < amount) {
                throw new IllegalArgumentException("Insufficient balance");
            }

            pm.setBalance(pm.getBalance() - amount);
            repository.updateBalance(pm.getPaymentMethodId(), pm.getBalance());

            return new TNGPayment(pm.getBalance());
        }

        @Override
        public Payment createPayment(PaymentMethod paymentMethod) {
            return new TNGPayment(paymentMethod.getBalance());
        }
    }

    // In-memory food repository for testing
     
    private static class MockFoodRepository implements IFoodRepository {
        private final Map<Integer, Food> foods = new HashMap<>();

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
