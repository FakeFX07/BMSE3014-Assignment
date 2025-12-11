package presentation.Admin;

import controller.OrderController;
import presentation.Food.FoodHandler;
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

            switch (adminChoice) {
                case 0:
                    backMainMenu = false;
                    System.out.println("[]===== Back main menu =====[]\n");
                    break;
                case 1:
                    foodHandler.handleRegisterFood();
                    break;
                case 2:
                    foodHandler.handleEditFood();
                    break;
                case 3:
                    foodHandler.handleDeleteFood();
                    break;
                case 4:
                    foodHandler.handleDisplayAllFoods();
                    break;
                case 5:
                    handleOrderReport();
                    break;
                default:
                    System.out.println("Choose 0 Until 5 Only !!!\n");
            }
        } while (backMainMenu);
    }

    public void handleOrderReport() {
        MenuDisplay.displayOrderReport(orderController.getAllOrders());
    }
}

