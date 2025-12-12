package presentation.Customer;

import controller.CustomerController;
import model.Customer;
import presentation.General.UserInputHandler;
import presentation.General.UserCancelledException; // 1. 记得导入这个异常类

/**
 * Handles customer-facing flows: login and register.
 */
public class CustomerHandler {

    private final CustomerController customerController;
    private final UserInputHandler inputHandler;

    public CustomerHandler(CustomerController customerController,
                           UserInputHandler inputHandler) {
        this.customerController = customerController;
        this.inputHandler = inputHandler;
    }

    public Customer handleLogin() {
        System.out.println("\n=====================================================================================");
        System.out.println("[]   Hi, dearly customer please enter your customer id and password before Login   []");
        System.out.println("=====================================================================================\n");

        try {
            // 2. 这里的 readInt 或 readString 如果输入 X 会抛出异常
            int customerId = inputHandler.readInt("Customer ID (X to cancel) : ");
            String password = inputHandler.readPassword("Password : ");

            Customer customer = customerController.login(customerId, password);

            if (customer != null) {
                System.out.println("\n=================================================================================");
                System.out.println("\t\t\t\tWelcome " + customer.getName());
                System.out.println("=================================================================================");
                return customer;
            } else {
                System.out.println("Wrong ID or Password !!!!");
            }
        } catch (UserCancelledException e) {
            System.out.println("\n>> Login Cancelled.\n");
            return null;
        }

        return null;
    }

    public void handleRegister() {
        System.out.println("==========================--=");
        System.out.println("[]      Enter Details      []");
        System.out.println("==========================--=");

        Customer customer = new Customer();

        try { 
            // Name validation
            String name;
            do {
                name = inputHandler.readString("Enter your name : ");
                try {
                    customerController.checkName(name);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("**" + e.getMessage() + "**\n");
                }
            } while (true);
            customer.setName(name);

            // Age validation
            int age;
            do {
                age = inputHandler.readInt("Enter your age (between 18 and 79) : ");
                try {
                    customerController.checkAge(age);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("**" + e.getMessage() + "**\n");
                }
            } while (true);
            customer.setAge(age);

            // Phone number validation
            String phoneNumber;
            do {
                phoneNumber = inputHandler.readString("Enter your phone number : ");
                try {
                    customerController.checkPhoneNumber(phoneNumber);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("**" + e.getMessage() + "**\n");
                }
            } while (true);
            customer.setPhoneNumber(phoneNumber);

            // Gender validation
            String gender;
            do {
                gender = inputHandler.readString("Enter your gender (Male/Female) : ");
                try {
                    customerController.checkGender(gender);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("**" + e.getMessage() + "**\n");
                }
            } while (true);
            customer.setGender(gender);

            // Password validation
            String password;
            do {
                password = inputHandler.readPassword("Enter your password : ");
                try {
                    customerController.checkPassword(password);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("**" + e.getMessage() + "**\n");
                }
            } while (true);

            // Password confirmation
            String confirmPassword;
            do {
                confirmPassword = inputHandler.readPassword("Confirm your password again : ");
                try {
                    customerController.checkPasswordConfirmation(password, confirmPassword);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("**" + e.getMessage() + "**\n");
                }
            } while (true);
            customer.setPassword(password);

            // Display customer information before confirmation
            System.out.println("\n==============================");
            System.out.println("[]     Customer Details     []");
            System.out.println("==============================");
            System.out.println("  Name : " + customer.getName());
            System.out.println("  Age : " + customer.getAge());
            System.out.println("  Phone No : " + customer.getPhoneNumber());
            System.out.println("  Gender : " + customer.getGender());
            System.out.println("==============================\n");

            // Final confirmation after displaying details
            if (inputHandler.readYesNo("Are you sure want to register (Y/N) : ")) {
                Customer registeredCustomer = customerController.registerCustomer(customer);
                if (registeredCustomer != null) {
                    System.out.println("\n=============================================");
                    System.out.println("[]      Registration Successful!          []");
                    System.out.println("=============================================");
                    System.out.println("  ID : " + registeredCustomer.getCustomerId());
                    System.out.println("  Name : " + registeredCustomer.getName());
                    System.out.println("  Age : " + registeredCustomer.getAge());
                    System.out.println("  Phone No : " + registeredCustomer.getPhoneNumber());
                    System.out.println("  Gender : " + registeredCustomer.getGender());
                    System.out.println("=============================================\n");
                } else {
                    System.out.println("\n=============================================");
                    System.out.println("[]      Registration Failed                 []");
                    System.out.println("=============================================\n");
                }
            } else {
                System.out.println("\n=============================================");
                System.out.println("[]            Quit From Register           []");
                System.out.println("=============================================\n");
            }

        } catch (UserCancelledException e) {
            // ==========================================
            // 5. 捕获 X 操作 (Catch Block)
            // ==========================================
            System.out.println("\n=============================================");
            System.out.println("|    Operation Cancelled by User (Exit)     |");
            System.out.println("=============================================\n");
            // 这里方法结束，自动回到 Main Menu
        }
    }
}