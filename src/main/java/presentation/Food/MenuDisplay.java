package presentation.Food;

import java.util.List;

import model.Food;
import model.Order;
import model.OrderDetails;
import model.PaymentMethod;
import presentation.Admin.AdminMenuOption;
import presentation.General.MainMenuOption;
import presentation.Payment.PaymentOption;

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
     * Display payment options
     */
    public static void displayPaymentOptions() {
        PaymentOption.displayMenu();
    }
    
    /**
     * Display order receipt
     * 
     * @param order Order to display
     */
    public static void displayReceipt(Order order) {
        System.out.println("======================================================================");
        System.out.println("                                 RECEIPT                              ");
        System.out.println("======================================================================");
        System.out.println("Order Id : " + order.getOrderId() + "\t\t\tDate : " + order.getOrderDate());
        System.out.println("==============");
        System.out.println("Cust ID : " + order.getCustomer().getCustomerId());
        System.out.println("======================================================================");
        
        System.out.println("Food Id \t Food Name\t     Food Price  Qty \t\tTotal Price");
        for (OrderDetails detail : order.getOrderDetails()) {
            System.out.println(detail.toString());
        }
        System.out.println("======================================================================");
        
        System.out.println("Subtotal :\t\t\t\t\t\tRM " + String.format("%.2f", order.getTotalPrice()));
        System.out.println("======================================================================");
        
        PaymentMethod paymentMethod = order.getPaymentMethod();
        System.out.println(paymentMethod.getPaymentType() + "      : \t\t\t\t\t\tRM " + 
                         String.format("%.2f", paymentMethod.getBalance()));
        
        // Calculate exchange (this would need payment processing info)
        System.out.println("======================================================================");
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
        System.out.println("===============================================================");
        System.out.println("                         All Food Details                      ");
        System.out.println("===============================================================");
        System.out.println("Food Id\t\tFood Name\t\tFood price\tFood Type");
        
        for (Food food : foods) {
            System.out.println(food.getFoodId() + "\t\t" + 
                             food.getFoodName() + "\t\t" + 
                             food.getFoodPrice() + "\t\t" + 
                             food.getFoodType());
        }
        
        System.out.println("===============================================================");
    }
}
