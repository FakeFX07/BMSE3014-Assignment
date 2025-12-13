package presentation.General;

import java.util.List;
import java.util.Scanner;

// Controllers
import controller.AdminController;
import controller.CustomerController;
import controller.FoodController;
import controller.OrderController;

// Models
import model.Customer;

// Handlers
import presentation.Admin.AdminHandler;
import presentation.Customer.CustomerHandler;
import presentation.Food.FoodHandler;
import presentation.Food.MenuDisplay;
import presentation.Order.OrderHandler;

public class Application {
    
    private final Scanner scanner;
    private final UserInputHandler inputHandler;
    
    // Controllers
    private final AdminController adminController;
    private final CustomerController customerController;
    private final FoodController foodController;
    private final OrderController orderController;
    
    // Handlers
    private final FoodHandler foodHandler;
    private final CustomerHandler customerHandler;
    private final OrderHandler orderHandler;
    private final AdminHandler adminHandler;
    
    Customer currentCustomer;
    
    public Application() {
        this.scanner = new Scanner(System.in);
        this.inputHandler = new UserInputHandler(scanner);
        
        // Initialize controllers
        this.foodController = new FoodController();
        this.customerController = new CustomerController();
        this.orderController = new OrderController();
        this.adminController = new AdminController();
        
        // Initialize handlers
        this.foodHandler = new FoodHandler(foodController, inputHandler);
        this.customerHandler = new CustomerHandler(customerController, inputHandler);
        this.orderHandler = new OrderHandler(foodController, orderController, inputHandler);
        this.adminHandler = new AdminHandler(adminController, foodHandler, orderController, inputHandler);
    }
        

    public void run() {
        boolean isRunning = true; 
        
        do {
            MenuDisplay.displayMainMenu();
            int choice = inputHandler.readInt("Your choice : ");
            MainMenuOption option = MainMenuOption.fromCode(choice);
            
            if (option == null) {
                System.out.println("Enter Only 1 until 4 !!!\n");
                continue;
            }
            
            switch (option) {
                case LOGIN:
                    currentCustomer = customerHandler.handleLogin();
                    if (currentCustomer != null) {
                        orderHandler.handleOrder(currentCustomer);
                    }
                    break;
                case REGISTER:
                    customerHandler.handleRegister();
                    break;
                case ADMIN:
                    adminHandler.handleAdminMenu(orderHandler, currentCustomer);
                    break;
                case EXIT:
                    System.out.println("Exiting Program....");
                    isRunning = false; 
                    break;
                default:
                    System.out.println("Enter Only 1 until 4 !!!\n");
            }
        } while (isRunning);
        
        scanner.close();
    }

    // Expore handling for testing
    void handleLogin() {
        customerHandler.handleLogin();
    }

    // Expore handling for testing
    void handleRegister() {
        customerHandler.handleRegister();
    }

    // Expore handling for testing
    void handleAdminMenu() {
        adminHandler.handleAdminMenu(orderHandler, currentCustomer);
    }

    // Expore handling for testing
    void handleOrderReport() {
        adminHandler.handleOrderReport();
    }

    // Expore handling for testing
    void handleOrder() {
        orderHandler.handleOrder(currentCustomer);
    }

    // Expore handling for testing
    void processOrder(List<model.OrderDetails> details) {
        orderHandler.processOrder(currentCustomer, details);
    }
}