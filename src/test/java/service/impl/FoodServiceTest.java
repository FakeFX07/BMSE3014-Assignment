package service.impl;

import model.Food;
import org.junit.jupiter.api.*;
import repository.interfaces.IFoodRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FoodServiceTest {

    private FoodService foodService;
    private FakeFoodRepository fakeRepository;

    @BeforeEach
    void setUp() {
        fakeRepository = new FakeFoodRepository();
        foodService = new FoodService(fakeRepository);
    }

    @Nested
    @DisplayName("Registration Logic")
    class RegistrationTests {

        @Test
        @DisplayName("Should successfully register valid food")
        void shouldRegisterValidFood() {
            Food newFood = new Food("Nasi Lemak", 5.50, "Set");
            
            Food result = foodService.registerFood(newFood);
            
            assertNotNull(result);
            assertTrue(result.getFoodId() >= 2000, "Should generate a valid ID starting from 2000");
            assertEquals("Nasi Lemak", result.getFoodName());
            
            // Verify persistence
            assertTrue(fakeRepository.existsById(result.getFoodId()));
        }

        @Test
        @DisplayName("Should reject invalid names (numbers/empty)")
        void shouldRejectInvalidNames() {
            assertAll("Invalid Name Scenarios",
                () -> assertThrows(IllegalArgumentException.class, () -> 
                        foodService.registerFood(new Food("Food123", 10.0, "Set"))),
                () -> assertThrows(IllegalArgumentException.class, () -> 
                        foodService.registerFood(new Food("", 10.0, "Set"))),
                () -> assertThrows(IllegalArgumentException.class, () -> 
                        foodService.registerFood(new Food(null, 10.0, "Set")))
            );
        }

        @Test
        @DisplayName("Should reject invalid prices (zero/negative)")
        void shouldRejectInvalidPrices() {
            assertAll("Invalid Price Scenarios",
                () -> assertThrows(IllegalArgumentException.class, () -> 
                        foodService.registerFood(new Food("Free Food", 0.0, "Set"))),
                () -> assertThrows(IllegalArgumentException.class, () -> 
                        foodService.registerFood(new Food("Negative Price", -5.0, "Set")))
            );
        }

        @Test
        @DisplayName("Should accept minimum valid price (0.01)")
        void shouldAcceptMinimumPrice() {
            Food cheapFood = new Food("Penny Candy", 0.01, "Set");
            assertDoesNotThrow(() -> foodService.registerFood(cheapFood));
        }

        @Test
        @DisplayName("Should validate food types")
        void shouldValidateFoodTypes() {
            // Valid types
            assertDoesNotThrow(() -> foodService.registerFood(new Food("Valid Set", 10.0, "Set")));
            assertDoesNotThrow(() -> foodService.registerFood(new Food("Valid Ala Carte", 10.0, "A la carte")));

            // Invalid type
            assertThrows(IllegalArgumentException.class, () -> 
                foodService.registerFood(new Food("Invalid Type", 10.0, "Buffet")));
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateTests {

        @Test
        @DisplayName("Should update existing food details")
        void shouldUpdateSuccessfully() {
            // Arrange
            Food original = new Food(2000, "Old Name", 10.00, "Set");
            fakeRepository.save(original);

            // Act
            original.setFoodName("New Name");
            original.setFoodPrice(12.00);
            Food updated = foodService.updateFood(original);

            // Assert
            assertEquals("New Name", updated.getFoodName());
            assertEquals(12.00, updated.getFoodPrice());
            
            // Verify DB state
            assertEquals("New Name", fakeRepository.findById(2000).get().getFoodName());
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent food")
        void shouldFailUpdateIfNotFound() {
            Food ghostFood = new Food(9999, "Ghost", 10.00, "Set");
            assertThrows(IllegalArgumentException.class, () -> foodService.updateFood(ghostFood));
        }

        @Test
        @DisplayName("Should re-validate data before updating")
        void shouldValidateDataOnUpdate() {
            Food existing = new Food(2000, "Valid", 10.0, "Set");
            fakeRepository.save(existing);

            // Try setting invalid name
            existing.setFoodName("Invalid123");
            
            assertThrows(IllegalArgumentException.class, () -> foodService.updateFood(existing));
        }
    }

    @Nested
    @DisplayName("Read & Delete Operations")
    class RetrievalTests {

        @Test
        @DisplayName("Should retrieve food by ID")
        void shouldGetFoodById() {
            fakeRepository.save(new Food(2000, "Target", 10.0, "Set"));
            
            Optional<Food> found = foodService.getFoodById(2000);
            assertTrue(found.isPresent());
            assertEquals("Target", found.get().getFoodName());
        }

        @Test
        @DisplayName("Should find food by name (case-insensitive)")
        void shouldFindByName() {
            fakeRepository.save(new Food(2000, "Burger", 10.0, "Set"));

            assertTrue(foodService.getFoodByName("Burger").isPresent());
            assertTrue(foodService.getFoodByName("BURGER").isPresent());
            assertFalse(foodService.getFoodByName("Pizza").isPresent());
        }

        @Test
        @DisplayName("Should retrieve all foods")
        void shouldGetAllFoods() {
            fakeRepository.save(new Food(2000, "A", 1.0, "Set"));
            fakeRepository.save(new Food(2001, "B", 2.0, "Set"));

            assertEquals(2, foodService.getAllFoods().size());
        }

        @Test
        @DisplayName("Should delete existing food")
        void shouldDeleteFood() {
            fakeRepository.save(new Food(2000, "To Delete", 10.0, "Set"));
            
            assertTrue(foodService.deleteFood(2000));
            assertFalse(fakeRepository.findById(2000).isPresent());
        }

        @Test
        @DisplayName("Should return false when deleting non-existent food")
        void shouldFailDeleteIfNotFound() {
            assertFalse(foodService.deleteFood(9999));
        }
    }

    @Nested
    @DisplayName("Utility Validations")
    class ValidationUtilityTests {

        @Test
        @DisplayName("Should verify name uniqueness")
        void shouldCheckUniqueness() {
            fakeRepository.save(new Food(2000, "Taken", 10.0, "Set"));

            assertFalse(foodService.isFoodNameUnique("Taken"));
            assertTrue(foodService.isFoodNameUnique("Available"));
            assertFalse(foodService.isFoodNameUnique("")); // Empty is implicitly not unique/valid
            assertFalse(foodService.isFoodNameUnique(null));
        }

        @Test
        @DisplayName("Should validate business rules for fields")
        void shouldValidateFieldsCorrectly() {
            // Name
            assertTrue(foodService.validateFoodName("Good Name"));
            assertFalse(foodService.validateFoodName("Bad123"));
            
            // Price
            assertTrue(foodService.validateFoodPrice(10.0));
            assertFalse(foodService.validateFoodPrice(0.0));
            
            // Type
            assertTrue(foodService.validateFoodType("Set"));
            assertFalse(foodService.validateFoodType("Buffet"));
        }
    }

    private static class FakeFoodRepository implements IFoodRepository {
        private final Map<Integer, Food> db = new HashMap<>();

        @Override
        public Food save(Food food) {
            if (food.getFoodId() == 0) {
                food.setFoodId(getNextFoodId());
            }
            db.put(food.getFoodId(), food);
            return food;
        }

        @Override
        public Food update(Food food) {
            if (db.containsKey(food.getFoodId())) {
                db.put(food.getFoodId(), food);
                return food;
            }
            return null;
        }

        @Override
        public Optional<Food> findById(int id) {
            return Optional.ofNullable(db.get(id));
        }

        @Override
        public Optional<Food> findByName(String name) {
            return db.values().stream()
                    .filter(f -> f.getFoodName().equalsIgnoreCase(name))
                    .findFirst();
        }

        @Override
        public List<Food> findAll() {
            return new ArrayList<>(db.values());
        }

        @Override
        public boolean deleteById(int id) {
            return db.remove(id) != null;
        }

        @Override
        public boolean existsById(int id) {
            return db.containsKey(id);
        }

        @Override
        public boolean existsByName(String name) {
            return db.values().stream()
                    .anyMatch(f -> f.getFoodName().equalsIgnoreCase(name));
        }

        @Override
        public int getNextFoodId() {
            return db.keySet().stream().max(Integer::compareTo).orElse(1999) + 1;
        }

        @Override
        public boolean decrementQuantity(int foodId, int quantityToDeduct) {
            // Not strictly needed for these service tests, but implemented for interface compliance
            return true; 
        }
    }
}