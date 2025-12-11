package presentation.Customer;

import controller.CustomerController;
import model.Customer;
import presentation.General.UserInputHandler;

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
        System.out.println("================================================================================");
        System.out.println("[] Hi,dearly customer please enter your customer id and password before Login []");
        System.out.println("================================================================================\n");

        int customerId = inputHandler.readInt("Customer id : ");
        String password = inputHandler.readString("Password : ");

        Customer customer = customerController.login(customerId, password);

        if (customer != null) {
            System.out.println("[]=========================================[]");
            System.out.println("\t\tWelcome " + customer.getName());
            System.out.println("\tHave a great time placing an order");
            System.out.println("[]=========================================[]");
            return customer;
        } else {
            System.out.println("Wrong id or password");
        }
        return null;
    }

    public void handleRegister() {
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
}

