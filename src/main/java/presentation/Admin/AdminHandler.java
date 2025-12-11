package presentation.Admin;

import controller.OrderController;
import presentation.Food.FoodHandler;
import presentation.Food.FoodManagementOption;
import presentation.Food.MenuDisplay;
import presentation.Order.OrderHandler;
import presentation.General.UserInputHandler;

/**
 * Handles admin menu navigation and reporting.
 */
public class AdminHandler {

    private final FoodHandler foodHandler;
    private final OrderController orderController;
    private final UserInputHandler inputHandler;

    public AdminHandler(FoodHandler foodHandler,
                        OrderController orderController,
                        UserInputHandler inputHandler) {
        this.foodHandler = foodHandler;
        this.orderController = orderController;
        this.inputHandler = inputHandler;
    }

    public void handleAdminMenu(OrderHandler orderHandler, model.Customer currentCustomer) {
        boolean backMainMenu = true;

        do {
            MenuDisplay.displayAdminMenu();
            int adminChoice = inputHandler.readInt("Enter your choice : ");

            AdminMenuOption adminOption = AdminMenuOption.getByOptionNumber(adminChoice);
            if (adminOption == null) {
                System.out.println("Choose 0 Until 2 Only !!!\n");
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
                    System.out.println("[]===== Back main menu =====[]\n");
                    break;
                default:
                    System.out.println("Choose 0 Until 2 Only !!!\n");
            }
        } while (backMainMenu);
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
                default:
                    backFoodMenu = true;
            }
        } while (!backFoodMenu);
    }
}

