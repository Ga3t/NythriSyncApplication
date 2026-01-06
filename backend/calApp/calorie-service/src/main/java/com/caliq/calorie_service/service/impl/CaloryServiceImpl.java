package com.caliq.calorie_service.service.impl;
import com.caliq.calorie_service.exeptions.UserNotFoundException;
import com.caliq.calorie_service.models.dto.CalendarResponse;
import com.caliq.calorie_service.models.dto.MainPageResponseNew;
import com.caliq.calorie_service.models.entity.CaloryLogsEntity;
import com.caliq.calorie_service.models.entity.WeightLogsEntity;
import com.caliq.calorie_service.models.response.MainPageResponse;
import com.caliq.calorie_service.models.response.MealByDateResponse;
import com.caliq.calorie_service.models.entity.MealEntity;
import com.caliq.calorie_service.models.entity.UserModel;
import com.caliq.calorie_service.models.types.GoalType;
import com.caliq.calorie_service.models.types.MealType;
import com.caliq.calorie_service.repository.CaloryLogsRepository;
import com.caliq.calorie_service.repository.CaloryRepository;
import com.caliq.calorie_service.repository.UserRepository;
import com.caliq.calorie_service.repository.WeightLogsRepository;
import com.caliq.calorie_service.service.CaloryService;
import com.caliq.core.dto.MealDto;
import com.caliq.core.message.MealMessage;
import com.caliq.core.message.WaterMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
@Service
@Primary
public class CaloryServiceImpl implements CaloryService {
    private CaloryRepository caloryRepository;
    private UserRepository userRepository;
    private CaloryLogsRepository caloryLogsRepository;
    private WeightLogsRepository weightLogsRepository;
    @Autowired
    private ObjectMapper objectMapper;
    public CaloryServiceImpl(CaloryRepository caloryRepository, UserRepository userRepository,  CaloryLogsRepository caloryLogsRepository,
                             WeightLogsRepository weightLogsRepository) {
        this.caloryRepository = caloryRepository;
        this.userRepository = userRepository;
        this.caloryLogsRepository = caloryLogsRepository;
        this.weightLogsRepository = weightLogsRepository;
    }
    @Override
    public String saveMealToDb(Long userId, MealDto mealDto, LocalDate time, MealType mealType) {
        UserModel userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Optional<MealEntity> existingMealOpt =
                caloryRepository.findByUser_IdAndDateTimeAndMealType(userId, time, mealType);
        MealEntity mealEntity;
        boolean isUpdate = existingMealOpt.isPresent();
        MealDto oldMealDto = null;
        if (isUpdate) {
            mealEntity = existingMealOpt.get();
            JsonNode existingJson = mealEntity.getMeal();
            if (existingJson != null && !existingJson.isNull() && existingJson.isObject()) {
                try {
                    oldMealDto = objectMapper.treeToValue(existingJson, MealDto.class);
                } catch (JsonProcessingException e) {
                }
            }
            JsonNode newJson = objectMapper.valueToTree(mealDto);
            if (existingJson != null && existingJson.isObject()) {
                ((ObjectNode) existingJson).setAll((ObjectNode) newJson);
                mealEntity.setMeal(existingJson);
            } else {
                mealEntity.setMeal(newJson);
            }
        } else {
            mealEntity = new MealEntity();
            mealEntity.setUser(userEntity);
            mealEntity.setMealType(mealType);
            mealEntity.setDateTime(time);
            mealEntity.setMeal(objectMapper.valueToTree(mealDto));
        }
        caloryRepository.save(mealEntity);
        if (isUpdate && oldMealDto != null) {
            removeFromCaloryLogs(oldMealDto, time, userEntity);
        }
        saveToCaloryLogs(mealDto, time, userEntity);
        Map<String, List<MealDto.Dish>> groupedDishes = mealDto.dishes().dish().stream()
                .collect(Collectors.groupingBy(MealDto.Dish::type));
        groupedDishes.forEach((type, dishesByType) -> {
            MealDto groupedMealDto = new MealDto(new MealDto.Dishes(dishesByType));
            MealMessage message = new MealMessage();
            message.setUserId(userId);
            message.setMeal(groupedMealDto);
            message.setSentAt(time.atStartOfDay());
            String topic;
            switch (type) {
                case "OPENFOODFACT" -> topic = "analise-meal-open-food_events-topic";
                case "FOODSECRET" -> topic = "analise-meal-food-secret_events-topic";
                default -> topic = "analise-meal_events-topic";
            }
        });
        return "Meal saved successfully";
    }
    @Override
    public MealByDateResponse getMealByDate(Long userId, LocalDate time, MealType mealType) {
        MealEntity mealsFromDb = caloryRepository.findByUser_IdAndDateTimeAndMealType(
                userId, time, mealType).orElseGet(()-> new MealEntity());
        JsonNode mealNode = mealsFromDb.getMeal();
        if (mealNode == null) {
            mealNode = objectMapper.createObjectNode();
        }
        MealByDateResponse response = new MealByDateResponse(time.toString(),mealType,mealNode);
        return response;
    }
    @Override
    @Transactional
    public MainPageResponse showMainPageInfo(Long userId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        LocalDate today = LocalDate.now();
        CaloryLogsEntity caloryLogsEntity = caloryLogsRepository.findByUserAndDate(user, today)
                .orElseGet(() -> {
                    CaloryLogsEntity e = new CaloryLogsEntity();
                    e.setUser(user);
                    e.setDate(today);
                    e.setCarbohydrates(BigDecimal.ZERO);
                    e.setProtein(BigDecimal.ZERO);
                    e.setFat(BigDecimal.ZERO);
                    e.setDrankWaterMl(BigDecimal.ZERO);
                    e.setTotalCalory(BigDecimal.ZERO);
                    return caloryLogsRepository.save(e);
                });
        LocalDate start = today.minusDays(6);
        List<CaloryLogsEntity> logs = caloryLogsRepository
                .findAllByUser_IdAndDateBetweenOrderByDateAsc(user.getId(), start, today);
        Map<LocalDate, BigDecimal> totalByDate = logs.stream()
                .collect(Collectors.toMap(
                        CaloryLogsEntity::getDate,
                        l -> nz(l.getTotalCalory()),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        MainPageResponse response = new MainPageResponse();
        response.setCalorieGoal(nz(userRepository.findBmrByUserId(userId)));
        response.setWaterGoal(nz(userRepository.findWaterNeedsByUserId(userId)));
        response.setCalorieValue(nz(caloryLogsEntity.getTotalCalory()));
        response.setCurrentWeight(nz(user.getCurrentWeight()));
        response.setProteinValue(nz(caloryLogsEntity.getProtein()));
        response.setWaterValue(nz(caloryLogsEntity.getDrankWaterMl()));
        response.setFatValue(nz(caloryLogsEntity.getFat()));
        response.setCarbohydrateValue(nz(caloryLogsEntity.getCarbohydrates()));
        return response;
    }
    @Override
    public void saveToCaloryLogs(MealDto mealDto, LocalDate time, UserModel user) {
        CaloryLogsEntity caloryLogsEntity = caloryLogsRepository.findByUserAndDate(user, time)
                .orElseGet(() -> {
                    CaloryLogsEntity e = new CaloryLogsEntity();
                    e.setUser(user);
                    e.setDate(time);
                    e.setCarbohydrates(BigDecimal.ZERO);
                    e.setProtein(BigDecimal.ZERO);
                    e.setFat(BigDecimal.ZERO);
                    e.setDrankWaterMl(BigDecimal.ZERO);
                    e.setTotalCalory(BigDecimal.ZERO);
                    e.setSugar(BigDecimal.ZERO);
                    e.setFiber(BigDecimal.ZERO);
                    return e;
                });
        BigDecimal totalCalory = BigDecimal.ZERO;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalCarbohydrate = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        BigDecimal totalSugar = BigDecimal.ZERO;
        BigDecimal totalFiber = BigDecimal.ZERO;
        boolean hasCalories = false;
        boolean hasFat = false;
        boolean hasProtein = false;
        boolean hasCarbohydrate = false;
        boolean hasSugar = false;
        boolean hasFiber = false;
        if (mealDto != null && mealDto.dishes() != null && mealDto.dishes().dish() != null) {
            for (MealDto.Dish d : mealDto.dishes().dish()) {
                if (d.calories() != null) {
                    totalCalory = totalCalory.add(d.calories());
                    hasCalories = true;
                }
                if (d.protein() != null) {
                    totalProtein = totalProtein.add(d.protein());
                    hasProtein = true;
                }
                if (d.carbohydrates() != null) {
                    totalCarbohydrate = totalCarbohydrate.add(d.carbohydrates());
                    hasCarbohydrate = true;
                }
                if (d.fat() != null) {
                    totalFat = totalFat.add(d.fat());
                    hasFat = true;
                }
                if (d.sugars() != null) {
                    totalSugar = totalSugar.add(d.sugars());
                    hasSugar = true;
                }
                if (d.fiber() != null) {
                    totalFiber = totalFiber.add(d.fiber());
                    hasFiber = true;
                }
            }
        }
        if (hasCalories)
            caloryLogsEntity.setTotalCalory(nz(caloryLogsEntity.getTotalCalory()).add(totalCalory));
        if (hasProtein)
            caloryLogsEntity.setProtein(nz(caloryLogsEntity.getProtein()).add(totalProtein));
        if (hasCarbohydrate)
            caloryLogsEntity.setCarbohydrates(nz(caloryLogsEntity.getCarbohydrates()).add(totalCarbohydrate));
        if (hasFat)
            caloryLogsEntity.setFat(nz(caloryLogsEntity.getFat()).add(totalFat));
        if (hasSugar)
            caloryLogsEntity.setSugar(nz(caloryLogsEntity.getSugar()).add(totalSugar));
        if (hasFiber)
            caloryLogsEntity.setFiber(nz(caloryLogsEntity.getFiber()).add(totalFiber));
        caloryLogsRepository.save(caloryLogsEntity);
    }
    private void removeFromCaloryLogs(MealDto mealDto, LocalDate time, UserModel user) {
        CaloryLogsEntity caloryLogsEntity = caloryLogsRepository.findByUserAndDate(user, time)
                .orElse(null);
        if (caloryLogsEntity == null) {
            return;
        }
        BigDecimal totalCalory = BigDecimal.ZERO;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalCarbohydrate = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        BigDecimal totalSugar = BigDecimal.ZERO;
        BigDecimal totalFiber = BigDecimal.ZERO;
        boolean hasCalories = false;
        boolean hasFat = false;
        boolean hasProtein = false;
        boolean hasCarbohydrate = false;
        boolean hasSugar = false;
        boolean hasFiber = false;
        if (mealDto != null && mealDto.dishes() != null && mealDto.dishes().dish() != null) {
            for (MealDto.Dish d : mealDto.dishes().dish()) {
                if (d.calories() != null) {
                    totalCalory = totalCalory.add(d.calories());
                    hasCalories = true;
                }
                if (d.protein() != null) {
                    totalProtein = totalProtein.add(d.protein());
                    hasProtein = true;
                }
                if (d.carbohydrates() != null) {
                    totalCarbohydrate = totalCarbohydrate.add(d.carbohydrates());
                    hasCarbohydrate = true;
                }
                if (d.fat() != null) {
                    totalFat = totalFat.add(d.fat());
                    hasFat = true;
                }
                if (d.sugars() != null) {
                    totalSugar = totalSugar.add(d.sugars());
                    hasSugar = true;
                }
                if (d.fiber() != null) {
                    totalFiber = totalFiber.add(d.fiber());
                    hasFiber = true;
                }
            }
        }
        if (hasCalories) {
            BigDecimal currentValue = nz(caloryLogsEntity.getTotalCalory());
            caloryLogsEntity.setTotalCalory(currentValue.subtract(totalCalory).max(BigDecimal.ZERO));
        }
        if (hasProtein) {
            BigDecimal currentValue = nz(caloryLogsEntity.getProtein());
            caloryLogsEntity.setProtein(currentValue.subtract(totalProtein).max(BigDecimal.ZERO));
        }
        if (hasCarbohydrate) {
            BigDecimal currentValue = nz(caloryLogsEntity.getCarbohydrates());
            caloryLogsEntity.setCarbohydrates(currentValue.subtract(totalCarbohydrate).max(BigDecimal.ZERO));
        }
        if (hasFat) {
            BigDecimal currentValue = nz(caloryLogsEntity.getFat());
            caloryLogsEntity.setFat(currentValue.subtract(totalFat).max(BigDecimal.ZERO));
        }
        if (hasSugar) {
            BigDecimal currentValue = nz(caloryLogsEntity.getSugar());
            caloryLogsEntity.setSugar(currentValue.subtract(totalSugar).max(BigDecimal.ZERO));
        }
        if (hasFiber) {
            BigDecimal currentValue = nz(caloryLogsEntity.getFiber());
            caloryLogsEntity.setFiber(currentValue.subtract(totalFiber).max(BigDecimal.ZERO));
        }
        caloryLogsRepository.save(caloryLogsEntity);
    }
    @Override
    public BigDecimal addWaterToLogs(Long userId, BigDecimal water, LocalDate date) {
        UserModel user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found"));
        CaloryLogsEntity caloryLogs = caloryLogsRepository.findByUserAndDate(user,date).orElseGet(()->{
            CaloryLogsEntity e = new CaloryLogsEntity();
            e.setUser(user);
            e.setDate(date);
            e.setCarbohydrates(BigDecimal.valueOf(0));
            e.setProtein(BigDecimal.valueOf(0));
            e.setFat(BigDecimal.valueOf(0));
            e.setDrankWaterMl(BigDecimal.valueOf(0));
            e.setTotalCalory(BigDecimal.valueOf(0));
            return e;
        });
        caloryLogs.setDrankWaterMl(caloryLogs.getDrankWaterMl().add(water));
        caloryLogsRepository.save(caloryLogs);
        WaterMessage waterMessage = new WaterMessage();
        waterMessage.setWaterMl(water);
        waterMessage.setUserId(userId);
        waterMessage.setSendAt(LocalDateTime.now());
        return  caloryLogs.getDrankWaterMl();
    }
    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
    @Override
    @Transactional
    public MainPageResponseNew showMainPageInfoNew(Long userId){
        MainPageResponseNew mainPageResponseNew;
        UserModel user = userRepository.findById(userId).orElseThrow(()->
                new UserNotFoundException("User details not found"));
        GoalType goalType = user.getGoalType();
        LocalDate today = LocalDate.now();
        CaloryLogsEntity todayLog = caloryLogsRepository.findByUserAndDate(user, today)
                .orElseGet(() -> {
                    CaloryLogsEntity e = new CaloryLogsEntity();
                    e.setUser(user);
                    e.setDate(today);
                    e.setCarbohydrates(BigDecimal.ZERO);
                    e.setProtein(BigDecimal.ZERO);
                    e.setFat(BigDecimal.ZERO);
                    e.setDrankWaterMl(BigDecimal.ZERO);
                    e.setTotalCalory(BigDecimal.ZERO);
                    return caloryLogsRepository.save(e);
                });
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);
        List<CaloryLogsEntity> weekLogs = caloryLogsRepository
                .findAllByUser_IdAndDateBetweenOrderByDateAsc(user.getId(), monday, sunday);
        List<MealEntity> todayMeals = caloryRepository.findByUser_IdAndDateTime(userId,today);
        mainPageResponseNew = formNewMainPageResponse(weekLogs, todayLog,todayMeals, user.getBmr(), user.getWaterNeeds(), goalType);
        return mainPageResponseNew;
    }
    private MainPageResponseNew formNewMainPageResponse(
            List<CaloryLogsEntity> weekLogs,
            CaloryLogsEntity todayLog,
            List<MealEntity> todayMeals,
            BigDecimal calorieNorm,
            BigDecimal waterNorm,
            GoalType goalType
    ) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        Map<String, BigDecimal> caloryWeekCons = new LinkedHashMap<>();
        Map<String, BigDecimal> caloryWeekNorm = new LinkedHashMap<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);
        for (int i = 0; i < 7; i++) {
            LocalDate day = monday.plusDays(i);
            String key = day.format(dayFormatter);
            if (!day.isAfter(today)) {
                CaloryLogsEntity log = weekLogs.stream()
                        .filter(l -> l.getDate().equals(day))
                        .findFirst()
                        .orElse(null);
                caloryWeekCons.put(key, log != null ? nz(log.getTotalCalory()) : BigDecimal.ZERO);
                caloryWeekNorm.put(key, calorieNorm);
            } else {
                caloryWeekCons.put(key, null);
                caloryWeekNorm.put(key, null);
            }
        }
        MainPageResponseNew.WeekCalory weekCalory =
                new MainPageResponseNew.WeekCalory(caloryWeekCons, caloryWeekNorm);
        MainPageResponseNew.TodayCalory todayCalory =
                new MainPageResponseNew.TodayCalory(nz(todayLog.getTotalCalory()), calorieNorm);
        MainPageResponseNew.TodayWater todayWater =
                new MainPageResponseNew.TodayWater(nz(todayLog.getDrankWaterMl()), waterNorm);
        BigDecimal proteinPercent;
        BigDecimal fatPercent;
        BigDecimal carbsPercent;
        switch (goalType) {
            case LOSS -> {
                proteinPercent = new BigDecimal("0.35");
                fatPercent = new BigDecimal("0.25");
                carbsPercent = new BigDecimal("0.40");
            }
            case GAIN -> {
                proteinPercent = new BigDecimal("0.30");
                fatPercent = new BigDecimal("0.20");
                carbsPercent = new BigDecimal("0.50");
            }
            default -> {
                proteinPercent = new BigDecimal("0.30");
                fatPercent = new BigDecimal("0.30");
                carbsPercent = new BigDecimal("0.40");
            }
        }
        BigDecimal proteinNorm = calorieNorm.multiply(proteinPercent).divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);
        BigDecimal fatNorm = calorieNorm.multiply(fatPercent).divide(new BigDecimal("9"), 2, RoundingMode.HALF_UP);
        BigDecimal carbsNorm = calorieNorm.multiply(carbsPercent).divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);
        MainPageResponseNew.TodayCarbs todayCarbs =
                new MainPageResponseNew.TodayCarbs(nz(todayLog.getCarbohydrates()), carbsNorm);
        MainPageResponseNew.TodayProtein todayProtein =
                new MainPageResponseNew.TodayProtein(nz(todayLog.getProtein()), proteinNorm);
        MainPageResponseNew.TodayFat todayFat =
                new MainPageResponseNew.TodayFat(nz(todayLog.getFat()), fatNorm);
        List<MainPageResponseNew.MealPage> mealPages = new ArrayList<>();
        for (MealEntity mealEntity : todayMeals) {
            try {
                MealDto mealDto = new ObjectMapper().treeToValue(mealEntity.getMeal(), MealDto.class);
                BigDecimal totalCalories = mealDto.dishes().dish().stream()
                        .map(MealDto.Dish::calories)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                mealPages.add(new MainPageResponseNew.MealPage(
                        mealEntity.getMealType().name(),
                        totalCalories
                ));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return new MainPageResponseNew(
                weekCalory,
                todayCalory,
                todayWater,
                todayCarbs,
                todayProtein,
                todayFat,
                mealPages
        );
    }
    @Transactional
    @Override
    public MainPageResponseNew showDatePageInfo(LocalDate date, Long userId) {
        MainPageResponseNew datePageResponse;
        UserModel user = userRepository.findById(userId).orElseThrow(()->
                new UserNotFoundException("User details not found"));
        GoalType goalType = user.getGoalType();
        CaloryLogsEntity todayLog = caloryLogsRepository.findByUserAndDate(user, date)
                .orElseGet(() -> {
                    CaloryLogsEntity e = new CaloryLogsEntity();
                    e.setUser(user);
                    e.setDate(date);
                    e.setCarbohydrates(BigDecimal.ZERO);
                    e.setProtein(BigDecimal.ZERO);
                    e.setFat(BigDecimal.ZERO);
                    e.setDrankWaterMl(BigDecimal.ZERO);
                    e.setTotalCalory(BigDecimal.ZERO);
                    return caloryLogsRepository.save(e);
                });
        LocalDate monday = date.with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);
        List<CaloryLogsEntity> weekLogs = caloryLogsRepository
                .findAllByUser_IdAndDateBetweenOrderByDateAsc(user.getId(), monday, sunday);
        List<MealEntity> todayMeals = caloryRepository.findByUser_IdAndDateTime(userId, date);
        datePageResponse = formNewMainPageResponse(weekLogs, todayLog,todayMeals, user.getBmr(), user.getWaterNeeds(), goalType);
        return datePageResponse;
    }
    @Transactional
    @Override
    public CalendarResponse getCalendar(int year, Long userId) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        List<CaloryLogsEntity> caloryLogs = caloryLogsRepository
                .findAllByUser_IdAndDateBetweenOrderByDateAsc(userId, start, end);
        List<WeightLogsEntity> weightLogs = weightLogsRepository
                .findAllByUser_IdAndDateBetweenOrderByDateAsc(userId, start, end);
        Map<LocalDate, BigDecimal> caloryByDate = caloryLogs.stream()
                .collect(Collectors.toMap(
                        CaloryLogsEntity::getDate,
                        CaloryLogsEntity::getTotalCalory
                ));
        List<CalendarResponse.CaloryDays> days = new ArrayList<>();
        BigDecimal currentCaloryNorm = null;
        int weightIndex = 0;
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            while (weightIndex < weightLogs.size()
                    && !weightLogs.get(weightIndex).getDate().isAfter(d)) {
                WeightLogsEntity w = weightLogs.get(weightIndex);
                currentCaloryNorm = w.getBmr();
                weightIndex++;
            }
            BigDecimal caloryCons = caloryByDate.get(d);
            days.add(new CalendarResponse.CaloryDays(
                    d,
                    currentCaloryNorm,
                    caloryCons
            ));
        }
        return new CalendarResponse(days);
    }
}