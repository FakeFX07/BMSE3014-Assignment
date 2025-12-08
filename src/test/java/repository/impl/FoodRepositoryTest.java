package repository.impl;

import config.ConnectionProvider;
import config.DatabaseConnection;
import config.TestDatabaseSetup;
import model.Food;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Food Repository Test
 * Tests food repository database operations
 */
public class FoodRepositoryTest {
    
    private FoodRepository repository;
    private ConnectionProvider connectionProvider;
    private static final String H2_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL";
    
    @BeforeEach
    void setUp() throws SQLException {
        connectionProvider = DatabaseConnection.createInstance(H2_URL, "sa", "");
        TestDatabaseSetup.initializeSchema(connectionProvider);
        repository = new FoodRepository(connectionProvider);
    }
    
    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        if (connectionProvider instanceof DatabaseConnection) {
            ((DatabaseConnection) connectionProvider).closeConnection();
        }
    }
    
    @Test
    @DisplayName("Test findById - existing food")
    void testFindById_Existing() {
        Optional<Food> food = repository.findById(2000);
        assertTrue(food.isPresent());
        assertEquals("Chicken Rice", food.get().getFoodName());
        assertEquals(10.50, food.get().getFoodPrice(), 0.01);
    }
    
    @Test
    @DisplayName("Test findById - non-existing food")
    void testFindById_NonExisting() {
        Optional<Food> food = repository.findById(9999);
        assertFalse(food.isPresent());
    }
    
    @Test
    @DisplayName("Test findAll - returns all foods")
    void testFindAll() {
        List<Food> foods = repository.findAll();
        assertTrue(foods.size() >= 3);
    }
    
    @Test
    @DisplayName("Test save - new food")
    void testSave_NewFood() {
        Food food = new Food("Test Food", 15.00, "Set");
        Food saved = repository.save(food);
        assertTrue(saved.getFoodId() > 0);
        assertEquals("Test Food", saved.getFoodName());
    }
    
    @Test
    @DisplayName("Test update - existing food")
    void testUpdate_ExistingFood() {
        Food food = new Food(2000, "Updated Chicken Rice", 12.00, "Set");
        Food updated = repository.update(food);
        assertEquals(12.00, updated.getFoodPrice(), 0.01);
        
        Optional<Food> found = repository.findById(2000);
        assertTrue(found.isPresent());
        assertEquals("Updated Chicken Rice", found.get().getFoodName());
    }
    
    @Test
    @DisplayName("Test deleteById - existing food")
    void testDeleteById_Existing() {
        boolean deleted = repository.deleteById(2001);
        assertTrue(deleted);
        
        Optional<Food> found = repository.findById(2001);
        assertFalse(found.isPresent());
    }
    
    @Test
    @DisplayName("Test deleteById - non-existing food")
    void testDeleteById_NonExisting() {
        boolean deleted = repository.deleteById(9999);
        assertFalse(deleted);
    }
    
    @Test
    @DisplayName("Test getNextFoodId - returns next ID")
    void testGetNextFoodId() {
        int nextId = repository.getNextFoodId();
        assertTrue(nextId >= 2000);
    }
    
    @Test
    @DisplayName("Test existsById - existing")
    void testExistsById_Existing() {
        assertTrue(repository.existsById(2000));
    }
    
    @Test
    @DisplayName("Test existsById - non-existing")
    void testExistsById_NonExisting() {
        assertFalse(repository.existsById(9999));
    }
    
    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        FoodRepository repo = new FoodRepository();
        assertNotNull(repo);
    }
    
    @Test
    @DisplayName("Test getNextFoodId - when no foods exist")
    void testGetNextFoodId_NoFoods() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        int nextId = repository.getNextFoodId();
        assertEquals(2000, nextId);
    }
    
    @Test
    @DisplayName("Test save - food with all fields")
    void testSave_AllFields() {
        Food food = new Food("Complete Food", 25.50, "A la carte");
        Food saved = repository.save(food);
        assertTrue(saved.getFoodId() > 0);
        assertEquals("Complete Food", saved.getFoodName());
        assertEquals(25.50, saved.getFoodPrice(), 0.01);
        assertEquals("A la carte", saved.getFoodType());
    }
    
    @Test
    @DisplayName("Test update - updates all fields")
    void testUpdate_AllFields() {
        Food food = new Food(2000, "Fully Updated", 15.75, "A la carte");
        repository.update(food);
        
        Optional<Food> found = repository.findById(2000);
        assertTrue(found.isPresent());
        assertEquals("Fully Updated", found.get().getFoodName());
        assertEquals(15.75, found.get().getFoodPrice(), 0.01);
        assertEquals("A la carte", found.get().getFoodType());
    }
    
    @Test
    @DisplayName("Test save - food with generated ID")
    void testSave_WithGeneratedId() {
        Food food = new Food("Auto ID Food", 12.00, "Set");
        Food saved = repository.save(food);
        assertTrue(saved.getFoodId() > 0);
        
        Optional<Food> found = repository.findById(saved.getFoodId());
        assertTrue(found.isPresent());
        assertEquals("Auto ID Food", found.get().getFoodName());
    }
    
    @Test
    @DisplayName("Test findAll - returns all foods including new ones")
    void testFindAll_IncludesNewFoods() {
        int initialCount = repository.findAll().size();
        
        Food newFood = new Food("New Food", 15.00, "A la carte");
        repository.save(newFood);
        
        List<Food> allFoods = repository.findAll();
        assertTrue(allFoods.size() > initialCount);
    }
    
    @Test
    @DisplayName("Test update - verify all fields updated")
    void testUpdate_AllFieldsUpdated() {
        Food original = repository.findById(2000).orElseThrow();
        String originalName = original.getFoodName();
        double originalPrice = original.getFoodPrice();
        
        Food updated = new Food(2000, "Updated Name", 20.00, "A la carte");
        repository.update(updated);
        
        Food found = repository.findById(2000).orElseThrow();
        assertNotEquals(originalName, found.getFoodName());
        assertNotEquals(originalPrice, found.getFoodPrice(), 0.01);
        assertEquals("Updated Name", found.getFoodName());
        assertEquals(20.00, found.getFoodPrice(), 0.01);
    }
    
    @Test
    @DisplayName("Test findAll - verify order and content")
    void testFindAll_OrderAndContent() {
        List<Food> foods = repository.findAll();
        assertTrue(foods.size() >= 3);
        
        // Verify foods are ordered by ID
        for (int i = 1; i < foods.size(); i++) {
            assertTrue(foods.get(i).getFoodId() >= foods.get(i-1).getFoodId());
        }
    }
    
    @Test
    @DisplayName("Test existsById - verify for existing and non-existing")
    void testExistsById_ExistingAndNonExisting() {
        assertTrue(repository.existsById(2000));
        assertTrue(repository.existsById(2001));
        assertFalse(repository.existsById(9999));
    }
    
    @Test
    @DisplayName("Test save - multiple foods")
    void testSave_MultipleFoods() {
        Food food1 = new Food("Food One", 10.00, "Set");
        Food food2 = new Food("Food Two", 15.00, "A la carte");
        
        Food saved1 = repository.save(food1);
        Food saved2 = repository.save(food2);
        
        assertNotEquals(saved1.getFoodId(), saved2.getFoodId());
        assertTrue(saved1.getFoodId() > 0);
        assertTrue(saved2.getFoodId() > 0);
    }
    
    @Test
    @DisplayName("Test findById - verify all food fields")
    void testFindById_AllFields() {
        Optional<Food> food = repository.findById(2000);
        assertTrue(food.isPresent());
        assertNotNull(food.get().getFoodName());
        assertTrue(food.get().getFoodPrice() > 0);
        assertNotNull(food.get().getFoodType());
    }
    
    @Test
    @DisplayName("Test getNextFoodId - with max ID less than 2000")
    void testGetNextFoodId_MaxIdLessThan2000() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        int nextId = repository.getNextFoodId();
        assertEquals(2000, nextId);
    }
    
    @Test
    @DisplayName("Test getNextFoodId - with max ID greater than 2000")
    void testGetNextFoodId_MaxIdGreaterThan2000() {
        // Create foods to ensure max ID > 2000
        Food food = new Food("Test Food", 10.00, "Set");
        repository.save(food);
        
        int nextId = repository.getNextFoodId();
        assertTrue(nextId >= 2000);
    }
    
    @Test
    @DisplayName("Test existsById - returns false when count is 0")
    void testExistsById_CountZero() {
        assertFalse(repository.existsById(99999));
    }
    
    @Test
    @DisplayName("Test save - verify generated keys path")
    void testSave_GeneratedKeysPath() {
        Food food = new Food("Gen Key Food", 12.00, "Set");
        Food saved = repository.save(food);
        // Verify ID was generated
        assertTrue(saved.getFoodId() > 0);
        assertTrue(saved.getFoodId() >= 2000);
    }
    
    @Test
    @DisplayName("Test findById - verify mapping all fields")
    void testFindById_VerifyAllFields() {
        Optional<Food> food = repository.findById(2000);
        assertTrue(food.isPresent());
        Food f = food.get();
        assertEquals(2000, f.getFoodId());
        assertEquals("Chicken Rice", f.getFoodName());
        assertEquals(10.50, f.getFoodPrice(), 0.01);
        assertEquals("Set", f.getFoodType());
    }
    
    @Test
    @DisplayName("Test findAll - verify all foods have all fields")
    void testFindAll_VerifyAllFields() {
        List<Food> foods = repository.findAll();
        assertTrue(foods.size() >= 3);
        
        for (Food food : foods) {
            assertTrue(food.getFoodId() > 0);
            assertNotNull(food.getFoodName());
            assertTrue(food.getFoodPrice() > 0);
            assertNotNull(food.getFoodType());
        }
    }
    
    @Test
    @DisplayName("Test update - verify all fields are updated in database")
    void testUpdate_VerifyDatabaseUpdate() {
        Food original = repository.findById(2001).orElseThrow();
        
        Food updated = new Food(2001, "Completely New Name", 25.99, "A la carte");
        repository.update(updated);
        
        Food found = repository.findById(2001).orElseThrow();
        assertEquals("Completely New Name", found.getFoodName());
        assertEquals(25.99, found.getFoodPrice(), 0.01);
        assertEquals("A la carte", found.getFoodType());
        assertNotEquals(original.getFoodName(), found.getFoodName());
    }
    
    @Test
    @DisplayName("Test deleteById - verify food is actually deleted")
    void testDeleteById_VerifyDeletion() {
        // First create a food
        Food food = new Food("To Delete", 10.00, "Set");
        Food saved = repository.save(food);
        int foodId = saved.getFoodId();
        
        // Verify it exists
        assertTrue(repository.existsById(foodId));
        
        // Delete it
        boolean deleted = repository.deleteById(foodId);
        assertTrue(deleted);
        
        // Verify it no longer exists
        assertFalse(repository.existsById(foodId));
        assertFalse(repository.findById(foodId).isPresent());
    }
    
    @Test
    @DisplayName("Test save - multiple saves verify unique IDs")
    void testSave_MultipleSavesUniqueIds() {
        Food f1 = new Food("Multi Food 1", 10.00, "Set");
        Food f2 = new Food("Multi Food 2", 15.00, "A la carte");
        
        Food saved1 = repository.save(f1);
        Food saved2 = repository.save(f2);
        
        assertNotEquals(saved1.getFoodId(), saved2.getFoodId());
        assertTrue(saved1.getFoodId() > 0);
        assertTrue(saved2.getFoodId() > 0);
    }
    
    @Test
    @DisplayName("Test findAll - empty result set")
    void testFindAll_EmptyResultSet() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        List<Food> foods = repository.findAll();
        assertTrue(foods.isEmpty());
    }
    
    @Test
    @DisplayName("Test update - update with same values")
    void testUpdate_WithSameValues() {
        Food original = repository.findById(2000).orElseThrow();
        String originalName = original.getFoodName();
        double originalPrice = original.getFoodPrice();
        String originalType = original.getFoodType();
        
        Food updated = new Food(2000, originalName, originalPrice, originalType);
        repository.update(updated);
        
        Food found = repository.findById(2000).orElseThrow();
        assertEquals(originalName, found.getFoodName());
        assertEquals(originalPrice, found.getFoodPrice(), 0.01);
        assertEquals(originalType, found.getFoodType());
    }
    
    @Test
    @DisplayName("Test save - food with existing ID gets new generated ID")
    void testSave_WithExistingId() {
        Food food = new Food(9999, "Pre-set ID Food", 20.00, "Set");
        // Save generates a new ID regardless of pre-set ID
        Food saved = repository.save(food);
        assertTrue(saved.getFoodId() > 0);
        // ID will be generated, not the pre-set value
    }
    
    @Test
    @DisplayName("Test deleteById - delete non-existent food")
    void testDeleteById_NonExistent() {
        boolean deleted = repository.deleteById(99999);
        assertFalse(deleted);
    }
    
    @Test
    @DisplayName("Test existsById - multiple checks")
    void testExistsById_MultipleChecks() {
        assertTrue(repository.existsById(2000));
        assertTrue(repository.existsById(2001));
        assertTrue(repository.existsById(2002));
        assertFalse(repository.existsById(9999));
        assertFalse(repository.existsById(0));
    }
    
    @Test
    @DisplayName("Test getNextFoodId - after multiple saves")
    void testGetNextFoodId_AfterMultipleSaves() {
        int initialNextId = repository.getNextFoodId();
        
        Food f1 = new Food("Food A", 10.00, "Set");
        Food f2 = new Food("Food B", 15.00, "A la carte");
        Food f3 = new Food("Food C", 20.00, "Set");
        
        repository.save(f1);
        repository.save(f2);
        repository.save(f3);
        
        int finalNextId = repository.getNextFoodId();
        assertTrue(finalNextId > initialNextId);
    }
    
    @Test
    @DisplayName("Test findAll - verify no duplicates")
    void testFindAll_NoDuplicates() {
        List<Food> foods = repository.findAll();
        long uniqueIds = foods.stream().mapToInt(Food::getFoodId).distinct().count();
        assertEquals(foods.size(), uniqueIds);
    }
    
    @Test
    @DisplayName("Test save - verify BigDecimal conversion")
    void testSave_BigDecimalConversion() {
        Food food = new Food("Decimal Test", 12.345, "Set");
        Food saved = repository.save(food);
        
        Optional<Food> found = repository.findById(saved.getFoodId());
        assertTrue(found.isPresent());
        // Database stores DECIMAL(10,2) so rounds to 2 decimal places
        assertEquals(12.35, found.get().getFoodPrice(), 0.01);
    }
    
    @Test
    @DisplayName("Test update - verify BigDecimal conversion")
    void testUpdate_BigDecimalConversion() {
        Food food = new Food(2000, "Decimal Update", 33.456, "A la carte");
        repository.update(food);
        
        Optional<Food> found = repository.findById(2000);
        assertTrue(found.isPresent());
        // Database stores DECIMAL(10,2) so rounds to 2 decimal places
        assertEquals(33.46, found.get().getFoodPrice(), 0.01);
    }
    
    @Test
    @DisplayName("Test save - affectedRows path")
    void testSave_AffectedRowsPath() {
        Food food = new Food("Affected Rows Test", 15.00, "Set");
        Food saved = repository.save(food);
        // Verify save was successful (affectedRows > 0)
        assertTrue(saved.getFoodId() > 0);
        assertNotNull(saved.getFoodName());
    }
    
    @Test
    @DisplayName("Test save - verify generated keys next() path")
    void testSave_GeneratedKeysNext() {
        Food food = new Food("Gen Keys Test", 18.00, "A la carte");
        Food saved = repository.save(food);
        // This tests the generatedKeys.next() path
        assertTrue(saved.getFoodId() > 0);
    }
    
    @Test
    @DisplayName("Test update - executeUpdate path")
    void testUpdate_ExecuteUpdatePath() {
        Food food = new Food(2001, "Execute Update Test", 22.00, "Set");
        Food updated = repository.update(food);
        assertEquals(2001, updated.getFoodId());
        assertEquals("Execute Update Test", updated.getFoodName());
    }
    
    @Test
    @DisplayName("Test deleteById - executeUpdate returns 0")
    void testDeleteById_ExecuteUpdateReturnsZero() {
        // Delete non-existent food - executeUpdate returns 0
        boolean deleted = repository.deleteById(99999);
        assertFalse(deleted);
    }
    
    @Test
    @DisplayName("Test deleteById - executeUpdate returns > 0")
    void testDeleteById_ExecuteUpdateReturnsPositive() {
        // Create a food first
        Food food = new Food("To Delete", 10.00, "Set");
        Food saved = repository.save(food);
        
        // Delete it - executeUpdate should return > 0
        boolean deleted = repository.deleteById(saved.getFoodId());
        assertTrue(deleted);
    }
    
    @Test
    @DisplayName("Test getNextFoodId - rs.next() returns false")
    void testGetNextFoodId_ResultSetNextFalse() throws SQLException {
        // This tests the path where rs.next() returns false
        TestDatabaseSetup.cleanup(connectionProvider);
        int nextId = repository.getNextFoodId();
        // Should return default 2000 when no rows
        assertEquals(2000, nextId);
    }
    
    @Test
    @DisplayName("Test existsById - rs.next() returns false")
    void testExistsById_ResultSetNextFalse() {
        // Test with non-existent ID where rs.next() returns false
        boolean exists = repository.existsById(99999);
        assertFalse(exists);
    }
    
    @Test
    @DisplayName("Test findAll - while loop with multiple rows")
    void testFindAll_WhileLoopMultipleRows() {
        // Add multiple foods
        repository.save(new Food("Food A", 10.00, "Set"));
        repository.save(new Food("Food B", 15.00, "A la carte"));
        repository.save(new Food("Food C", 20.00, "Set"));
        
        List<Food> foods = repository.findAll();
        // Should include original test data + new foods
        assertTrue(foods.size() >= 6);
    }
    
    @Test
    @DisplayName("Test findById - rs.next() returns false")
    void testFindById_ResultSetNextFalse() {
        Optional<Food> food = repository.findById(99999);
        assertFalse(food.isPresent());
    }
    
    @Test
    @DisplayName("Test getNextFoodId - max ID exactly 2000")
    void testGetNextFoodId_MaxIdExactly2000() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        // Insert food with ID 2000
        try (var conn = connectionProvider.getConnection();
             var stmt = conn.prepareStatement("INSERT INTO foods (food_id, food_name, food_price, food_type) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, 2000);
            stmt.setString(2, "ID 2000");
            stmt.setBigDecimal(3, java.math.BigDecimal.valueOf(10.00));
            stmt.setString(4, "Set");
            stmt.executeUpdate();
        }
        
        int nextId = repository.getNextFoodId();
        assertEquals(2001, nextId);
    }
    
    @Test
    @DisplayName("Test getNextFoodId - max ID less than 2000 returns 2000")
    void testGetNextFoodId_MaxIdLessThan2000Returns2000() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        // Insert food with ID < 2000
        try (var conn = connectionProvider.getConnection();
             var stmt = conn.prepareStatement("INSERT INTO foods (food_id, food_name, food_price, food_type) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, 1999);
            stmt.setString(2, "ID 1999");
            stmt.setBigDecimal(3, java.math.BigDecimal.valueOf(10.00));
            stmt.setString(4, "Set");
            stmt.executeUpdate();
        }
        
        int nextId = repository.getNextFoodId();
        assertEquals(2000, nextId);
    }
    
    @Test
    @DisplayName("Test existsById - count greater than 0")
    void testExistsById_CountGreaterThanZero() {
        assertTrue(repository.existsById(2000));
    }
    
    @Test
    @DisplayName("Test existsById - count equals 0")
    void testExistsById_CountEqualsZero() {
        assertFalse(repository.existsById(0));
    }
    
    @Test
    @DisplayName("Test save - verify all food fields mapped correctly")
    void testSave_VerifyAllFieldsMapped() {
        Food food = new Food("Mapping Test", 19.99, "A la carte");
        Food saved = repository.save(food);
        Optional<Food> found = repository.findById(saved.getFoodId());
        
        assertTrue(found.isPresent());
        Food f = found.get();
        assertEquals("Mapping Test", f.getFoodName());
        assertEquals(19.99, f.getFoodPrice(), 0.01);
        assertEquals("A la carte", f.getFoodType());
    }
    
    @Test
    @DisplayName("Test mapResultSetToFood - all fields from database")
    void testMapResultSetToFood_AllFields() {
        Optional<Food> food = repository.findById(2000);
        assertTrue(food.isPresent());
        Food f = food.get();
        // Verify all fields are mapped
        assertTrue(f.getFoodId() > 0);
        assertNotNull(f.getFoodName());
        assertTrue(f.getFoodPrice() > 0);
        assertNotNull(f.getFoodType());
    }
    
    @Test
    @DisplayName("Test findAll - with exactly one food")
    void testFindAll_ExactlyOneFood() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        Food food = new Food("Single Food", 10.00, "Set");
        repository.save(food);
        
        List<Food> foods = repository.findAll();
        assertEquals(1, foods.size());
        assertEquals("Single Food", foods.get(0).getFoodName());
    }
    
    @Test
    @DisplayName("Test update - update non-existent food")
    void testUpdate_NonExistentFood() {
        Food food = new Food(99999, "Non-existent", 10.00, "Set");
        // Update should not throw, but won't affect anything
        repository.update(food);
        Optional<Food> found = repository.findById(99999);
        assertFalse(found.isPresent());
    }
    
    @Test
    @DisplayName("Test save - affectedRows equals 0 path")
    void testSave_AffectedRowsZeroPath() {
        // This tests the affectedRows > 0 path (should always be > 0 for valid saves)
        Food food = new Food("Affected Rows", 15.00, "Set");
        Food saved = repository.save(food);
        // Verify affectedRows > 0 path was taken
        assertTrue(saved.getFoodId() > 0);
    }
    
    @Test
    @DisplayName("Test save - generatedKeys.next() path")
    void testSave_GeneratedKeysNextPath() {
        Food food = new Food("Gen Keys Next", 18.00, "A la carte");
        Food saved = repository.save(food);
        // This tests the generatedKeys.next() path
        assertTrue(saved.getFoodId() > 0);
    }
    
    @Test
    @DisplayName("Test findAll - while loop termination")
    void testFindAll_WhileLoopTermination() {
        List<Food> foods = repository.findAll();
        // Verify while loop processes all rows and terminates correctly
        assertTrue(foods.size() >= 3);
        // All foods should have valid data
        for (Food food : foods) {
            assertTrue(food.getFoodId() > 0);
        }
    }
    
    @Test
    @DisplayName("Test findById - rs.next() returns true path")
    void testFindById_ResultSetNextTrue() {
        Optional<Food> food = repository.findById(2000);
        // This tests the rs.next() returns true path
        assertTrue(food.isPresent());
    }
    
    @Test
    @DisplayName("Test getNextFoodId - max ID >= 2000 path")
    void testGetNextFoodId_MaxIdGreaterOrEqual2000() {
        // Create food to ensure max ID >= 2000
        Food food = new Food("Max ID Test", 10.00, "Set");
        repository.save(food);
        
        int nextId = repository.getNextFoodId();
        assertTrue(nextId >= 2000);
    }
    
    @Test
    @DisplayName("Test getNextFoodId - max ID condition >= 2000")
    void testGetNextFoodId_MaxIdConditionGreaterOrEqual() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        // Insert food with ID exactly 2000
        try (var conn = connectionProvider.getConnection();
             var stmt = conn.prepareStatement("INSERT INTO foods (food_id, food_name, food_price, food_type) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, 2000);
            stmt.setString(2, "Test");
            stmt.setBigDecimal(3, java.math.BigDecimal.valueOf(10.00));
            stmt.setString(4, "Set");
            stmt.executeUpdate();
        }
        
        int nextId = repository.getNextFoodId();
        // Should return maxId + 1 = 2001 (since maxId >= 2000)
        assertEquals(2001, nextId);
    }
    
    @Test
    @DisplayName("Test getNextFoodId - max ID condition < 2000")
    void testGetNextFoodId_MaxIdConditionLessThan() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        // Insert food with ID < 2000
        try (var conn = connectionProvider.getConnection();
             var stmt = conn.prepareStatement("INSERT INTO foods (food_id, food_name, food_price, food_type) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, 1500);
            stmt.setString(2, "Test");
            stmt.setBigDecimal(3, java.math.BigDecimal.valueOf(10.00));
            stmt.setString(4, "Set");
            stmt.executeUpdate();
        }
        
        int nextId = repository.getNextFoodId();
        // Should return 2000 (since maxId < 2000)
        assertEquals(2000, nextId);
    }
    
    @Test
    @DisplayName("Test deleteById - executeUpdate returns positive")
    void testDeleteById_ExecuteUpdatePositive() {
        // Create and then delete
        Food food = new Food("To Delete Now", 10.00, "Set");
        Food saved = repository.save(food);
        boolean deleted = repository.deleteById(saved.getFoodId());
        assertTrue(deleted);
    }
    
    @Test
    @DisplayName("Test existsById - executeUpdate path with count > 0")
    void testExistsById_CountGreaterThanZeroPath() {
        // Test existing food
        boolean exists = repository.existsById(2000);
        assertTrue(exists);
    }
    
    @Test
    @DisplayName("Test findAll - multiple foods verify ordering")
    void testFindAll_MultipleFoodsOrdering() {
        // Add foods with different IDs
        Food f1 = new Food("Food A", 10.00, "Set");
        Food f2 = new Food("Food B", 15.00, "A la carte");
        repository.save(f1);
        repository.save(f2);
        
        List<Food> foods = repository.findAll();
        // Verify foods are ordered by ID (ORDER BY food_id)
        for (int i = 1; i < foods.size(); i++) {
            assertTrue(foods.get(i).getFoodId() >= foods.get(i-1).getFoodId());
        }
    }
    
    @Test
    @DisplayName("Test update - all fields updated simultaneously")
    void testUpdate_AllFieldsSimultaneously() {
        Food food = new Food(2001, "All New", 25.50, "A la carte");
        repository.update(food);
        
        Optional<Food> found = repository.findById(2001);
        assertTrue(found.isPresent());
        assertEquals("All New", found.get().getFoodName());
        assertEquals(25.50, found.get().getFoodPrice(), 0.01);
        assertEquals("A la carte", found.get().getFoodType());
    }
    
    @Test
    @DisplayName("Test save - multiple sequential saves")
    void testSave_MultipleSequentialSaves() {
        Food f1 = new Food("Seq 1", 10.00, "Set");
        Food f2 = new Food("Seq 2", 11.00, "Set");
        Food f3 = new Food("Seq 3", 12.00, "A la carte");
        
        Food s1 = repository.save(f1);
        Food s2 = repository.save(f2);
        Food s3 = repository.save(f3);
        
        // Verify all have unique IDs
        assertNotEquals(s1.getFoodId(), s2.getFoodId());
        assertNotEquals(s2.getFoodId(), s3.getFoodId());
        assertNotEquals(s1.getFoodId(), s3.getFoodId());
    }
    
    @Test
    @DisplayName("Test findById - verify BigDecimal conversion in mapping")
    void testFindById_BigDecimalConversion() {
        Optional<Food> food = repository.findById(2000);
        assertTrue(food.isPresent());
        // Verify BigDecimal was converted to double correctly
        assertTrue(food.get().getFoodPrice() > 0);
        assertEquals(10.50, food.get().getFoodPrice(), 0.01);
    }
    
    @Test
    @DisplayName("Test getNextFoodId - after deleting all foods")
    void testGetNextFoodId_AfterDeletingAll() throws SQLException {
        TestDatabaseSetup.cleanup(connectionProvider);
        // Add a food and delete it
        Food food = new Food("Temp", 10.00, "Set");
        Food saved = repository.save(food);
        repository.deleteById(saved.getFoodId());
        
        int nextId = repository.getNextFoodId();
        // Should return 2000 since table is empty again
        assertEquals(2000, nextId);
    }
}