package presentation.Food;

//Edit menu options for food.
public enum FoodEditOption {
    NAME(1, "Food Name"),
    PRICE(2, "Food Price"),
    TYPE(3, "Food Type");

    private final int code;
    private final String label;

    FoodEditOption(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static FoodEditOption fromCode(int code) {
        for (FoodEditOption opt : values()) {
            if (opt.code == code) {
                return opt;
            }
        }
        return null;
    }
}

