package presentation.Order;
import controller.FoodController;
import controller.OrderController;

import model.Food;
import model.Order;
import model.OrderDetails;
import model.PaymentMethod;
import model.Customer;


import presentation.General.UserInputHandler;
import presentation.Payment.PaymentOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles order flow and payment selection.
 */
public class OrderHandler {

    private final FoodController foodController;
    private final OrderController orderController;
    private final UserInputHandler inputHandler;
    public OrderHandler(FoodController foodController,
                        OrderController orderController,
                        UserInputHandler inputHandler) {
        this.foodController = foodController;
        this.orderController = orderController;
        this.inputHandler = inputHandler;
    }

    public void handleOrder(Customer currentCustomer) {
    if (currentCustomer == null) {
        System.out.println("Please login first");
        return;
    }

    List<OrderDetails> orderDetailsList = new ArrayList<>();
    Map<Integer, Integer> tempQuantityReductions = new HashMap<>(); // Track temporary quantity reductions
    char newOrder = 'Y';

    while (newOrder == 'Y') {

        // Refresh food list from database
        List<Food> foods = foodController.getAllFoods();
        if (foods.isEmpty()) {
            System.out.println("No food items available");
            return;
        }
        
        // Apply temporary reductions for display
        for (Food food : foods) {
            int reduction = tempQuantityReductions.getOrDefault(food.getFoodId(), 0);
            food.setQuantity(food.getQuantity() - reduction);
        }
        
        // ✅ 改成使用 OrderMenuDisplay
        OrderMenuDisplay.displayOrderMenu(foods);

        int foodChoice = inputHandler.readInt("Choose a food item (1 to " + foods.size() + "): ");

        if (foodChoice == 0) {
            System.out.println("Stop order !!!");
            break;
        }

        if (foodChoice < 1 || foodChoice > foods.size()) {
            System.out.println("Invalid choice. Please enter a valid number.\n");
            continue;
        }

        Food selectedFood = foods.get(foodChoice - 1);
        
        // Check if food is available (considering temporary reductions)
        int availableQty = selectedFood.getQuantity();
        if (availableQty <= 0) {
            System.out.println("\n===== Sorry, " + selectedFood.getFoodName() + " is out of stock! =====\n");
            continue;
        }
        
        int quantity;

        do {
            quantity = inputHandler.readInt("Quantity (Available: " + availableQty + "): ");
            if (quantity <= 0) {
                System.out.println("\nQuantity should be greater than 0 !!!\n");
            } else if (quantity > availableQty) {
                System.out.println("\nQuantity exceeds available stock! Available: " + availableQty + "!!!\n");
            }
        } while (quantity <= 0 || quantity > availableQty);

        if (inputHandler.readYesNo("Are you want to order " + selectedFood.getFoodName() + " x " + quantity + " qty(s) (Y/N): ")) {
            // Check if this food item is already in the cart
            boolean foundExisting = false;
            for (OrderDetails existingDetail : orderDetailsList) {
                if (existingDetail.getFood().getFoodId() == selectedFood.getFoodId()) {
                    // Sum up the quantities
                    int newQuantity = existingDetail.getQuantity() + quantity;
                    existingDetail.setQuantity(newQuantity);
                    // Recalculate subtotal using BigDecimal
                    java.math.BigDecimal unitPrice = existingDetail.getUnitPriceDecimal();
                    java.math.BigDecimal newSubtotal = unitPrice.multiply(java.math.BigDecimal.valueOf(newQuantity));
                    existingDetail.setSubtotal(newSubtotal);
                    System.out.println("\n✅ Updated " + selectedFood.getFoodName() + " quantity to: " + newQuantity + " qty(s)\n");
                    foundExisting = true;
                    break;
                }
            }
            
            if (!foundExisting) {
                // Add new item to cart
                OrderDetails orderDetail = new OrderDetails(selectedFood, quantity);
                orderDetailsList.add(orderDetail);
                System.out.println("\n✅ Order placed for " + selectedFood.getFoodName() + ": " + quantity + " qty(s)\n");
            }
            
            // Update temporary quantity reduction
            tempQuantityReductions.put(selectedFood.getFoodId(), 
                tempQuantityReductions.getOrDefault(selectedFood.getFoodId(), 0) + quantity);
        }

        // Ask user what to do next
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║              What would you like to do next?              ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║  Y = Continue adding items                                ║");
        System.out.println("║  N = Continue to payment                                  ║");
        System.out.println("║  X = Cancel order                                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        String choice = inputHandler.readString("Your choice (Y/N/X): ").trim().toUpperCase();
        
        if ("Y".equals(choice)) {
            // Continue adding more items
            System.out.println("\n>>> Continue adding more items to your order...\n");
            // Loop continues, showing menu again
        } else if ("N".equals(choice)) {
            // User wants to proceed to payment
            if (orderDetailsList.isEmpty()) {
                System.out.println("\n>>> No items in your order. Please add items first.\n");
            } else {
                processOrder(currentCustomer, orderDetailsList);
                orderDetailsList.clear();

                if (inputHandler.readYesNo("Do you want to proceed another order (Y/N) : ")) {
                    newOrder = 'Y';
                } else {
                    System.out.println("Thank You. Please Come Again");
                    newOrder = 'N';
                }
            }
        } else if ("X".equals(choice)) {
            // Cancel current items and continue ordering
            System.out.println("\n>>> Order Cancelled !!!\n");
            orderDetailsList.clear();
            tempQuantityReductions.clear(); // Clear temporary reductions
            // Continue the loop to show menu again (don't set newOrder = 'N')
        } else {
            System.out.println("Invalid choice. Please enter Y, N, or X.");
        }
    }
}


    public void processOrder(Customer currentCustomer, List<OrderDetails> orderDetailsList) {
    if (orderDetailsList.isEmpty()) {
        System.out.println("No items in order");
        return;
    }

    
    OrderMenuDisplay.displayOrderSummary(orderDetailsList);

    // Payment selection
    displayPaymentOptions();
    int paymentChoice = inputHandler.readInt("Please Select a Payment Method :");

    String paymentType = null;
    String identifier = null; // wallet_id or card_number
    String password = null;

    switch (paymentChoice) {
        case 1:
            paymentType = "TNG";
            identifier = inputHandler.readString("Enter your TNG Wallet ID : ");
            password = inputHandler.readPassword("Enter your TNG password : ");
            break;
        case 2:
            paymentType = "Grab";
            identifier = inputHandler.readString("Enter your Grab Wallet ID : ");
            password = inputHandler.readPassword("Enter your Grab password : ");
            break;
        case 3:
            paymentType = "Bank";
            identifier = inputHandler.readString("Enter your card number (16 digit) : ");
            if (identifier == null || identifier.length() != 16) {
                System.out.println("Invalid card number. Payment cancelled.\n");
                return;
            }
            password = inputHandler.readPassword("Enter your card password : ");
            break;
        default:
            System.out.println("Wrong Choice !\n");
            return;
    }

    // Create order with wallet_id/card_number and password
    Order order = orderController.createOrder(
            currentCustomer.getCustomerId(),
            orderDetailsList,
            paymentType,
            identifier,
            password
    );

    if (order != null) {
        displayReceipt(order);
    }
}


        /**
     * Display order receipt
     * 
     * @param order Order to display
     */
    public static void displayReceipt(Order order) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                            🧾 RECEIPT 🧾                                      ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Order ID    : %-65d ║%n", order.getOrderId());
        System.out.printf("║ Date        : %-65s ║%n", order.getOrderDate());
        System.out.printf("║ Customer ID : %-65d ║%n", order.getCustomer().getCustomerId());
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ %-6s │ %-30s │ %10s │ %6s │ %12s ║%n", "ID", "Food Name", "Unit Price", "Qty", "Subtotal");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");
        
        for (OrderDetails detail : order.getOrderDetails()) {
            if (detail != null && detail.getFood() != null) {
                System.out.printf("║ %6d │ %-30s │ RM %7.2f │ %4d │ RM %9.2f ║%n",
                        detail.getFood().getFoodId(),
                        detail.getFood().getFoodName(),
                        detail.getUnitPrice(),
                        detail.getQuantity(),
                        detail.getSubtotal());
            }
        }
        
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ %-75s RM %9.2f ║%n", "SUBTOTAL:", order.getTotalPrice());
        
        PaymentMethod paymentMethod = order.getPaymentMethod();
        System.out.printf("║ %-75s RM %9.2f ║%n", 
                         paymentMethod.getPaymentType() + " Balance:", 
                         paymentMethod.getBalance());
        
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("\n                    Thank you for your order! 🙏\n");
    }

        /**
     * Display payment options
     */
    public static void displayPaymentOptions() {
        PaymentOption.displayMenu();
    }
    
}


