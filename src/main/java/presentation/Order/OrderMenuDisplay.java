package presentation.Order;

import java.util.List;

import model.Food;
import model.OrderDetails;

public final class OrderMenuDisplay {

    private OrderMenuDisplay() {}

    /**
     * Display the selectable food menu used during ordering (index, name, price, quantity).
     */
    public static void displayOrderMenu(List<Food> foods) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                   ORDER MENU                                  ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ %-4s │ %-35s │ %12s │ %17s ║%n", "No.", "Food Name", "Price (RM)", "Available");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");
        
        int index = 1;
        for (Food food : foods) {
            String availability = food.getQuantity() > 0 ? String.valueOf(food.getQuantity()) + " pcs" : "Out of Stock";
            // Truncate food name if too long to maintain alignment
            String foodName = food.getFoodName();
            if (foodName.length() > 35) {
                foodName = foodName.substring(0, 32) + "...";
            }
            // Pad availability to ensure consistent width
            if (availability.length() < 12) {
                availability = String.format("%12s", availability);
            } else if (availability.length() > 12) {
                availability = availability.substring(0, 12);
            }
            System.out.printf("║ %3d. │ %-35s │ %12.2f │ %17s ║%n", 
                            index++, foodName, food.getFoodPrice(), availability);
        }
        
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║   0. Exit Order                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Display a short order summary (line items + subtotal)
     */
    public static void displayOrderSummary(List<OrderDetails> details) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                             ORDER SUMMARY                                     ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ %-6s │ %-30s │ %10s │ %6s │ %13s ║%n", "ID", "Food Name", "Unit Price", "Qty", "Subtotal");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");
        
        double total = 0.0;
        for (OrderDetails d : details) {
            if (d == null || d.getFood() == null) continue;
            System.out.printf("║ %6d │ %-30s │ RM %7.2f │ %4d │ RM %12.2f ║%n",
                    d.getFood().getFoodId(), 
                    d.getFood().getFoodName(), 
                    d.getUnitPriceDecimal().doubleValue(), 
                    d.getQuantity(), 
                    d.getSubtotal());
            total += d.getSubtotal();
        }
        
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ %-64s RM %9.2f ║%n", "TOTAL:", total);
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝\n");
    }
}
