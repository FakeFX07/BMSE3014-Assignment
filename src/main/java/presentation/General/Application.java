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

<<<<<<< Updated upstream
import repository.impl.FoodRepository;
import service.impl.FoodService;
import service.interfaces.IFoodService;
=======
// --- IMPORTS FOR ADMIN DB CONNECTION ---
import repository.impl.AdminRepository;
import repository.interfaces.IAdminRepository;

// ---------------------------------------
>>>>>>> Stashed changes

/**
 * Main Application Class
 * Entry point for the POS system
 * Follows N-Tier Architecture pattern
 */
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
<<<<<<< Updated upstream
        
        // Wire dependencies following N-layered architecture
        // Repository → Service → Controller
        IFoodService foodService = new FoodService(new FoodRepository());
        this.foodController = new FoodController(foodService);
        
=======

        // 1. Initialize Controllers
        // (Assuming these manage their own services internally as per your request)
>>>>>>> Stashed changes
        this.customerController = new CustomerController();
        this.orderController = new OrderController();
<<<<<<< Updated upstream
        
=======
        this.adminController = new AdminController();

        // 2. Initialize Admin Service Stack (REQUIRED for DB Login)
        IAdminRepository adminRepository = new AdminRepository();

        // 3. Initialize Handlers
>>>>>>> Stashed changes
        this.foodHandler = new FoodHandler(foodController, inputHandler);
        this.customerHandler = new CustomerHandler(customerController, inputHandler);
        this.orderHandler = new OrderHandler(foodController, orderController, inputHandler);
        
        // 4. Initialize AdminHandler with the AdminService
        this.adminHandler = new AdminHandler(adminController, foodHandler, orderController, inputHandler);
    }
        
    
    /**
     * Run the application
     */
    public void run() {
        boolean isRunning = true; // Control flag for the loop
        
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
                    if (currentCustomer != null && inputHandler.readYesNo("Do you want to start Order (Y/N) : ")) {
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
                    System.out.println("\n=========================");
                    System.out.println("[]   Exiting Program   []");
                    System.out.println("=========================\n");
                    isRunning = false; // Stop the loop
                    break;
                default:
                    System.out.println("Enter Only 1 until 4 !!!\n");
            }
        } while (isRunning);
        
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