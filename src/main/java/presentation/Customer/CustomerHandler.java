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
            String password = inputHandler.readString("Password (X to cancel) : ");

            Customer customer = customerController.login(customerId, password);

            if (customer != null) {
                System.out.println("\n===================================================");
                System.out.println("\t\tWelcome " + customer.getName());
                System.out.println("\tHave a great time placing an order");
                System.out.println("===================================================\n");
                return customer;
            } else {
                System.out.println("\n========================");
                System.out.println("| Wrong ID or Password |");
                System.out.println("========================\n");
            }
        } catch (UserCancelledException e) {
            // 3. 捕获取消操作，安全退出登录流程
            System.out.println("\n>> Login Cancelled.\n");
            return null;
        }

        return null;
    }

    public void handleRegister() {
        // 这个简单的 YesNo 也可以抛出异常，不过通常 UserInputHandler 对 readYesNo 的实现可能略有不同
        // 如果你的 readYesNo 也会抛出异常，建议也放进 try 里面。
        // 这里假设 readYesNo 只是返回 true/false。
        if (!inputHandler.readYesNo("Are you confirm want to register ? (Y/N) : ")) {
            System.out.println("\n========================");
            System.out.println("|  Quit From Register  |");
            System.out.println("========================\n");
            return;
        }

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
                password = inputHandler.readString("Enter your password : ");
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
                confirmPassword = inputHandler.readString("Confirm your password again : ");
                try {
                    customerController.checkPasswordConfirmation(password, confirmPassword);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("**" + e.getMessage() + "**\n");
                }
            } while (true);
            customer.setPassword(password);

            // Final confirmation
            if (inputHandler.readYesNo("Are you sure want to register (Y/N) :")) {
                Customer registeredCustomer = customerController.registerCustomer(customer);
                if (registeredCustomer != null) {
                    System.out.println("\n==============================");
                    System.out.println("[]     Customer Details     []");
                    System.out.println("==============================");
                    System.out.println("  ID : " + registeredCustomer.getCustomerId());
                    System.out.println("  Name : " + registeredCustomer.getName());
                    System.out.println("  Age : " + registeredCustomer.getAge());
                    System.out.println("  Phone No : " + registeredCustomer.getPhoneNumber());
                    System.out.println("  Gender : " + registeredCustomer.getGender());
                    System.out.println("==============================\n");
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