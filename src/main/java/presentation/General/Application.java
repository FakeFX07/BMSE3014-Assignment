package presentation.General;

import java.util.List;
import java.util.Scanner;

import controller.CustomerController;
import controller.FoodController;
import controller.OrderController;

import model.Customer;

import presentation.Admin.AdminHandler;
import presentation.Customer.CustomerHandler;
import presentation.Food.FoodHandler;
import presentation.Food.MenuDisplay;
import presentation.Order.OrderHandler;

/**
 * Main Application Class
 * Entry point for the POS system
 * Follows N-Tier Architecture pattern
 */
public class Application {
    
    private final Scanner scanner;
    private final UserInputHandler inputHandler;
    
    // Controllers
    private final CustomerController customerController;
    private final FoodController foodController;
    private final OrderController orderController;
    
    // Handlers
    private final FoodHandler foodHandler;
    private final CustomerHandler customerHandler;
    private final OrderHandler orderHandler;
    private final AdminHandler adminHandler;
    
    Customer currentCustomer; // Package-private for testing
    
    public Application() {
        this.scanner = new Scanner(System.in);
        this.inputHandler = new UserInputHandler(scanner);
        this.customerController = new CustomerController();
        this.foodController = new FoodController();
        this.orderController = new OrderController();
        this.foodHandler = new FoodHandler(foodController, inputHandler);
        this.customerHandler = new CustomerHandler(customerController, inputHandler);
        this.orderHandler = new OrderHandler(foodController, orderController, inputHandler);
        this.adminHandler = new AdminHandler(foodHandler, orderController, inputHandler);
    }
    
    /**
     * Run the application
     */
    public void run() {
        final int EXIT_PASSWORD = 1890;
        int exitPassword = 0;
        
        do {
            MenuDisplay.displayMainMenu();
            int choice = inputHandler.readInt("Your choice : ");
            
            switch (choice) {
                case 1:
                    currentCustomer = customerHandler.handleLogin();
                    break;
                case 2:
                    customerHandler.handleRegister();
                    break;
                case 3:
                    adminHandler.handleAdminMenu(orderHandler, currentCustomer);
                    break;
                case 4:
                    exitPassword = inputHandler.readInt("Enter the correct pin to exit program : ");
                    if (exitPassword == EXIT_PASSWORD) {
                        System.out.println("......Exiting Program......\n");
                    } else {
                        System.out.println("......Unable Exit Program......\n");
                    }
                    break;
                default:
                    System.out.println("Enter Only 1 until 4 !!!\n");
            }
        } while (exitPassword != EXIT_PASSWORD);
        
        scanner.close();
    }

    /**
     * Expose customer login for tests (delegates to handler)
     */
    void handleLogin() {
        customerHandler.handleLogin();
    }

    /**
     * Expose customer registration for tests (delegates to handler)
     */
    void handleRegister() {
        customerHandler.handleRegister();
    }

    /**
     * Expose admin menu for tests (delegates to handler)
     */
    void handleAdminMenu() {
        adminHandler.handleAdminMenu(orderHandler, currentCustomer);
    }

    /**
     * Expose order report for tests (delegates to handler)
     */
    void handleOrderReport() {
        adminHandler.handleOrderReport();
    }

    /**
     * Expose order handling for tests (delegates to handler)
     */
    void handleOrder() {
        orderHandler.handleOrder(currentCustomer);
    }

    /**
     * Expose process order for tests (delegates to handler)
     */
    void processOrder(List<model.OrderDetails> details) {
        orderHandler.processOrder(currentCustomer, details);
    }
}
