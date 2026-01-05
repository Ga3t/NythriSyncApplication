export enum MealType {
  BREAKFAST = 'BREAKFAST',
  LUNCH = 'LUNCH',
  DINNER = 'DINNER',
  SNACK = 'SNACK'
}

export interface MealDto {
  dishes: {
    dish: Dish[];
  };
}

export interface Dish {
  type: string;
  code: string;
  name: string;
  calories: number;
  fat: number;
  protein: number;
  carbohydrates: number;
  sugars?: number;
  fiber?: number;
  quantity?: number;
  unit?: string;
}

export interface FoodItem {
  id?: string;
  name: string;
  calories: number;
  proteins: number;
  fats: number;
  carbohydrates: number;
  sugar?: number;
  fiber?: number;
  cholesterol?: number;
  barcode?: string;
}

export interface MealEntry {
  id: string;
  mealType: MealType;
  date: string;
  foods: MealDto[];
  totalCalories: number;
  totalProteins: number;
  totalFats: number;
  totalCarbohydrates: number;
}

