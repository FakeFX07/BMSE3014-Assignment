package presentation.Food;

//Food management (Admin) menu options.
public enum FoodManagementOption {
    REGISTER_FOOD(1, "Register New Food"),
    EDIT_FOOD(2, "Edit Food"),
    DELETE_FOOD(3, "Delete Food"),
    VIEW_ALL_FOOD(4, "View All Food"),
    EXIT(0, "Back to Admin Menu");

    private final int code;
    private final String label;

    FoodManagementOption(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static FoodManagementOption fromCode(int code) {
        for (FoodManagementOption opt : values()) {
            if (opt.code == code) {
                return opt;
            }
        }
        return null;
    }
}

