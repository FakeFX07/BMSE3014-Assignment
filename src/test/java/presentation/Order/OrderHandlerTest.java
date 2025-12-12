package presentation.Order;

import controller.FoodController;
import controller.OrderController;
import model.*;

import org.junit.jupiter.api.*;
import org.mockito.*;

import presentation.General.UserInputHandler;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderHandlerTest {

    @Mock
    private FoodController foodController;

    @Mock
    private OrderController orderController;

    @Mock
    private UserInputHandler inputHandler;

    @InjectMocks
    private OrderHandler orderHandler;

    private Customer mockCustomer;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockCustomer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
    }

    /// ---------- MOCK FOOD LIST ----------
    private List<Food> mockFoodList() {
        Food f1 = new Food(2000, "Chicken Rice", 10.50, "Set");
        Food f2 = new Food(3000, "Nasi Lemak", 8.00, "Set");
        return Arrays.asList(f1, f2);
    }

    /// ---------- handleOrder: Customer null ----------
    @Test
    void testHandleOrder_customerNull() {
        orderHandler.handleOrder(null);
        verify(foodController, never()).getAllFoods();
    }

    /// ---------- handleOrder: food list empty ----------
    @Test
    void testHandleOrder_foodListEmpty() {
        when(foodController.getAllFoods()).thenReturn(Collections.emptyList());
        orderHandler.handleOrder(mockCustomer);
        verify(foodController, times(1)).getAllFoods();
    }

    /// ---------- handleOrder: user immediately stops ----------
    @Test
    void testHandleOrder_stopImmediately() {
        when(foodController.getAllFoods()).thenReturn(mockFoodList());
        when(inputHandler.readInt(anyString())).thenReturn(0);

        orderHandler.handleOrder(mockCustomer);

        verify(orderController, never())
                .createOrder(anyInt(), anyList(), any(), any(), any());
    }

    /// ---------- processOrder: invalid payment ----------
    @Test
    void testProcessOrder_invalidPaymentChoice() {
        List<OrderDetails> details = List.of(
                new OrderDetails(mockFoodList().get(0), 1)
        );

        when(inputHandler.readInt(anyString())).thenReturn(99);

        orderHandler.processOrder(mockCustomer, details);

        verify(orderController, never())
                .createOrder(anyInt(), anyList(), any(), any(), any());
    }

    /// ---------- processOrder: empty list ----------
    @Test
    void testProcessOrder_emptyDetails() {
        orderHandler.processOrder(mockCustomer, new ArrayList<>());
        verify(orderController, never())
                .createOrder(anyInt(), anyList(), any(), any(), any());
    }

    /// ---------- displayReceipt: ensure no errors ----------
    @Test
    void testDisplayReceipt_runsWithoutError() {
        OrderDetails detail = new OrderDetails(mockFoodList().get(0), 2);

        PaymentMethod pm = new PaymentMethod(
                "GRAB001",  // wallet ID
                "Grab",     // payment type
                "grab456",  // password
                21.00       // balance
        );
        pm.setPaymentMethodId(30);

        model.Order order = new model.Order.Builder()
        .orderId(1234)
        .orderDate(new Date())
        .customer(mockCustomer)
        .orderDetails(List.of(detail))
        .totalPrice(new BigDecimal("21.00"))
        .paymentMethod(pm)
        .status("COMPLETED")
        .build();


        assertDoesNotThrow(() -> OrderHandler.displayReceipt(order));
    }
}