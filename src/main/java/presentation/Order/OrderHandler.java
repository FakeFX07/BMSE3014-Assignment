package presentation.Order;

import controller.FoodController;
import controller.OrderController;

import model.Food;
import model.Order;
import model.OrderDetails;
import model.Customer;

import presentation.Food.MenuDisplay;
import presentation.General.UserInputHandler;

import java.util.ArrayList;
import java.util.List;

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

        List<Food> foods = foodController.getAllFoods();
        if (foods.isEmpty()) {
            System.out.println("No food items available");
            return;
        }

        List<OrderDetails> orderDetailsList = new ArrayList<>();
        char newOrder = 'Y';

        while (newOrder == 'Y') {
            MenuDisplay.displayFoodMenu(foods);
            int foodChoice = inputHandler.readInt("Choose a food item (1 to " + foods.size() + "): ");

            if (foodChoice == 0) {
                System.out.println("==========");
                System.out.println("Stop order");
                System.out.println("==========\n");
                break;
            }

            if (foodChoice < 1 || foodChoice > foods.size()) {
                System.out.println("Invalid choice. Please enter a valid number.\n");
                continue;
            }

            Food selectedFood = foods.get(foodChoice - 1);
            int quantity;

            do {
                quantity = inputHandler.readInt("Quantity: ");
                if (quantity <= 0) {
                    System.out.println("===== Quantity should be greater than 0! =====\n");
                }
            } while (quantity <= 0);

            if (inputHandler.readYesNo("Are you want to order " + selectedFood.getFoodName() + " (Y/N): ")) {
                OrderDetails orderDetail = new OrderDetails(selectedFood, quantity);
                orderDetailsList.add(orderDetail);
                System.out.print("Order placed for " + selectedFood.getFoodName() + ": " + quantity + " qty(s) \n");
            }

            if (inputHandler.readYesNo("Complete Ordering ? (Y/N): ")) {
                processOrder(currentCustomer, orderDetailsList);
                orderDetailsList.clear();

                if (inputHandler.readYesNo("Do you want to proceed another order (Y/N) : ")) {
                    newOrder = 'Y';
                } else {
                    System.out.println("Thank You. Please Come Again");
                    newOrder = 'N';
                }
            }
        }
    }

    public void processOrder(Customer currentCustomer, List<OrderDetails> orderDetailsList) {
        if (orderDetailsList.isEmpty()) {
            System.out.println("No items in order");
            return;
        }

        // Display order summary
        System.out.println("============ Double Confirm Item ===========");
        for (OrderDetails detail : orderDetailsList) {
            System.out.println("\nFood Id: " + detail.getFood().getFoodId());
            System.out.println("Name :" + detail.getFood().getFoodName());
            System.out.println("QTY(s): " + detail.getQuantity());
            System.out.println("Per price : " + detail.getFood().getFoodPrice());
            System.out.println("Each Total Price : RM " + String.format("%.2f", detail.getSubtotal()));
        }
        System.out.println("===========================================");

        // Payment selection
        MenuDisplay.displayPaymentOptions();
        int paymentChoice = inputHandler.readInt("Please Select a Payment Method :");

        String paymentType = null;
        String cardNumber = null;
        String expiryDate = null;

        switch (paymentChoice) {
            case 1:
                paymentType = "TNG";
                break;
            case 2:
                paymentType = "Grab";
                break;
            case 3:
                paymentType = "Bank";
                String cardInput;
                do {
                    cardInput = inputHandler.readString("Enter your card number (16 digit) : ");
                    if (cardInput.length() != 16) {
                        System.out.println("Please Enter Card Number Again : ");
                    }
                } while (cardInput.length() != 16);
                cardNumber = cardInput;

                String expiryInput;
                do {
                    expiryInput = inputHandler.readString("Enter expired date (4 digit): ");
                    if (expiryInput.length() != 4) {
                        System.out.println("Please Enter Expiry Date Again : ");
                    }
                } while (expiryInput.length() != 4);
                expiryDate = expiryInput;
                break;
            default:
                System.out.println("Wrong Choice !\n");
                return;
        }

        // Create order
        Order order = orderController.createOrder(currentCustomer.getCustomerId(), orderDetailsList,
                paymentType, cardNumber, expiryDate);

        if (order != null) {
            MenuDisplay.displayReceipt(order);
        }
    }
}

