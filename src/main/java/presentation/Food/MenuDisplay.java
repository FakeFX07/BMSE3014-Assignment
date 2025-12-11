package presentation.Food;

import java.util.List;

import model.Food;
import model.Order;

import presentation.Admin.AdminMenuOption;
import presentation.General.MainMenuOption;


/**
 * Menu Display Utility
 * Handles display of menus and formatted output
 * Follows SOLID: Single Responsibility Principle
 */
public class MenuDisplay {
    
    /**
     * Display main menu
     */
    public static void displayMainMenu() {
        MainMenuOption.displayMenu();
    }
    
    /**
     * Display admin menu
     */
    public static void displayAdminMenu() {
        AdminMenuOption.displayMenu();
    }
    
    /**
     * Display food admin submenu
     */
    public static void displayFoodAdminMenu() {
        System.out.println("\n[]===============================[]");
        System.out.println("[]        Food Management        []");
        System.out.println("[]===============================[]");
        for (FoodManagementOption option : FoodManagementOption.values()) {
            String optionText = option.getCode() + "." + option.getLabel();
            String formattedLine = String.format("        %-25s", optionText);
            System.out.println(formattedLine);
        }
        System.out.println("[]===============================[]\n");
    }
    
    /**
     * Display food menu
     * 
     * @param foods List of foods to display
     */
    public static void displayFoodMenu(List<Food> foods) {
        System.out.println("============================ []Menu[] ======================");
        int index = 1;
        for (Food food : foods) {
            System.out.println(index + ". " + food.getFoodId() + "\t" + 
                             food.getFoodName() + "\t\t" + "RM " + food.getFoodPrice());
            index++;
        }
        System.out.println("0. Exit Order");
        System.out.println("==============================================================");
    }
    
    /**
     * Display order report
     * 
     * @param orders List of orders to display
     */
    public static void displayOrderReport(List<Order> orders) {
        System.out.println("=================================================================================");
        System.out.println("                                        Order Report                             ");
        System.out.println("=================================================================================");
        System.out.println("Order ID\t\tCustomer Id\t\tPayment Method\t\tTotal Price");
        System.out.println("=================================================================================");
        
        for (Order order : orders) {
            System.out.println(order.getOrderId() + "\t\t" + 
                             order.getCustomer().getCustomerId() + "\t\t\t" + 
                             order.getPaymentMethod().getPaymentType() + "\t\t\t" + 
                             "RM " + String.format("%.2f", order.getTotalPrice()));
        }
        
        System.out.println("=================================================================================");
    }
    
    /**
     * Display all foods
     * 
     * @param foods List of foods to display
     */
    public static void displayAllFoods(List<Food> foods) {
        System.out.println("================================================================");
        System.out.println("                         All Food Details                      ");
        System.out.println("================================================================");
        System.out.printf("%-5s %-10s %-22s %-14s %-12s%n", "No", "Food Id", "Food Name", "Food Price", "Food Type");
        int index = 1;
        for (Food food : foods) {
            System.out.printf("%-5d %-10d %-22s %-14.2f %-12s%n",
                    index++,
                    food.getFoodId(),
                    food.getFoodName(),
                    food.getFoodPrice(),
                    food.getFoodType());
        }
        System.out.println("================================================================");
    }
}
