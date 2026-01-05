export interface MainPageResponse {
  weekCalory?: WeekCalory;
  todayCalory: TodayCalory;
  todayWater: TodayWater;
  todayCarbs: TodayCarbs;
  todayProtein: TodayProtein;
  todayFat: TodayFat;
  mealPage: MealPage[];
  totalCalories?: number;
  totalProteins?: number;
  totalFats?: number;
  totalCarbohydrates?: number;
  calorieGoal?: number;
  waterConsumed?: number;
  waterGoal?: number;
  meals?: MealSummary[];
}

export interface WeekCalory {
  thisWeekCaloryCons: { [key: string]: number };
  thisWeekCaloryNorm: { [key: string]: number };
}

export interface TodayCalory {
  todayCaloryCons: number;
  todayCaloryNorm: number;
}

export interface TodayWater {
  todayWaterCons: number;
  todayWaterNeeds: number;
}

export interface TodayCarbs {
  todayCarbsCons: number;
  todayCarbsNorm: number;
}

export interface TodayProtein {
  todayProteinCons: number;
  todayProteinNorm: number;
}

export interface TodayFat {
  todayFatCons: number;
  todayFatNorm: number;
}

export interface MealPage {
  mealType: string;
  caloryCons: number;
}

export interface MealSummary {
  mealType: string;
  calories: number;
  foods: FoodItemSummary[];
}

export interface FoodItemSummary {
  name: string;
  quantity: number;
  unit: string;
  calories: number;
}

export interface CalendarDay {
  date: string;
  totalCalories: number;
  hasMeals: boolean;
}

export interface CalendarResponse {
  calendar: CalendarDayData[];
}

export interface CalendarDayData {
  date: string;
  caloryNorm: number;
  caloryCons: number;
}

export interface CalendarMonth {
  month: number;
  days: CalendarDay[];
}

export interface MealByDateResponse {
  mealType: string;
  date: string;
  mealDto: any;
}

export interface MealFoodItem {
  name: string;
  calories: number;
  proteins: number;
  fats: number;
  carbohydrates: number;
  quantity: number;
  unit: string;
}

