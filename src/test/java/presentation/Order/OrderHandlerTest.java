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
import static org.mockito.ArgumentMatchers.eq;
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

    /// ---------- handleOrder: Add item + finish order ----------
    @Test
    void testHandleOrder_addItemAndCompleteOrder() {
        when(foodController.getAllFoods()).thenReturn(mockFoodList());

        when(inputHandler.readInt(anyString()))
                .thenReturn(1)  // food index
                .thenReturn(2); // quantity

        when(inputHandler.readYesNo(anyString()))
                .thenReturn(true)   // confirm item
                .thenReturn(true)   // complete order
                .thenReturn(false); // no new order

        OrderDetails detail = new OrderDetails(mockFoodList().get(0), 2);

        PaymentMethod pm = new PaymentMethod(
                10,        // paymentId
                "TNG",     // method type
                21.00      // balance/paid
        );

        model.Order order = new model.Order.Builder()
        .orderId(1234)
        .orderDate(new Date())
        .customer(mockCustomer)
        .orderDetails(List.of(detail))
        .totalPrice(new BigDecimal("21.00"))
        .paymentMethod(pm)
        .status("COMPLETED")
        .build();


        when(orderController.createOrder(anyInt(), anyList(), eq("Bank"), any(), any()))
        .thenReturn(order);


        when(inputHandler.readInt("Please Select a Payment Method :"))
                .thenReturn(1);

        orderHandler.handleOrder(mockCustomer);

        verify(orderController, times(1))
                .createOrder(anyInt(), anyList(), anyString(), any(), any());
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

    /// ---------- processOrder: bank payment w/ validation ----------
    @Test
    void testProcessOrder_bankPaymentWithValidation() {
        List<OrderDetails> details = List.of(
                new OrderDetails(mockFoodList().get(0), 1)
        );

        when(inputHandler.readInt(anyString())).thenReturn(3); // choose Bank

        // card number input retry - use anyString() matcher to handle any call
        when(inputHandler.readString(anyString()))
                .thenReturn("123")  // first call - invalid length
                .thenReturn("1234567890123456")  // second call - valid length
                .thenReturn("12")  // first expiry call - invalid length
                .thenReturn("0528");  // second expiry call - valid length

        PaymentMethod pm = new PaymentMethod(
                20,
                "Bank",
                10.50
        );

        model.Order order = new model.Order.Builder()
        .orderId(1234)
        .orderDate(new Date())
        .customer(mockCustomer)
        .orderDetails(List.of(details.get(0)))
        .totalPrice(new BigDecimal("21.00"))
        .paymentMethod(pm)
        .status("COMPLETED")
        .build();



        when(orderController.createOrder(anyInt(), anyList(), eq("Bank"), any(), any()))
        .thenReturn(order);


        orderHandler.processOrder(mockCustomer, details);

        verify(orderController, times(1))
                .createOrder(anyInt(), anyList(), eq("Bank"),
                        eq("1234567890123456"), eq("0528"));
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
                30,
                "Grab",
                21.00
        );

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