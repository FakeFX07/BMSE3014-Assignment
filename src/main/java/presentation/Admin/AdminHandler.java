package presentation.Admin;

import controller.AdminController;
import controller.OrderController;
import presentation.Food.FoodHandler;
import presentation.Food.FoodManagementOption;
import presentation.Food.MenuDisplay;
import presentation.Order.OrderHandler;
import presentation.General.UserInputHandler;

public class AdminHandler {

    //Add the AdminService field
    private final AdminController adminController;
    private final FoodHandler foodHandler;
    private final OrderController orderController;
    private final UserInputHandler inputHandler;

    //Update Constructor to accept IAdminService
    public AdminHandler(AdminController adminController,
                        FoodHandler foodHandler,
                        OrderController orderController,
                        UserInputHandler inputHandler) {
        this.adminController = adminController; 
        this.foodHandler = foodHandler;
        this.orderController = orderController;
        this.inputHandler = inputHandler;
    }

    public void handleAdminMenu(OrderHandler orderHandler, model.Customer currentCustomer) {

        //Check Login before showing the menu
        if (!performLogin()) {
            System.out.println("\n!!! Access Denied: Wrong Username or Password !!!\n");
            return; 
        }

        //If login passed, show the menu
        boolean backMainMenu = true;

        do {
            MenuDisplay.displayAdminMenu();
            int adminChoice = inputHandler.readInt("Enter your choice : ");

            AdminMenuOption adminOption = AdminMenuOption.getByOptionNumber(adminChoice);
            if (adminOption == null) {
                System.out.println("**Choose 0 Until 2 Only !!!**\n");
                continue;
            }

            switch (adminOption) {
                case FOOD_MANAGEMENT:
                    handleFoodManagement();
                    break;
                case ORDER_REPORT:
                    handleOrderReport();
                    break;
                case BACK_MAIN_MENU:
                    backMainMenu = false;
                    System.out.println("\n[]======== Back main menu ========[]\n");
                    break;
                default:
                    System.out.println("**Choose 0 Until 2 Only !!!**\n");
            }
        } while (backMainMenu);
    }

    private boolean performLogin() {
        System.out.println("\n=======================");
        System.out.println("[]    ADMIN LOGIN    []");
        System.out.println("=======================\n");

        String name = inputHandler.readString("Username: ");
        String password = inputHandler.readPassword("Password: ");

        //Call the service to check database
        return adminController.login(name, password);
    }

    public void handleOrderReport() {
        MenuDisplay.displayOrderReport(orderController.getAllOrders());
    }

    private void handleFoodManagement() {
        boolean backFoodMenu = false;
        do {
            MenuDisplay.displayFoodAdminMenu();
            int choice = inputHandler.readInt("Enter your choice : ");
            FoodManagementOption option = FoodManagementOption.fromCode(choice);
            if (option == null) {
                System.out.println("Choose 0 Until 4 Only !!!\n");
                continue;
            }
            switch (option) {
                case REGISTER_FOOD:
                    foodHandler.handleRegisterFood();
                    break;
                case EDIT_FOOD:
                    foodHandler.handleEditFood();
                    break;
                case DELETE_FOOD:
                    foodHandler.handleDeleteFood();
                    break;
                case VIEW_ALL_FOOD:
                    foodHandler.handleDisplayAllFoods();
                    break;
                case EXIT:
                    backFoodMenu = true;
                    System.out.println("Returning to Admin Menu...\n");
                    break;
            }
        } while (!backFoodMenu);
    }
}