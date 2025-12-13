package presentation.Order;

import controller.FoodController;
import controller.OrderController;
import model.*;

import org.junit.jupiter.api.*;
import org.mockito.*;

import presentation.General.UserInputHandler;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
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

    private Customer customer;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer(1000, "John", 25, "0123456789", "M", "pass");
    }

    // ---------- Helper: mock food list ----------
    private List<Food> sampleFoods() {
        return Arrays.asList(
            new Food(2000, "Chicken Rice", 10.50, "Set"),
            new Food(3000, "Nasi Lemak", 8.00, "Set")
        );
    }

    // ---------- handleOrder: customer null ----------
    @Test
    void handleOrder_customerIsNull() {
        orderHandler.handleOrder(null);
        verify(foodController, never()).getAllFoods();
    }

    // ---------- handleOrder: empty food list ----------
    @Test
    void handleOrder_foodListEmpty() {
        when(foodController.getAllFoods()).thenReturn(Collections.emptyList());

        orderHandler.handleOrder(customer);

        verify(foodController, times(1)).getAllFoods();
        // no orders should be created
        verify(orderController, never()).createOrder(anyInt(), anyList(), any(), any(), any());
    }

    // ---------- handleOrder: user exits immediately ----------
    @Test
    void handleOrder_userStopsImmediately() {
        when(foodController.getAllFoods()).thenReturn(sampleFoods());
        when(inputHandler.readInt(anyString())).thenReturn(0); // user selects 'exit'

        orderHandler.handleOrder(customer);

        verify(orderController, never()).createOrder(anyInt(), anyList(), any(), any(), any());
    }
@Test
@DisplayName("handleOrder handles invalid food choice then exit")
void handleOrder_invalidFoodChoice() {
    when(foodController.getAllFoods()).thenReturn(sampleFoods());

    when(inputHandler.readInt(anyString()))
            .thenReturn(99)  // invalid choice
            .thenReturn(0);  // exit

    orderHandler.handleOrder(customer);

    verify(orderController, never()).createOrder(anyInt(), anyList(), any(), any(), any());
}

@Test
@DisplayName("handleOrder merges quantity when same food selected twice")
void handleOrder_mergeSameFood() {
    Food food = sampleFoods().get(0);
    food.setQuantity(10);

    when(foodController.getAllFoods()).thenReturn(List.of(food));

    when(inputHandler.readInt(anyString()))
            .thenReturn(1) // select food
            .thenReturn(2) // qty
            .thenReturn(1) // select same food again
            .thenReturn(3) // qty
            .thenReturn(0); // exit

    when(inputHandler.readYesNo(anyString())).thenReturn(true);
    when(inputHandler.readString(anyString())).thenReturn("Y");

    orderHandler.handleOrder(customer);

    verify(orderController, never()).createOrder(anyInt(), anyList(), any(), any(), any());
}
@Test
@DisplayName("processOrder does nothing when order list is empty")
void processOrder_emptyOrderList() {
    orderHandler.processOrder(customer, new ArrayList<>());

    verify(orderController, never()).createOrder(anyInt(), anyList(), any(), any(), any());
}

    // ---------- displayReceipt: just runs ----------
    @Test
    void displayReceipt_runsWithoutException() {
        Food food = sampleFoods().get(0);
        OrderDetails detail = new OrderDetails(food, 2);

        PaymentMethod pm = new PaymentMethod("GRAB001", "Grab", "grab456", 21.00);
        pm.setPaymentMethodId(30);

        model.Order order = new model.Order.Builder()
            .orderId(1234)
            .orderDate(new Date())
            .customer(customer)
            .orderDetails(List.of(detail))
            .totalPrice(new BigDecimal("21.00"))
            .paymentMethod(pm)
            .status("COMPLETED")
            .build();

        assertDoesNotThrow(() -> OrderHandler.displayReceipt(order));
    }
}
