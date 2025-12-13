package repository.impl;

import config.ConnectionProvider;
import config.DatabaseConnection;
import config.TestDatabaseSetup;
import model.Food;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FoodRepositoryTest {
    
    private FoodRepository repository;
    private ConnectionProvider connectionProvider;
    
    // Use MySQL mode to simulate production environment
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
    @DisplayName("Sanity Check: Default constructor initialization")
    void shouldInitializeWithDefaultConstructor() {
        runWithSuppressedError(() -> {
            FoodRepository repo = new FoodRepository();
            assertNotNull(repo);
        });
    }

    @Test
    @DisplayName("Sanity Check: Constructor with provider")
    void shouldInitializeWithProvider() {
        FoodRepository repo = new FoodRepository(connectionProvider);
        assertNotNull(repo);
        assertTrue(repo.findById(2000).isPresent());
    }

    // =========================================================================
    // Retrieval Operations (Find, Exists)
    // =========================================================================
    
    @Nested
    @DisplayName("Retrieval Operations")
    class RetrievalTests {

        @Test
        @DisplayName("Find By ID: Should return valid food")
        void shouldFindFoodById() {
            Optional<Food> food = repository.findById(2000);
            
            assertTrue(food.isPresent());
            Food f = food.get();
            
            assertAll("Food Content",
                () -> assertEquals(2000, f.getFoodId()),
                () -> assertEquals("Chicken Rice", f.getFoodName()),
                () -> assertEquals(10.50, f.getFoodPrice(), 0.01),
                () -> assertEquals("Set", f.getFoodType())
            );
        }

        @Test
        @DisplayName("Find By ID: Should return empty for non-existent ID")
        void shouldReturnEmptyWhenIdNotFound() {
            assertFalse(repository.findById(9999).isPresent());
            assertFalse(repository.findById(99999).isPresent());
        }

        @Test
        @DisplayName("Find All: Should return all records")
        void shouldReturnAllFoods() {
            List<Food> foods = repository.findAll();
            
            assertTrue(foods.size() >= 3);
            
            // Verify content integrity
            for (Food food : foods) {
                assertTrue(food.getFoodId() > 0);
                assertNotNull(food.getFoodName());
                assertTrue(food.getFoodPrice() > 0);
                assertNotNull(food.getFoodType());
            }
            
            // Verify ordering (Food ID ascending)
            for (int i = 1; i < foods.size(); i++) {
                assertTrue(foods.get(i).getFoodId() >= foods.get(i-1).getFoodId());
            }
        }

        @Test
        @DisplayName("Find All: Should return empty list if DB is empty")
        void shouldReturnEmptyListWhenNoRecords() throws SQLException {
            TestDatabaseSetup.cleanup(connectionProvider);
            assertTrue(repository.findAll().isEmpty());
        }

        @Test
        @DisplayName("Find All: Should correctly handle single record")
        void shouldReturnListWithSingleRecord() throws SQLException {
            TestDatabaseSetup.cleanup(connectionProvider);
            repository.save(new Food("Single Food", 10.00, "Set"));
            
            List<Food> foods = repository.findAll();
            assertEquals(1, foods.size());
            assertEquals("Single Food", foods.get(0).getFoodName());
        }

        @Test
        @DisplayName("Find By Name: Should return exact match")
        void shouldFindFoodByName() {
            Optional<Food> food = repository.findByName("Chicken Rice");
            assertTrue(food.isPresent());
            assertEquals(10.50, food.get().getFoodPrice(), 0.01);
        }

        @Test
        @DisplayName("Find By Name: Should be case-insensitive")
        void shouldFindFoodByNameIgnoringCase() {
            assertTrue(repository.findByName("CHICKEN RICE").isPresent());
            assertTrue(repository.findByName("chicken rice").isPresent());
            assertTrue(repository.findByName("ChIcKeN rIcE").isPresent());
        }

        @Test
        @DisplayName("Find By Name: Should handle new records")
        void shouldFindNewlySavedFoodByName() {
            Food saved = repository.save(new Food("New Test Food", 15.00, "Set"));
            
            Optional<Food> found = repository.findByName("New Test Food");
            assertTrue(found.isPresent());
            assertEquals(saved.getFoodId(), found.get().getFoodId());
        }

        @Test
        @DisplayName("Find By Name: Should return empty for unknown name")
        void shouldReturnEmptyForUnknownName() {
            assertFalse(repository.findByName("Non-existent Food").isPresent());
            assertFalse(repository.findByName("").isPresent());
            assertFalse(repository.findByName(null).isPresent());
        }
    }

    @Nested
    @DisplayName("Existence Checks")
    class ExistenceTests {

        @Test
        @DisplayName("Exists By ID: Should return true for existing IDs")
        void shouldReturnTrueIfIdExists() {
            assertTrue(repository.existsById(2000));
            assertTrue(repository.existsById(2001));
            assertTrue(repository.existsById(2002));
        }

        @Test
        @DisplayName("Exists By ID: Should return false for invalid IDs")
        void shouldReturnFalseIfIdDoesNotExist() {
            assertFalse(repository.existsById(9999));
            assertFalse(repository.existsById(0));
            assertFalse(repository.existsById(99999));
        }

        @Test
        @DisplayName("Exists By Name: Should check name existence")
        void shouldReturnTrueIfNameExists() {
            assertTrue(repository.existsByName("Chicken Rice"));
            assertTrue(repository.existsByName("Nasi Lemak"));
        }

        @Test
        @DisplayName("Exists By Name: Should be case-insensitive")
        void shouldCheckNameExistenceIgnoringCase() {
            assertTrue(repository.existsByName("CHICKEN RICE"));
            assertTrue(repository.existsByName("chicken rice"));
        }

        @Test
        @DisplayName("Exists By Name: Should handle duplicates or similar names")
        void shouldHandleSimilarNames() {
            repository.save(new Food("Test Food A", 10.00, "Set"));
            repository.save(new Food("Test Food B", 15.00, "A la carte"));
            
            assertTrue(repository.existsByName("Test Food A"));
            assertTrue(repository.existsByName("Test Food B"));
        }

        @Test
        @DisplayName("Exists By Name: Should return false for unknown names")
        void shouldReturnFalseForUnknownNames() {
            assertFalse(repository.existsByName("Ghost Food"));
            assertFalse(repository.existsByName(""));
            assertFalse(repository.existsByName(null));
        }
    }

    // =========================================================================
    // Persistence Operations (Save, Update, Delete)
    // =========================================================================

    @Nested
    @DisplayName("Persistence Operations")
    class PersistenceTests {

        @Test
        @DisplayName("Save: Should persist new food and generate ID")
        void shouldSaveNewFood() {
            Food food = new Food("Test Food", 15.00, "Set");
            Food saved = repository.save(food);
            
            assertTrue(saved.getFoodId() > 0);
            assertEquals("Test Food", saved.getFoodName());
            
            // Verify DB state
            assertTrue(repository.existsById(saved.getFoodId()));
        }

        @Test
        @DisplayName("Save: Should handle all fields correctly")
        void shouldSaveAllFields() {
            Food food = new Food("Complete Food", 25.50, "A la carte");
            Food saved = repository.save(food);
            
            Food found = repository.findById(saved.getFoodId()).orElseThrow();
            assertEquals("Complete Food", found.getFoodName());
            assertEquals(25.50, found.getFoodPrice(), 0.01);
            assertEquals("A la carte", found.getFoodType());
        }

        @Test
        @DisplayName("Save: Should generate new ID even if one is provided")
        void shouldIgnorePreSetIdsOnSave() {
            Food food = new Food(9999, "Auto ID Food", 12.00, "Set");
            Food saved = repository.save(food);
            
            assertTrue(saved.getFoodId() > 0);
            assertNotEquals(9999, saved.getFoodId()); // Should be a new generated key
        }

        @Test
        @DisplayName("Save: Should generate unique IDs for multiple saves")
        void shouldGenerateUniqueIds() {
            Food s1 = repository.save(new Food("Food 1", 10.00, "Set"));
            Food s2 = repository.save(new Food("Food 2", 15.00, "Set"));
            Food s3 = repository.save(new Food("Food 3", 20.00, "Set"));
            
            assertNotEquals(s1.getFoodId(), s2.getFoodId());
            assertNotEquals(s2.getFoodId(), s3.getFoodId());
        }

        @Test
        @DisplayName("Save: Should handle edge cases (Long strings, Max values)")
        void shouldHandleDataEdgeCases() {
            // Long name
            String longName = "A".repeat(100);
            Food longFood = repository.save(new Food(longName, 10.00, "Set"));
            assertEquals(longName, longFood.getFoodName());

            // Max Price
            Food maxPrice = repository.save(new Food("Expensive", 999999.99, "Set"));
            assertEquals(999999.99, maxPrice.getFoodPrice(), 0.01);

            // Min Price
            Food minPrice = repository.save(new Food("Cheap", 0.01, "Set"));
            assertEquals(0.01, minPrice.getFoodPrice(), 0.01);
            
            // Decimal precision (rounding)
            Food decimal = repository.save(new Food("Rounding", 12.345, "Set"));
            // Expect rounding to 2 decimal places by DB
            assertEquals(12.35, repository.findById(decimal.getFoodId()).get().getFoodPrice(), 0.01);
        }

        @Test
        @DisplayName("Update: Should modify existing food")
        void shouldUpdateExistingFood() {
            Food food = new Food(2000, "Updated Chicken Rice", 12.00, "Set");
            Food updated = repository.update(food);
            
            assertEquals(12.00, updated.getFoodPrice(), 0.01);
            
            Food fromDb = repository.findById(2000).orElseThrow();
            assertEquals("Updated Chicken Rice", fromDb.getFoodName());
            assertEquals(12.00, fromDb.getFoodPrice(), 0.01);
        }

        @Test
        @DisplayName("Update: Should handle full object updates")
        void shouldUpdateAllFields() {
            Food original = repository.findById(2001).orElseThrow();
            
            Food updateReq = new Food(2001, "Completely New", 25.99, "A la carte");
            repository.update(updateReq);
            
            Food found = repository.findById(2001).orElseThrow();
            assertNotEquals(original.getFoodName(), found.getFoodName());
            assertEquals("Completely New", found.getFoodName());
            assertEquals(25.99, found.getFoodPrice(), 0.01);
            assertEquals("A la carte", found.getFoodType());
        }

        @Test
        @DisplayName("Update: Should verify existence path")
        void shouldUpdateIfFoodExists() {
            Food food = new Food(2000, "Existing Name", 10.00, "Set");
            repository.update(food);
            assertEquals("Existing Name", repository.findById(2000).get().getFoodName());
        }

        @Test
        @DisplayName("Update: Should do nothing for non-existent food")
        void shouldNotFailOnNonExistentUpdate() {
            Food food = new Food(99999, "Ghost", 10.00, "Set");
            repository.update(food); // Should not throw
            assertFalse(repository.findById(99999).isPresent());
        }

        @Test
        @DisplayName("Delete: Should remove food by ID")
        void shouldDeleteFoodById() {
            boolean deleted = repository.deleteById(2001);
            assertTrue(deleted);
            assertFalse(repository.findById(2001).isPresent());
        }

        @Test
        @DisplayName("Delete: Should return true if deletion successful")
        void shouldReturnTrueOnSuccessfulDeletion() {
            Food temp = repository.save(new Food("Temp", 10.0, "Set"));
            assertTrue(repository.deleteById(temp.getFoodId()));
        }

        @Test
        @DisplayName("Delete: Should return false for non-existent ID")
        void shouldReturnFalseOnFailedDeletion() {
            assertFalse(repository.deleteById(9999));
            assertFalse(repository.deleteById(99999));
        }
    }

    // =========================================================================
    // ID Generation Tests
    // =========================================================================

    @Nested
    @DisplayName("ID Generation Logic")
    class IdGenerationTests {

        @Test
        @DisplayName("Should return next ID incremented from max")
        void shouldCalculateNextId() {
            // Initial state (2000, 2001, 2002) -> Next should be >= 2003 or at least >= 2000
            int nextId = repository.getNextFoodId();
            assertTrue(nextId >= 2000);
        }

        @Test
        @DisplayName("Should default to 2000 if table is empty")
        void shouldDefaultTo2000ForEmptyTable() throws SQLException {
            TestDatabaseSetup.cleanup(connectionProvider);
            assertEquals(2000, repository.getNextFoodId());
        }
        
        @Test
        @DisplayName("Should return 2000 if current max ID < 1999")
        void shouldReturn2000IfMaxIdIsSmall() throws SQLException {
            TestDatabaseSetup.cleanup(connectionProvider);
            insertRawFood(1500, "Small ID"); // Max ID is 1500
            
            assertEquals(2000, repository.getNextFoodId());
        }
        
        @Test
        @DisplayName("Should return 2000 if current max ID is 1999")
        void shouldReturn2000IfMaxIdIs1999() throws SQLException {
            TestDatabaseSetup.cleanup(connectionProvider);
            insertRawFood(1999, "Boundary");
            
            assertEquals(2000, repository.getNextFoodId());
        }

        @Test
        @DisplayName("Should increment correctly from 2000")
        void shouldIncrementFrom2000() throws SQLException {
            TestDatabaseSetup.cleanup(connectionProvider);
            insertRawFood(2000, "Boundary Start");
            
            assertEquals(2001, repository.getNextFoodId());
        }

        @Test
        @DisplayName("Should increment correctly from 2001")
        void shouldIncrementFrom2001() throws SQLException {
            TestDatabaseSetup.cleanup(connectionProvider);
            insertRawFood(2001, "Existing");
            
            assertEquals(2002, repository.getNextFoodId());
        }

        @Test
        @DisplayName("Should handle database reset and refill")
        void shouldHandleResetSequence() throws SQLException {
            TestDatabaseSetup.cleanup(connectionProvider);
            Food f = repository.save(new Food("Temp", 1.0, "Set"));
            repository.deleteById(f.getFoodId());
            
            // Table empty again
            assertEquals(2000, repository.getNextFoodId());
        }
    }

    // =========================================================================
    // Error Handling & Mock Tests (SQLException paths)
    // =========================================================================

    @Nested
    @DisplayName("Exception Handling")
    class ExceptionTests {

        @Test
        @DisplayName("Save: Should throw RuntimeException on DB error")
        void shouldThrowRuntimeOnSaveError() {
            runWithSuppressedError(() -> {
                ConnectionProvider mockProvider = createMockProviderWithException();
                FoodRepository repo = new FoodRepository(mockProvider);
                Food food = new Food("Test", 10.0, "Set");

                RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.save(food));
                assertTrue(ex.getMessage().contains("Failed to save food"));
            });
        }
        
        @Test
        @DisplayName("Save: Should handle connection error")
        void shouldHandleConnectionErrorOnSave() {
            runWithSuppressedError(() -> {
                ConnectionProvider mockProvider = mock(ConnectionProvider.class);
                when(mockProvider.getConnection()).thenThrow(new SQLException("Conn fail"));
                
                FoodRepository repo = new FoodRepository(mockProvider);
                assertThrows(RuntimeException.class, () -> repo.save(new Food("F", 1.0, "S")));
            });
        }

        @Test
        @DisplayName("Update: Should throw RuntimeException on DB error")
        void shouldThrowRuntimeOnUpdateError() {
            runWithSuppressedError(() -> {
                ConnectionProvider mockProvider = createMockProviderWithException();
                FoodRepository repo = new FoodRepository(mockProvider);
                Food food = new Food(2000, "Test", 10.0, "Set");

                RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.update(food));
                assertTrue(ex.getMessage().contains("Failed to update food"));
            });
        }
        
        @Test
        @DisplayName("Update: Should handle connection error")
        void shouldHandleConnectionErrorOnUpdate() {
            runWithSuppressedError(() -> {
                ConnectionProvider mockProvider = mock(ConnectionProvider.class);
                when(mockProvider.getConnection()).thenThrow(new SQLException("Conn fail"));
                
                FoodRepository repo = new FoodRepository(mockProvider);
                assertThrows(RuntimeException.class, () -> repo.update(new Food(2000, "F", 1.0, "S")));
            });
        }

        @Test
        @DisplayName("FindById: Should return empty on DB error")
        void shouldReturnEmptyOnFindByIdError() {
            runWithSuppressedError(() -> {
                FoodRepository repo = new FoodRepository(createMockProviderWithException());
                assertFalse(repo.findById(2000).isPresent());
            });
        }

        @Test
        @DisplayName("FindByName: Should return empty on DB error")
        void shouldReturnEmptyOnFindByNameError() {
            runWithSuppressedError(() -> {
                FoodRepository repo = new FoodRepository(createMockProviderWithException());
                assertFalse(repo.findByName("Test").isPresent());
            });
        }

        @Test
        @DisplayName("FindAll: Should return empty list on DB error")
        void shouldReturnEmptyListOnFindAllError() {
            runWithSuppressedError(() -> {
                FoodRepository repo = new FoodRepository(createMockProviderWithException());
                assertTrue(repo.findAll().isEmpty());
            });
        }

        @Test
        @DisplayName("ExistsById: Should return false on DB error")
        void shouldReturnFalseOnExistsByIdError() {
            runWithSuppressedError(() -> {
                FoodRepository repo = new FoodRepository(createMockProviderWithException());
                assertFalse(repo.existsById(2000));
            });
        }

        @Test
        @DisplayName("ExistsByName: Should return false on DB error")
        void shouldReturnFalseOnExistsByNameError() {
            runWithSuppressedError(() -> {
                FoodRepository repo = new FoodRepository(createMockProviderWithException());
                assertFalse(repo.existsByName("Test"));
            });
        }

        @Test
        @DisplayName("Delete: Should return false on DB error")
        void shouldReturnFalseOnDeleteError() {
            runWithSuppressedError(() -> {
                FoodRepository repo = new FoodRepository(createMockProviderWithException());
                assertFalse(repo.deleteById(2000));
            });
        }

        @Test
        @DisplayName("GetNextId: Should default to 2000 on DB error")
        void shouldReturnDefaultIdOnError() {
            runWithSuppressedError(() -> {
                FoodRepository repo = new FoodRepository(createMockProviderWithException());
                assertEquals(2000, repo.getNextFoodId());
            });
        }
        
        @Test
        @DisplayName("Save: Should handle missing generated keys gracefully")
        void shouldHandleMissingGeneratedKeys() throws SQLException {
            // Setup mock that says "Success (1 row)" but returns no keys
            ConnectionProvider mockProvider = mock(ConnectionProvider.class);
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);
            ResultSet mockKeys = mock(ResultSet.class);
            
            when(mockProvider.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1); // Rows affected > 0
            when(mockStmt.getGeneratedKeys()).thenReturn(mockKeys);
            when(mockKeys.next()).thenReturn(false); // No keys returned
            
            FoodRepository repo = new FoodRepository(mockProvider);
            Food result = repo.save(new Food("Test", 10.0, "Set"));
            
            // Logic dictates ID is not set (0), but no exception thrown
            assertEquals(0, result.getFoodId());
        }
    }

    // =========================================================================
    // Test Utilities
    // =========================================================================
    /**
     * Helper to insert raw data directly into H2 to simulate pre-existing conditions
     * without relying on the 'save' method of the class under test.
     */
    private void insertRawFood(int id, String name) throws SQLException {
        try (var conn = connectionProvider.getConnection();
             var stmt = conn.prepareStatement(
                 "INSERT INTO foods (food_id, food_name, food_price, food_type) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setBigDecimal(3, java.math.BigDecimal.valueOf(10.00));
            stmt.setString(4, "Set");
            stmt.executeUpdate();
        }
    }

    //Creates a Mock ConnectionProvider that always throws SQLExceptions.
    private ConnectionProvider createMockProviderWithException() throws RuntimeException {
        try {
            ConnectionProvider mockProvider = mock(ConnectionProvider.class);
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);
            
            when(mockProvider.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockStmt);
            
            // Configure all execution methods to throw
            when(mockStmt.executeUpdate()).thenThrow(new SQLException("Simulated DB Error"));
            when(mockStmt.executeQuery()).thenThrow(new SQLException("Simulated DB Error"));
            
            return mockProvider;
        } catch (SQLException e) {
            throw new RuntimeException("Test setup failed", e);
        }
    }

    //Helper to suppress System.err output during tests that intentionally provoke stack traces.
    private void runWithSuppressedError(Executable testLogic) {
        PrintStream originalErr = System.err;
        try {
            System.setErr(new PrintStream(new ByteArrayOutputStream()));
            testLogic.execute();
        } catch (Throwable e) {
            // Rethrow as unchecked to fail the test if something unexpected happened
            throw new RuntimeException(e);
        } finally {
            System.setErr(originalErr);
        }
    }
}