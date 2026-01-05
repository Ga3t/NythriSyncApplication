export interface ReportResponse {
  anlyses: DayAnalyse[];
}

export interface DayAnalyse {
  date: string;
  weight: number | null;
  sugarCons: number;
  sugarNorm: number;
  fiberCons: number;
  fiberNorm: number;
  kcalCons: number;
  kcalNorm: number;
  fatCons: number;
  fatNorm: number;
  proteinCons: number;
  proteinNorm: number;
  carbsCons: number;
  carbsNorm: number;
  waterCons: number;
  waterNorm: number;
}

export interface DailyReport {
  date: string;
  totalCalories: number;
  totalProteins: number;
  totalFats: number;
  totalCarbohydrates: number;
  meals: MealReport[];
}

export interface MealReport {
  mealType: string;
  calories: number;
  proteins: number;
  fats: number;
  carbohydrates: number;
}













