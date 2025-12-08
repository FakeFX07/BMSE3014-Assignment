package presentation;

import controller.*;
import model.*;

import repository.impl.CustomerRepository;
import repository.impl.FoodRepository;
import repository.impl.OrderRepository;
import repository.impl.PaymentMethodRepository;
import repository.interfaces.ICustomerRepository;
import repository.interfaces.IFoodRepository;
import repository.interfaces.IOrderRepository;
import repository.interfaces.IPaymentMethodRepository;

import service.impl.CustomerService;
import service.impl.FoodService;
import service.impl.OrderService;
import service.impl.PaymentService;
import service.interfaces.ICustomerService;
import service.interfaces.IFoodService;
import service.interfaces.IOrderService;
import service.interfaces.IPaymentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    private Customer currentCustomer;
    
    public Application() {
        this.scanner = new Scanner(System.in);
        this.inputHandler = new UserInputHandler(scanner);
        
        // Initialize repositories
        ICustomerRepository customerRepository = new CustomerRepository();
        IFoodRepository foodRepository = new FoodRepository();
        IPaymentMethodRepository paymentMethodRepository = new PaymentMethodRepository();
        IOrderRepository orderRepository = new OrderRepository();
        
        // Initialize services
        ICustomerService customerService = new CustomerService(customerRepository);
        IFoodService foodService = new FoodService(foodRepository);
        IPaymentService paymentService = new PaymentService(paymentMethodRepository);
        IOrderService orderService = new OrderService(orderRepository, customerRepository, 
                                                      paymentMethodRepository, paymentService);
        
        // Initialize controllers
        this.customerController = new CustomerController(customerService);
        this.foodController = new FoodController(foodService);
        this.orderController = new OrderController(orderService);
        new PaymentController(paymentService);
    }
    
    /**
     * Main method - entry point
     */
    public static void main(String[] args) {
        Application app = new Application();
        app.run();
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
                    handleLogin();
                    break;
                case 2:
                    handleRegister();
                    break;
                case 3:
                    handleAdminMenu();
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
     * Handle customer login
     */
    private void handleLogin() {
        System.out.println("================================================================================");
        System.out.println("[] Hi,dearly customer please enter your customer id and password before Login []");
        System.out.println("================================================================================\n");
        
        int customerId = inputHandler.readInt("Customer id : ");
        String password = inputHandler.readString("Password : ");
        
        Customer customer = customerController.login(customerId, password);
        
        if (customer != null) {
            this.currentCustomer = customer;
            System.out.println("[]=========================================[]");
            System.out.println("\t\tWelcome " + customer.getName());
            System.out.println("\tHave a great time placing an order");
            System.out.println("[]=========================================[]");
            
            if (inputHandler.readYesNo("Do you want to start Order (Y/N) : ")) {
                handleOrder();
            }
        } else {
            System.out.println("Wrong id or password");
        }
    }
    
    /**
     * Handle customer registration
     */
    private void handleRegister() {
        if (!inputHandler.readYesNo("Are u confirm want to register ? (Y/N) : ")) {
            System.out.println("....Quit From Register....\n");
            return;
        }
        
        System.out.println("==========================--=");
        System.out.println("[]      Enter Details      []");
        System.out.println("==========================--=");
        
        Customer customer = new Customer();
        
        // Name validation
        String name;
        do {
            name = inputHandler.readString("Enter your name: ");
            if (!customerController.validateName(name)) {
                System.out.println("Enter letters only!\n");
            }
        } while (!customerController.validateName(name));
        customer.setName(name);
        
        // Age validation
        int age;
        do {
            age = inputHandler.readInt("Enter your age (between 18 and 79): ");
            if (!customerController.validateAge(age)) {
                System.out.println("Enter in the range of 18 to 79 only !!!\n");
            }
        } while (!customerController.validateAge(age));
        customer.setAge(age);
        
        // Phone number validation
        String phoneNumber;
        do {
            phoneNumber = inputHandler.readString("Enter your phone number : ");
            if (!customerController.validatePhoneNumber(phoneNumber)) {
                System.out.println("Please Follow Malaysia Format 10 or 11 digit number !!!\n");
            }
        } while (!customerController.validatePhoneNumber(phoneNumber));
        customer.setPhoneNumber(phoneNumber);
        
        // Gender validation
        String gender;
        do {
            gender = inputHandler.readString("Enter your gender (Male or Female): ");
            if (!customerController.validateGender(gender)) {
                System.out.println("Only enter Male and Female!!!\n");
            }
        } while (!customerController.validateGender(gender));
        customer.setGender(gender);
        
        // Password validation
        String password;
        do {
            password = inputHandler.readString("Enter your password : ");
            if (!customerController.validatePassword(password)) {
                System.out.println("Please Enter At Least Five Character or Digit\n");
            }
        } while (!customerController.validatePassword(password));
        
        // Password confirmation
        String confirmPassword;
        do {
            confirmPassword = inputHandler.readString("Confirm your password again : ");
            if (!customerController.validatePasswordConfirmation(password, confirmPassword)) {
                System.out.println("Password and confirm password must be same\n");
            }
        } while (!customerController.validatePasswordConfirmation(password, confirmPassword));
        customer.setPassword(password);
        
        // Final confirmation
        if (inputHandler.readYesNo("Are you sure want to register (Y/N) :")) {
            Customer registeredCustomer = customerController.registerCustomer(customer);
            if (registeredCustomer != null) {
                System.out.println("\n======================\n");
                System.out.println("[] Customer Details []\n");
                System.out.println("======================\n");
                System.out.println("Id :" + registeredCustomer.getCustomerId() + "\n");
                System.out.println("Name : " + registeredCustomer.getName() + "\n");
                System.out.println("Age : " + registeredCustomer.getAge() + "\n");
                System.out.println("Phone No : " + registeredCustomer.getPhoneNumber() + "\n");
                System.out.println("Gender : " + registeredCustomer.getGender() + "\n");
                System.out.println("======================\n");
            }
        } else {
            System.out.println("..............Quit From Register .............");
        }
    }
    
    /**
     * Handle order process
     */
    private void handleOrder() {
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
                processOrder(orderDetailsList);
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
    
    /**
     * Process order and payment
     */
    private void processOrder(List<OrderDetails> orderDetailsList) {
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
            detail.getSubtotal();
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
    
    /**
     * Handle admin menu
     */
    private void handleAdminMenu() {
        boolean backMainMenu = true;
        
        do {
            MenuDisplay.displayAdminMenu();
            int adminChoice = inputHandler.readInt("Enter your choice : ");
            
            switch (adminChoice) {
                case 0:
                    backMainMenu = false;
                    System.out.println("[]===== Back main menu =====[]\n");
                    break;
                case 1:
                    handleRegisterFood();
                    break;
                case 2:
                    handleEditFood();
                    break;
                case 3:
                    handleDeleteFood();
                    break;
                case 4:
                    handleDisplayAllFoods();
                    break;
                case 5:
                    handleOrderReport();
                    break;
                default:
                    System.out.println("Choose 0 Until 5 Only !!!\n");
            }
        } while (backMainMenu);
    }
    
    /**
     * Handle food registration
     */
    private void handleRegisterFood() {
        if (!inputHandler.readYesNo("Are you sure want to add new food (Y/N) : ")) {
            System.out.println("====== Quit From Register New Food ====== \n");
            return;
        }
        
        String foodName;
        do {
            foodName = inputHandler.readString("Enter your food name: ");
            if (!foodController.validateFoodName(foodName)) {
                System.out.println("Enter letters only!\n");
            }
        } while (!foodController.validateFoodName(foodName));
        
        double foodPrice;
        do {
            foodPrice = inputHandler.readDouble("Enter your food price RM (1-69) : RM");
            if (!foodController.validateFoodPrice(foodPrice)) {
                System.out.println("Price are not able to be 0 or negative and not more than RM 70 !!!\n");
            }
        } while (!foodController.validateFoodPrice(foodPrice));
        
        String foodType;
        do {
            foodType = inputHandler.readString("Enter your food type (Set / A la carte : ");
            if (!foodController.validateFoodType(foodType)) {
                System.out.println("Type can be only Set or A la carte\n");
            }
        } while (!foodController.validateFoodType(foodType));
        
        if (inputHandler.readYesNo("Are you want to proceed to add (Y/N) : ")) {
            Food food = new Food(foodName, foodPrice, foodType);
            Food registeredFood = foodController.registerFood(food);
            if (registeredFood != null) {
                System.out.println("\n======================\n");
                System.out.println("[] Food Details []\n");
                System.out.println("======================\n");
                System.out.println("Id :" + registeredFood.getFoodId() + "\n");
                System.out.println("Name : " + registeredFood.getFoodName() + "\n");
                System.out.println("Food Price : " + registeredFood.getFoodPrice() + "\n");
                System.out.println("Food type : " + registeredFood.getFoodType() + "\n");
                System.out.println("======================\n");
            }
        } else {
            System.out.println("You have cancel registered !");
        }
    }
    
    /**
     * Handle food editing
     */
    private void handleEditFood() {
        if (!inputHandler.readYesNo("Are you sure want to edit Y(YES) / N (No): ")) {
            System.out.println("....Quit From Edit....\n");
            return;
        }
        
        int editFoodId = inputHandler.readInt("Enter the food id that want to edit : ");
        Food food = foodController.getFoodById(editFoodId);
        
        if (food == null) {
            System.out.println("Unable to find id in the database\n");
            return;
        }
        
        System.out.println("===========================");
        System.out.println("[]        DETAILS        []");
        System.out.println("===========================");
        System.out.println("         ID : " + food.getFoodId());
        System.out.println("         Name : " + food.getFoodName());
        System.out.println("         Price : " + food.getFoodPrice());
        System.out.println("         Type : " + food.getFoodType());
        System.out.println("===========================\n");
        
        char continueEdit = 'Y';
        do {
            System.out.println("=====================");
            System.out.println("[]       EDIT      []");
            System.out.println("=====================");
            System.out.println("      1.Food Name    ");
            System.out.println("      2.Food Price   ");
            System.out.println("      3.Food Type    ");
            System.out.println("=====================");
            
            int editChoice = inputHandler.readInt("Select your choice : ");
            
            switch (editChoice) {
                case 1:
                    String foodName;
                    do {
                        foodName = inputHandler.readString("Enter food name : ");
                        if (!foodController.validateFoodName(foodName)) {
                            System.out.println("Enter letters only!\n");
                        }
                    } while (!foodController.validateFoodName(foodName));
                    food.setFoodName(foodName);
                    break;
                case 2:
                    double foodPrice;
                    do {
                        foodPrice = inputHandler.readDouble("Enter food price : ");
                        if (!foodController.validateFoodPrice(foodPrice)) {
                            System.out.println("Price are not able to be 0 or negative and not more than RM 70 !!!\n");
                        }
                    } while (!foodController.validateFoodPrice(foodPrice));
                    food.setFoodPrice(foodPrice);
                    break;
                case 3:
                    String foodType;
                    do {
                        foodType = inputHandler.readString("Enter food type (Set / A la carte): ");
                        if (!foodController.validateFoodType(foodType)) {
                            System.out.println("Type can be only Set or A la carte\n");
                        }
                    } while (!foodController.validateFoodType(foodType));
                    food.setFoodType(foodType);
                    break;
                default:
                    System.out.println("Other than 1 and 3 is invalid !!!! \n");
            }
            
            continueEdit = inputHandler.readChar("Do you want to continue edit id: " + food.getFoodId() + "( Y / N ) : ");
        } while (continueEdit == 'Y' || continueEdit == 'y');
        
        Food updatedFood = foodController.updateFood(food);
        if (updatedFood != null) {
            System.out.println("File updated successfully");
        }
    }
    
    /**
     * Handle food deletion
     */
    private void handleDeleteFood() {
        if (!inputHandler.readYesNo("Do you want to delete a food (Y / N ) : ")) {
            System.out.println("====== Quit From Delete Function ======\n");
            return;
        }
        
        int deleteFoodId = inputHandler.readInt("Enter the food id want to delete : ");
        
        if (inputHandler.readYesNo("Are you sure want to delete id : " + deleteFoodId + " (Y/N) :")) {
            if (foodController.deleteFood(deleteFoodId)) {
                System.out.println("Food deleted successfully");
            }
        } else {
            System.out.println("==== You have cancel to delete id :" + deleteFoodId + " !!!!! ===\n");
        }
    }
    
    /**
     * Handle display all foods
     */
    private void handleDisplayAllFoods() {
        List<Food> foods = foodController.getAllFoods();
        MenuDisplay.displayAllFoods(foods);
    }
    
    /**
     * Handle order report
     */
    private void handleOrderReport() {
        List<Order> orders = orderController.getAllOrders();
        MenuDisplay.displayOrderReport(orders);
    }
}
