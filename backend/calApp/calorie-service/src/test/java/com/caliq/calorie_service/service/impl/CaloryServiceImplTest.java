package com.caliq.calorie_service.service.impl;

import com.caliq.calorie_service.exeptions.UserNotFoundException;
import com.caliq.calorie_service.models.dto.CalendarResponse;
import com.caliq.calorie_service.models.dto.MainPageResponseNew;
import com.caliq.calorie_service.models.entity.CaloryLogsEntity;
import com.caliq.calorie_service.models.entity.MealEntity;
import com.caliq.calorie_service.models.entity.UserModel;
import com.caliq.calorie_service.models.entity.WeightLogsEntity;
import com.caliq.calorie_service.models.response.MainPageResponse;
import com.caliq.calorie_service.models.response.MealByDateResponse;
import com.caliq.calorie_service.models.types.GoalType;
import com.caliq.calorie_service.models.types.MealType;
import com.caliq.calorie_service.repository.CaloryLogsRepository;
import com.caliq.calorie_service.repository.CaloryRepository;
import com.caliq.calorie_service.repository.UserRepository;
import com.caliq.calorie_service.repository.WeightLogsRepository;
import com.caliq.core.dto.MealDto;
import com.caliq.core.message.MealMessage;
import com.caliq.core.message.WaterMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaloryServiceImplTest {

    @Mock
    private CaloryRepository caloryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CaloryLogsRepository caloryLogsRepository;

    @Mock
    private WeightLogsRepository weightLogsRepository;


    @InjectMocks
    private CaloryServiceImpl caloryService;

    private UserModel testUser;
    private MealDto testMealDto;
    private LocalDate testDate;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        testDate = LocalDate.of(2024, 1, 15);

        // Setup test user
        testUser = new UserModel();
        testUser.setId(1L);
        testUser.setCurrentWeight(BigDecimal.valueOf(70));
        testUser.setBmr(BigDecimal.valueOf(2000));
        testUser.setWaterNeeds(BigDecimal.valueOf(2500));
        testUser.setGoalType(GoalType.MAINTENANCE);

        // Setup test meal DTO
        MealDto.Dish dish1 = new MealDto.Dish(
                "OPENFOODFACT",
                "123456",
                "Test Food",
                BigDecimal.valueOf(300),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(30),
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(3)
        );
        MealDto.Dish dish2 = new MealDto.Dish(
                "FOODSECRET",
                "789012",
                "Another Food",
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(8),
                BigDecimal.valueOf(15),
                BigDecimal.valueOf(25),
                BigDecimal.valueOf(4),
                BigDecimal.valueOf(2)
        );
        testMealDto = new MealDto(new MealDto.Dishes(List.of(dish1, dish2)));

        // Inject ObjectMapper using reflection since it's @Autowired
        Field objectMapperField = CaloryServiceImpl.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        objectMapperField.set(caloryService, objectMapper);
    }

    @Test
    void testSaveMealToDb_NewMeal() {
        // Arrange
        Long userId = 1L;
        MealType mealType = MealType.BREAKFAST;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(caloryRepository.findByUser_IdAndDateTimeAndMealType(userId, testDate, mealType))
                .thenReturn(Optional.empty());
        when(caloryRepository.save(any(MealEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(caloryLogsRepository.findByUserAndDate(any(), any())).thenReturn(Optional.empty());
        when(caloryLogsRepository.save(any(CaloryLogsEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String result = caloryService.saveMealToDb(userId, testMealDto, testDate, mealType);

        // Assert
        assertEquals("Meal saved successfully", result);
        verify(caloryRepository, times(1)).save(any(MealEntity.class));
        verify(caloryLogsRepository, times(1)).save(any(CaloryLogsEntity.class));
    }

    @Test
    void testSaveMealToDb_ExistingMeal() {
        // Arrange
        Long userId = 1L;
        MealType mealType = MealType.LUNCH;

        MealEntity existingMeal = new MealEntity();
        existingMeal.setId(1L);
        existingMeal.setUser(testUser);
        existingMeal.setMealType(mealType);
        existingMeal.setDateTime(testDate);
        existingMeal.setMeal(objectMapper.valueToTree(new MealDto(new MealDto.Dishes(List.of()))));

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(caloryRepository.findByUser_IdAndDateTimeAndMealType(userId, testDate, mealType))
                .thenReturn(Optional.of(existingMeal));
        when(caloryRepository.save(any(MealEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(caloryLogsRepository.findByUserAndDate(any(), any())).thenReturn(Optional.empty());
        when(caloryLogsRepository.save(any(CaloryLogsEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String result = caloryService.saveMealToDb(userId, testMealDto, testDate, mealType);

        // Assert
        assertEquals("Meal saved successfully", result);
        verify(caloryRepository, times(1)).save(any(MealEntity.class));
    }

    @Test
    void testSaveMealToDb_UserNotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            caloryService.saveMealToDb(userId, testMealDto, testDate, MealType.BREAKFAST);
        });
    }

    @Test
    void testGetMealByDate_MealExists() {
        // Arrange
        Long userId = 1L;
        MealType mealType = MealType.DINNER;

        MealEntity mealEntity = new MealEntity();
        mealEntity.setMeal(objectMapper.valueToTree(testMealDto));
        mealEntity.setMealType(mealType);
        mealEntity.setDateTime(testDate);

        when(caloryRepository.findByUser_IdAndDateTimeAndMealType(userId, testDate, mealType))
                .thenReturn(Optional.of(mealEntity));

        // Act
        MealByDateResponse response = caloryService.getMealByDate(userId, testDate, mealType);

        // Assert
        assertNotNull(response);
        assertEquals(testDate.toString(), response.date());
        assertEquals(mealType, response.mealType());
        assertNotNull(response.mealDto());
    }

    @Test
    void testGetMealByDate_MealNotExists() {
        // Arrange
        Long userId = 1L;
        MealType mealType = MealType.SNACK;

        when(caloryRepository.findByUser_IdAndDateTimeAndMealType(userId, testDate, mealType))
                .thenReturn(Optional.empty());

        // Act
        MealByDateResponse response = caloryService.getMealByDate(userId, testDate, mealType);

        // Assert
        assertNotNull(response);
        assertEquals(testDate.toString(), response.date());
        assertEquals(mealType, response.mealType());
        assertNotNull(response.mealDto());
    }

    @Test
    void testShowMainPageInfo_Success() {
        // Arrange
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        CaloryLogsEntity todayLog = new CaloryLogsEntity();
        todayLog.setUser(testUser);
        todayLog.setDate(today);
        todayLog.setTotalCalory(BigDecimal.valueOf(1500));
        todayLog.setProtein(BigDecimal.valueOf(100));
        todayLog.setFat(BigDecimal.valueOf(50));
        todayLog.setCarbohydrates(BigDecimal.valueOf(200));
        todayLog.setDrankWaterMl(BigDecimal.valueOf(1500));

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(caloryLogsRepository.findByUserAndDate(testUser, today))
                .thenReturn(Optional.of(todayLog));
        when(caloryLogsRepository.findAllByUser_IdAndDateBetweenOrderByDateAsc(
                eq(userId), any(LocalDate.class), eq(today))).thenReturn(Collections.emptyList());
        when(userRepository.findBmrByUserId(userId)).thenReturn(BigDecimal.valueOf(2000));
        when(userRepository.findWaterNeedsByUserId(userId)).thenReturn(BigDecimal.valueOf(2500));

        // Act
        MainPageResponse response = caloryService.showMainPageInfo(userId);

        // Assert
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(2000), response.getCalorieGoal());
        assertEquals(BigDecimal.valueOf(2500), response.getWaterGoal());
        assertEquals(BigDecimal.valueOf(1500), response.getCalorieValue());
        assertEquals(BigDecimal.valueOf(100), response.getProteinValue());
        assertEquals(BigDecimal.valueOf(50), response.getFatValue());
        assertEquals(BigDecimal.valueOf(200), response.getCarbohydrateValue());
        assertEquals(BigDecimal.valueOf(1500), response.getWaterValue());
    }

    @Test
    void testShowMainPageInfo_CreatesNewLog() {
        // Arrange
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(caloryLogsRepository.findByUserAndDate(testUser, today))
                .thenReturn(Optional.empty());
        when(caloryLogsRepository.save(any(CaloryLogsEntity.class))).thenAnswer(invocation -> {
            CaloryLogsEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        when(caloryLogsRepository.findAllByUser_IdAndDateBetweenOrderByDateAsc(
                eq(userId), any(LocalDate.class), eq(today))).thenReturn(Collections.emptyList());
        when(userRepository.findBmrByUserId(userId)).thenReturn(BigDecimal.valueOf(2000));
        when(userRepository.findWaterNeedsByUserId(userId)).thenReturn(BigDecimal.valueOf(2500));

        // Act
        MainPageResponse response = caloryService.showMainPageInfo(userId);

        // Assert
        assertNotNull(response);
        verify(caloryLogsRepository, times(1)).save(any(CaloryLogsEntity.class));
    }

    @Test
    void testSaveToCaloryLogs_NewLog() {
        // Arrange
        when(caloryLogsRepository.findByUserAndDate(testUser, testDate))
                .thenReturn(Optional.empty());
        when(caloryLogsRepository.save(any(CaloryLogsEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        caloryService.saveToCaloryLogs(testMealDto, testDate, testUser);

        // Assert
        ArgumentCaptor<CaloryLogsEntity> captor = ArgumentCaptor.forClass(CaloryLogsEntity.class);
        verify(caloryLogsRepository, times(1)).save(captor.capture());
        CaloryLogsEntity saved = captor.getValue();
        assertEquals(BigDecimal.valueOf(500), saved.getTotalCalory()); // 300 + 200
        assertEquals(BigDecimal.valueOf(35), saved.getProtein()); // 20 + 15
        assertEquals(BigDecimal.valueOf(18), saved.getFat()); // 10 + 8
        assertEquals(BigDecimal.valueOf(55), saved.getCarbohydrates()); // 30 + 25
    }

    @Test
    void testSaveToCaloryLogs_ExistingLog() {
        // Arrange
        CaloryLogsEntity existingLog = new CaloryLogsEntity();
        existingLog.setUser(testUser);
        existingLog.setDate(testDate);
        existingLog.setTotalCalory(BigDecimal.valueOf(1000));
        existingLog.setProtein(BigDecimal.valueOf(50));
        existingLog.setFat(BigDecimal.valueOf(30));
        existingLog.setCarbohydrates(BigDecimal.valueOf(100));

        when(caloryLogsRepository.findByUserAndDate(testUser, testDate))
                .thenReturn(Optional.of(existingLog));
        when(caloryLogsRepository.save(any(CaloryLogsEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        caloryService.saveToCaloryLogs(testMealDto, testDate, testUser);

        // Assert
        ArgumentCaptor<CaloryLogsEntity> captor = ArgumentCaptor.forClass(CaloryLogsEntity.class);
        verify(caloryLogsRepository, times(1)).save(captor.capture());
        CaloryLogsEntity saved = captor.getValue();
        assertEquals(BigDecimal.valueOf(1500), saved.getTotalCalory()); // 1000 + 500
        assertEquals(BigDecimal.valueOf(85), saved.getProtein()); // 50 + 35
    }

    @Test
    void testSaveToCaloryLogs_NullMealDto() {
        // Arrange
        when(caloryLogsRepository.findByUserAndDate(testUser, testDate))
                .thenReturn(Optional.empty());
        when(caloryLogsRepository.save(any(CaloryLogsEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        caloryService.saveToCaloryLogs(null, testDate, testUser);

        // Assert
        ArgumentCaptor<CaloryLogsEntity> captor = ArgumentCaptor.forClass(CaloryLogsEntity.class);
        verify(caloryLogsRepository, times(1)).save(captor.capture());
        CaloryLogsEntity saved = captor.getValue();
        assertEquals(BigDecimal.ZERO, saved.getTotalCalory());
    }

    @Test
    void testAddWaterToLogs_Success() {
        // Arrange
        Long userId = 1L;
        BigDecimal waterAmount = BigDecimal.valueOf(500);
        LocalDate date = LocalDate.now();

        CaloryLogsEntity existingLog = new CaloryLogsEntity();
        existingLog.setUser(testUser);
        existingLog.setDate(date);
        existingLog.setDrankWaterMl(BigDecimal.valueOf(1000));

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(caloryLogsRepository.findByUserAndDate(testUser, date))
                .thenReturn(Optional.of(existingLog));
        when(caloryLogsRepository.save(any(CaloryLogsEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BigDecimal result = caloryService.addWaterToLogs(userId, waterAmount, date);

        // Assert
        assertEquals(BigDecimal.valueOf(1500), result);
        verify(caloryLogsRepository, times(1)).save(any(CaloryLogsEntity.class));
    }

    @Test
    void testAddWaterToLogs_CreatesNewLog() {
        // Arrange
        Long userId = 1L;
        BigDecimal waterAmount = BigDecimal.valueOf(500);
        LocalDate date = LocalDate.now();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(caloryLogsRepository.findByUserAndDate(testUser, date))
                .thenReturn(Optional.empty());
        when(caloryLogsRepository.save(any(CaloryLogsEntity.class))).thenAnswer(invocation -> {
            CaloryLogsEntity entity = invocation.getArgument(0);
            entity.setDrankWaterMl(waterAmount);
            return entity;
        });

        // Act
        BigDecimal result = caloryService.addWaterToLogs(userId, waterAmount, date);

        // Assert
        assertEquals(waterAmount, result);
        verify(caloryLogsRepository, times(1)).save(any(CaloryLogsEntity.class));
    }

    @Test
    void testAddWaterToLogs_UserNotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            caloryService.addWaterToLogs(userId, BigDecimal.valueOf(500), testDate);
        });
    }

    @Test
    void testShowMainPageInfoNew_Success() {
        // Arrange
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        CaloryLogsEntity todayLog = new CaloryLogsEntity();
        todayLog.setUser(testUser);
        todayLog.setDate(today);
        todayLog.setTotalCalory(BigDecimal.valueOf(1500));
        todayLog.setProtein(BigDecimal.valueOf(100));
        todayLog.setFat(BigDecimal.valueOf(50));
        todayLog.setCarbohydrates(BigDecimal.valueOf(200));
        todayLog.setDrankWaterMl(BigDecimal.valueOf(1500));

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(caloryLogsRepository.findByUserAndDate(testUser, today))
                .thenReturn(Optional.of(todayLog));
        when(caloryLogsRepository.findAllByUser_IdAndDateBetweenOrderByDateAsc(
                eq(userId), any(LocalDate.class), any(LocalDate.class))).thenReturn(Collections.emptyList());
        when(caloryRepository.findByUser_IdAndDateTime(userId, today))
                .thenReturn(Collections.emptyList());

        // Act
        MainPageResponseNew response = caloryService.showMainPageInfoNew(userId);

        // Assert
        assertNotNull(response);
        assertNotNull(response.weekCalory());
        assertNotNull(response.todayCalory());
        assertNotNull(response.todayWater());
        assertNotNull(response.todayCarbs());
        assertNotNull(response.todayProtein());
        assertNotNull(response.todayFat());
    }

    @Test
    void testShowMainPageInfoNew_WithGoalTypeLoss() {
        // Arrange
        Long userId = 1L;
        testUser.setGoalType(GoalType.LOSS);
        LocalDate today = LocalDate.now();

        CaloryLogsEntity todayLog = new CaloryLogsEntity();
        todayLog.setUser(testUser);
        todayLog.setDate(today);
        todayLog.setTotalCalory(BigDecimal.ZERO);
        todayLog.setProtein(BigDecimal.ZERO);
        todayLog.setFat(BigDecimal.ZERO);
        todayLog.setCarbohydrates(BigDecimal.ZERO);
        todayLog.setDrankWaterMl(BigDecimal.ZERO);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(caloryLogsRepository.findByUserAndDate(testUser, today))
                .thenReturn(Optional.of(todayLog));
        when(caloryLogsRepository.findAllByUser_IdAndDateBetweenOrderByDateAsc(
                eq(userId), any(LocalDate.class), any(LocalDate.class))).thenReturn(Collections.emptyList());
        when(caloryRepository.findByUser_IdAndDateTime(userId, today))
                .thenReturn(Collections.emptyList());

        // Act
        MainPageResponseNew response = caloryService.showMainPageInfoNew(userId);

        // Assert
        assertNotNull(response);
        // For LOSS goal: protein 35%, fat 30%, carbs 35%
        // BMR = 2000, so protein norm = 2000 * 0.35 / 4 = 175
        assertEquals(0, BigDecimal.valueOf(175.00).compareTo(response.todayProtein().todayProteinNorm()));
    }

    @Test
    void testShowMainPageInfoNew_WithGoalTypeGain() {
        // Arrange
        Long userId = 1L;
        testUser.setGoalType(GoalType.GAIN);
        LocalDate today = LocalDate.now();

        CaloryLogsEntity todayLog = new CaloryLogsEntity();
        todayLog.setUser(testUser);
        todayLog.setDate(today);
        todayLog.setTotalCalory(BigDecimal.ZERO);
        todayLog.setProtein(BigDecimal.ZERO);
        todayLog.setFat(BigDecimal.ZERO);
        todayLog.setCarbohydrates(BigDecimal.ZERO);
        todayLog.setDrankWaterMl(BigDecimal.ZERO);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(caloryLogsRepository.findByUserAndDate(testUser, today))
                .thenReturn(Optional.of(todayLog));
        when(caloryLogsRepository.findAllByUser_IdAndDateBetweenOrderByDateAsc(
                eq(userId), any(LocalDate.class), any(LocalDate.class))).thenReturn(Collections.emptyList());
        when(caloryRepository.findByUser_IdAndDateTime(userId, today))
                .thenReturn(Collections.emptyList());

        // Act
        MainPageResponseNew response = caloryService.showMainPageInfoNew(userId);

        // Assert
        assertNotNull(response);
        // For GAIN goal: protein 25%, fat 25%, carbs 50%
        // BMR = 2000, so carbs norm = 2000 * 0.50 / 4 = 250
        assertEquals(0, BigDecimal.valueOf(250.00).compareTo(response.todayCarbs().todayCarbsNorm()));
    }

    @Test
    void testShowDatePageInfo_Success() {
        // Arrange
        Long userId = 1L;
        LocalDate date = LocalDate.of(2024, 1, 10);

        CaloryLogsEntity dateLog = new CaloryLogsEntity();
        dateLog.setUser(testUser);
        dateLog.setDate(date);
        dateLog.setTotalCalory(BigDecimal.valueOf(1200));
        dateLog.setProtein(BigDecimal.valueOf(80));
        dateLog.setFat(BigDecimal.valueOf(40));
        dateLog.setCarbohydrates(BigDecimal.valueOf(150));
        dateLog.setDrankWaterMl(BigDecimal.valueOf(1200));

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(caloryLogsRepository.findByUserAndDate(testUser, date))
                .thenReturn(Optional.of(dateLog));
        when(caloryLogsRepository.findAllByUser_IdAndDateBetweenOrderByDateAsc(
                eq(userId), any(LocalDate.class), any(LocalDate.class))).thenReturn(Collections.emptyList());
        when(caloryRepository.findByUser_IdAndDateTime(userId, date))
                .thenReturn(Collections.emptyList());

        // Act
        MainPageResponseNew response = caloryService.showDatePageInfo(date, userId);

        // Assert
        assertNotNull(response);
        assertNotNull(response.todayCalory());
    }

    @Test
    void testGetCalendar_Success() {
        // Arrange
        Long userId = 1L;
        int year = 2024;

        CaloryLogsEntity log1 = new CaloryLogsEntity();
        log1.setDate(LocalDate.of(2024, 1, 15));
        log1.setTotalCalory(BigDecimal.valueOf(1800));

        CaloryLogsEntity log2 = new CaloryLogsEntity();
        log2.setDate(LocalDate.of(2024, 6, 20));
        log2.setTotalCalory(BigDecimal.valueOf(2000));

        WeightLogsEntity weightLog = new WeightLogsEntity();
        weightLog.setDate(LocalDate.of(2024, 3, 1));
        weightLog.setBmr(BigDecimal.valueOf(2100));
        weightLog.setUser(testUser);

        when(caloryLogsRepository.findAllByUser_IdAndDateBetweenOrderByDateAsc(
                eq(userId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(log1, log2));
        when(weightLogsRepository.findAllByUser_IdAndDateBetweenOrderByDateAsc(
                eq(userId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(weightLog));

        // Act
        CalendarResponse response = caloryService.getCalendar(year, userId);

        // Assert
        assertNotNull(response);
        assertNotNull(response.calendar());
        assertEquals(366, response.calendar().size()); // 2024 is a leap year
        assertTrue(response.calendar().stream()
                .anyMatch(day -> day.date().equals(LocalDate.of(2024, 1, 15)) &&
                        day.caloryCons().equals(BigDecimal.valueOf(1800))));
    }

    @Test
    void testGetCalendar_EmptyData() {
        // Arrange
        Long userId = 1L;
        int year = 2024;

        when(caloryLogsRepository.findAllByUser_IdAndDateBetweenOrderByDateAsc(
                eq(userId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(weightLogsRepository.findAllByUser_IdAndDateBetweenOrderByDateAsc(
                eq(userId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        // Act
        CalendarResponse response = caloryService.getCalendar(year, userId);

        // Assert
        assertNotNull(response);
        assertNotNull(response.calendar());
        assertEquals(366, response.calendar().size()); // 2024 is a leap year
        assertTrue(response.calendar().stream()
                .allMatch(day -> day.caloryCons() == null && day.caloryNorm() == null));
    }

    @Test
    void testSaveMealToDb_EventProducerCalledWithCorrectTopics() {
        // Arrange
        Long userId = 1L;
        MealType mealType = MealType.BREAKFAST;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(caloryRepository.findByUser_IdAndDateTimeAndMealType(userId, testDate, mealType))
                .thenReturn(Optional.empty());
        when(caloryRepository.save(any(MealEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(caloryLogsRepository.findByUserAndDate(any(), any())).thenReturn(Optional.empty());
        when(caloryLogsRepository.save(any(CaloryLogsEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        caloryService.saveMealToDb(userId, testMealDto, testDate, mealType);

        // Assert
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        List<String> topics = topicCaptor.getAllValues();
        assertTrue(topics.contains("analise-meal-open-food_events-topic"));
        assertTrue(topics.contains("analise-meal-food-secret_events-topic"));
    }

    @Test
    void testShowMainPageInfo_WithNullValues() {
        // Arrange
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        testUser.setCurrentWeight(null);

        CaloryLogsEntity todayLog = new CaloryLogsEntity();
        todayLog.setUser(testUser);
        todayLog.setDate(today);
        todayLog.setTotalCalory(null);
        todayLog.setProtein(null);
        todayLog.setFat(null);
        todayLog.setCarbohydrates(null);
        todayLog.setDrankWaterMl(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(caloryLogsRepository.findByUserAndDate(testUser, today))
                .thenReturn(Optional.of(todayLog));
        when(caloryLogsRepository.findAllByUser_IdAndDateBetweenOrderByDateAsc(
                eq(userId), any(LocalDate.class), eq(today))).thenReturn(Collections.emptyList());
        when(userRepository.findBmrByUserId(userId)).thenReturn(null);
        when(userRepository.findWaterNeedsByUserId(userId)).thenReturn(null);

        // Act
        MainPageResponse response = caloryService.showMainPageInfo(userId);

        // Assert
        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.getCalorieGoal());
        assertEquals(BigDecimal.ZERO, response.getWaterGoal());
        assertEquals(BigDecimal.ZERO, response.getCalorieValue());
        assertEquals(BigDecimal.ZERO, response.getCurrentWeight());
    }
}

