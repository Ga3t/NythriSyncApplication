import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ApiService } from '../../../../core/services/api.service';
import { MealDto, MealType, Dish } from '../../../../core/models/food.models';
import { MatSnackBar } from '@angular/material/snack-bar';
export interface AddMealDialogData {
  mealType: string;
  date: Date;
}
@Component({
  selector: 'app-add-meal',
  templateUrl: './add-meal.component.html',
  styleUrls: ['./add-meal.component.scss']
})
export class AddMealComponent implements OnInit {
  currentStep: 'search' | 'results' | 'details' = 'search';
  searchMethodIndex = 0;
  searchQuery = '';
  searchingFood = false;
  searchResults: any[] = [];
  currentPage = 0;
  totalResults = 0;
  maxResults = 20;
  totalPages = 0;
  selectedFood: any = null;
  foodDetails: any = null;
  quantityForm!: FormGroup;
  quantityInGrams = 100;
  dishes: Dish[] = [];
  existingDishes: Dish[] = [];
  newDishes: Dish[] = [];
  loading = false;
  loadingExistingMeal = false;
  customFoodName = '';
  customFoodQuantity = 100;
  customFoodCalories = 0;
  customFoodProtein = 0;
  customFoodFat = 0;
  customFoodCarbohydrates = 0;
  customFoodFiber = 0;
  customFoodSugar = 0;
  customFoodCholesterol = 0;
  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddMealComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AddMealDialogData,
    private apiService: ApiService,
    private snackBar: MatSnackBar
  ) {
    this.quantityForm = this.fb.group({
      quantity: [100, [Validators.required, Validators.min(0.01)]]
    });
  }
  ngOnInit(): void {
    this.quantityForm.get('quantity')?.valueChanges.subscribe(quantity => {
      const qty = parseFloat(quantity) || 100;
      this.quantityInGrams = qty;
    });
    this.loadExistingMeal();
  }
  private formatDateLocal(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
  loadExistingMeal(): void {
    this.loadingExistingMeal = true;
    const dateStr = this.formatDateLocal(this.data.date);
    const mealType = this.data.mealType as MealType;
    this.apiService.getMealByDate(dateStr, mealType).subscribe({
      next: (response) => {
        this.loadingExistingMeal = false;
        if (response && response.mealDto) {
          this.parseExistingDishes(response.mealDto);
        }
      },
      error: () => {
        this.loadingExistingMeal = false;
      }
    });
  }
  parseExistingDishes(mealDto: any): void {
    try {
      if (mealDto.dishes && mealDto.dishes.dish) {
        const existingDishesArray = Array.isArray(mealDto.dishes.dish)
          ? mealDto.dishes.dish
          : [mealDto.dishes.dish];
        this.existingDishes = existingDishesArray.map((d: any) => ({
          type: d.type || 'FOOD',
          code: d.code || '',
          name: d.name || '',
          calories: d.calories || 0,
          fat: d.fat || 0,
          protein: d.protein || 0,
          carbohydrates: d.carbohydrates || 0,
          sugars: d.sugars || 0,
          fiber: d.fiber || 0,
          cholesterol: d.cholesterol || 0,
          quantity: d.quantity || 0,
          unit: d.unit || 'g'
        }));
        this.dishes = [...this.existingDishes, ...this.newDishes];
      }
    } catch (error) {
      console.error('Error parsing existing dishes:', error);
      this.existingDishes = [];
      this.dishes = [...this.existingDishes, ...this.newDishes];
    }
  }
  onSearchMethodChange(index: number): void {
    this.searchMethodIndex = index;
    this.searchQuery = '';
    this.searchResults = [];
    if (index === 2) {
      this.resetCustomFoodForm();
    }
  }
  resetCustomFoodForm(): void {
    this.customFoodName = '';
    this.customFoodQuantity = 100;
    this.customFoodCalories = 0;
    this.customFoodProtein = 0;
    this.customFoodFat = 0;
    this.customFoodCarbohydrates = 0;
    this.customFoodFiber = 0;
    this.customFoodSugar = 0;
    this.customFoodCholesterol = 0;
  }
  isCustomFoodValid(): boolean {
    return !!(
      this.customFoodName &&
      this.customFoodName.trim().length > 0 &&
      this.customFoodQuantity > 0 &&
      (this.customFoodCalories > 0 ||
       this.customFoodProtein > 0 ||
       this.customFoodFat > 0 ||
       this.customFoodCarbohydrates > 0)
    );
  }
  saveCustomFood(): void {
    if (!this.isCustomFoodValid()) {
      this.snackBar.open('Please fill in at least food name, quantity, and one nutritional value', 'Close', { duration: 3000 });
      return;
    }
    const dishName = this.customFoodName.trim();
    const dish: Dish = {
      type: 'FOOD',
      code: '',
      name: dishName,
      calories: this.customFoodCalories || 0,
      fat: this.customFoodFat || 0,
      protein: this.customFoodProtein || 0,
      carbohydrates: this.customFoodCarbohydrates || 0,
      sugars: this.customFoodSugar || 0,
      fiber: this.customFoodFiber || 0,
      cholesterol: this.customFoodCholesterol || 0,
      quantity: this.customFoodQuantity,
      unit: 'g'
    };
    const existingIndex = this.existingDishes.findIndex(d =>
      d.code === '' && d.name === dishName && d.quantity === this.customFoodQuantity
    );
    if (existingIndex !== -1) {
      this.existingDishes[existingIndex] = dish;
      this.dishes = [...this.existingDishes, ...this.newDishes];
      this.snackBar.open('Custom food updated!', 'Close', { duration: 2000 });
      this.resetCustomFoodForm();
      return;
    }
    const newIndex = this.newDishes.findIndex(d =>
      d.code === '' && d.name === dishName && d.quantity === this.customFoodQuantity
    );
    if (newIndex !== -1) {
      this.newDishes[newIndex] = dish;
      this.dishes = [...this.existingDishes, ...this.newDishes];
      this.snackBar.open('Custom food updated!', 'Close', { duration: 2000 });
      this.resetCustomFoodForm();
      return;
    }
    this.newDishes.push(dish);
    this.dishes = [...this.existingDishes, ...this.newDishes];
    this.snackBar.open('Custom food added!', 'Close', { duration: 2000 });
    this.resetCustomFoodForm();
  }
  searchFood(): void {
    if (this.searchMethodIndex === 0) {
      this.searchFoodByName();
    } else {
      this.searchFoodByBarcode();
    }
  }
  searchFoodByName(): void {
    if (!this.searchQuery || this.searchQuery.length < 2) {
      this.snackBar.open('Please enter at least 2 characters to search', 'Close', { duration: 3000 });
      return;
    }
    this.currentPage = 0;
    this.performSearch(this.currentPage);
  }
  performSearch(page: number): void {
    this.searchingFood = true;
    this.apiService.searchFood(this.searchQuery, page).subscribe({
      next: (response: any) => {
        this.searchingFood = false;
        if (response && response.items && response.items.length > 0) {
          this.searchResults = response.items;
          this.currentPage = parseInt(response.pageNumber || '0', 10);
          this.totalResults = parseInt(response.totalResults || '0', 10);
          this.maxResults = parseInt(response.maxResults || '20', 10);
          this.totalPages = Math.ceil(this.totalResults / this.maxResults);
          this.currentStep = 'results';
        } else {
          this.searchResults = [];
          this.snackBar.open('No food found. Please try a different search term.', 'Close', { duration: 3000 });
        }
      },
      error: () => {
        this.searchingFood = false;
        this.searchResults = [];
        this.snackBar.open('Food search failed. Please try again.', 'Close', { duration: 3000 });
      }
    });
  }
  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.performSearch(page);
    }
  }
  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.goToPage(this.currentPage + 1);
    }
  }
  previousPage(): void {
    if (this.currentPage > 0) {
      this.goToPage(this.currentPage - 1);
    }
  }
  getResultsRangeStart(): number {
    return this.currentPage * this.maxResults + 1;
  }
  getResultsRangeEnd(): number {
    return Math.min((this.currentPage + 1) * this.maxResults, this.totalResults);
  }
  searchFoodByBarcode(): void {
    if (!this.searchQuery || this.searchQuery.length < 8) {
      this.snackBar.open('Please enter a valid barcode (at least 8 digits)', 'Close', { duration: 3000 });
      return;
    }
    this.searchingFood = true;
    this.apiService.searchProductByBarcode(this.searchQuery).subscribe({
      next: (response: any) => {
        this.searchingFood = false;
        if (response && response.foodData) {
          const foodData = response.foodData;
          this.selectedFood = {
            id: foodData.id || '',
            name: foodData.name || '',
            description: foodData.description || ''
          };
          this.loadFoodDetails();
        } else {
          this.snackBar.open('No food found for this barcode.', 'Close', { duration: 3000 });
        }
      },
      error: () => {
        this.searchingFood = false;
        this.snackBar.open('Barcode search failed. Please try again.', 'Close', { duration: 3000 });
      }
    });
  }
  selectFoodFromResults(food: any): void {
    this.selectedFood = food;
    this.loadFoodDetails();
  }
  loadFoodDetails(): void {
    if (!this.selectedFood?.id) {
      this.snackBar.open('Food ID not available', 'Close', { duration: 3000 });
      return;
    }
    this.searchingFood = true;
    this.apiService.getFoodDetails(this.selectedFood.id).subscribe({
      next: (details: any) => {
        this.searchingFood = false;
        this.foodDetails = details;
        this.currentStep = 'details';
        this.quantityForm.patchValue({ quantity: 100 });
        this.quantityInGrams = 100;
      },
      error: () => {
        this.searchingFood = false;
        this.snackBar.open('Failed to get food details.', 'Close', { duration: 3000 });
      }
    });
  }
  getCalculatedMacros(): { calories: number; protein: number; fat: number; carbs: number; sugars: number; fiber: number } {
    if (!this.foodDetails) {
      return { calories: 0, protein: 0, fat: 0, carbs: 0, sugars: 0, fiber: 0 };
    }
    const baseQuantity = 100;
    const scaleFactor = this.quantityInGrams / baseQuantity;
    return {
      calories: Math.round((this.foodDetails.calories || 0) * scaleFactor * 100) / 100,
      protein: Math.round((this.foodDetails.protein || 0) * scaleFactor * 100) / 100,
      fat: Math.round((this.foodDetails.fat || 0) * scaleFactor * 100) / 100,
      carbs: Math.round((this.foodDetails.carbohydrate || 0) * scaleFactor * 100) / 100,
      sugars: Math.round((this.foodDetails.sugar || 0) * scaleFactor * 100) / 100,
      fiber: Math.round((this.foodDetails.fiber || 0) * scaleFactor * 100) / 100
    };
  }
  getMacroProgress(macroType: 'protein' | 'fat' | 'carbs'): number {
    if (!this.foodDetails || this.quantityInGrams <= 0) return 0;
    const macros = this.getCalculatedMacros();
    const macroValue = macroType === 'protein' ? macros.protein :
                       macroType === 'fat' ? macros.fat :
                       macros.carbs;
    const maxScale = 100;
    const progress = Math.min((macroValue / maxScale) * 100, 100);
    return progress;
  }
  saveDish(): void {
    if (this.quantityForm.invalid) {
      this.snackBar.open('Please enter a valid quantity', 'Close', { duration: 3000 });
      return;
    }
    const macros = this.getCalculatedMacros();
    const dishCode = this.foodDetails?.code || '';
    const dish: Dish = {
      type: 'FOOD',
      code: dishCode,
      name: this.selectedFood?.name || this.foodDetails?.name || '',
      calories: macros.calories,
      fat: macros.fat,
      protein: macros.protein,
      carbohydrates: macros.carbs,
      sugars: macros.sugars,
      fiber: macros.fiber,
      cholesterol: this.foodDetails?.cholesterol ? (this.foodDetails.cholesterol * (this.quantityInGrams / 100)) : 0,
      quantity: this.quantityInGrams,
      unit: 'g'
    };
    if (dishCode) {
      const existingIndex = this.existingDishes.findIndex(d => d.code === dishCode);
      if (existingIndex !== -1) {
        this.existingDishes[existingIndex] = dish;
        this.dishes = [...this.existingDishes, ...this.newDishes];
        this.snackBar.open('Dish updated!', 'Close', { duration: 2000 });
        this.resetToSearch();
        return;
      }
      const newIndex = this.newDishes.findIndex(d => d.code === dishCode);
      if (newIndex !== -1) {
        this.newDishes[newIndex] = dish;
        this.dishes = [...this.existingDishes, ...this.newDishes];
        this.snackBar.open('Dish updated!', 'Close', { duration: 2000 });
        this.resetToSearch();
        return;
      }
    }
    this.newDishes.push(dish);
    this.dishes = [...this.existingDishes, ...this.newDishes];
    this.snackBar.open('Dish added!', 'Close', { duration: 2000 });
    this.resetToSearch();
  }
  resetToSearch(): void {
    this.currentStep = 'search';
    this.searchQuery = '';
    this.searchResults = [];
    this.selectedFood = null;
    this.foodDetails = null;
    this.quantityForm.patchValue({ quantity: 100 });
    this.quantityInGrams = 100;
    this.currentPage = 0;
    this.totalResults = 0;
    this.totalPages = 0;
  }
  isExistingDish(index: number): boolean {
    return index < this.existingDishes.length;
  }
  removeDish(index: number): void {
    if (index < this.existingDishes.length) {
      this.existingDishes.splice(index, 1);
    } else {
      const newIndex = index - this.existingDishes.length;
      this.newDishes.splice(newIndex, 1);
    }
    this.dishes = [...this.existingDishes, ...this.newDishes];
  }
  getTotalCalories(): number {
    return this.dishes.reduce((sum, dish) => sum + (dish.calories || 0), 0);
  }
  getTotalProtein(): number {
    return this.dishes.reduce((sum, dish) => sum + (dish.protein || 0), 0);
  }
  getTotalFat(): number {
    return this.dishes.reduce((sum, dish) => sum + (dish.fat || 0), 0);
  }
  getTotalCarbohydrates(): number {
    return this.dishes.reduce((sum, dish) => sum + (dish.carbohydrates || 0), 0);
  }
  getTotalSugars(): number {
    return this.dishes.reduce((sum, dish) => sum + (dish.sugars || 0), 0);
  }
  getTotalFiber(): number {
    return this.dishes.reduce((sum, dish) => sum + (dish.fiber || 0), 0);
  }
  goBack(): void {
    if (this.currentStep === 'details') {
      this.currentStep = 'results';
    } else if (this.currentStep === 'results') {
      this.currentStep = 'search';
      this.searchResults = [];
    }
  }
  onSubmit(): void {
    if (this.dishes.length === 0) {
      this.snackBar.open('Please add at least one dish', 'Close', { duration: 3000 });
      return;
    }
    this.loading = true;
    const mealData: MealDto = {
      dishes: {
        dish: this.dishes
      }
    };
    const dateStr = this.formatDateLocal(this.data.date);
    const mealType = this.data.mealType as MealType;
    this.apiService.saveMeal(mealType, dateStr, mealData).subscribe({
      next: () => {
        this.snackBar.open('Meal saved successfully', 'Close', { duration: 3000 });
        this.dialogRef.close(true);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snackBar.open('Failed to save meal', 'Close', { duration: 3000 });
      }
    });
  }
  onCancel(): void {
    this.dialogRef.close(false);
  }
}