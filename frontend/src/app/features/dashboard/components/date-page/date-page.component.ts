import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ApiService } from '../../../../core/services/api.service';
import { MainPageResponse } from '../../../../core/models/calorie.models';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AddWaterDialogComponent } from '../add-water-dialog/add-water-dialog.component';
import { AddMealComponent } from '../add-meal/add-meal.component';
@Component({
  selector: 'app-date-page',
  templateUrl: './date-page.component.html',
  styleUrls: ['./date-page.component.scss']
})
export class DatePageComponent implements OnInit {
  mainPageData: MainPageResponse | null = null;
  loading = true;
  selectedDate: string = '';
  selectedDateObj: Date = new Date();
  constructor(
    private apiService: ApiService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private router: Router
  ) {}
  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.selectedDate = params['date'];
      if (this.selectedDate) {
        const parts = this.selectedDate.split('-');
        if (parts.length === 3) {
          const year = parseInt(parts[0], 10);
          const month = parseInt(parts[1], 10) - 1;
          const day = parseInt(parts[2], 10);
          this.selectedDateObj = new Date(year, month, day);
        } else {
          this.selectedDateObj = new Date(this.selectedDate);
        }
        this.loadDatePageData();
      } else {
        this.router.navigate(['/dashboard/main']);
      }
    });
  }
  loadDatePageData(): void {
    this.loading = true;
    this.apiService.getPageByDate(this.selectedDate).subscribe({
      next: (data) => {
        this.mainPageData = data;
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.snackBar.open('Failed to load date page data', 'Close', { duration: 3000 });
      }
    });
  }
  getFormattedDate(): string {
    return this.selectedDateObj.toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
  getTodayCaloriesLeft(): number {
    if (!this.mainPageData?.todayCalory) return 0;
    const diff = this.mainPageData.todayCalory.todayCaloryNorm - this.mainPageData.todayCalory.todayCaloryCons;
    return Math.max(0, diff);
  }
  getTodayCaloriesOver(): number {
    if (!this.mainPageData?.todayCalory) return 0;
    const diff = this.mainPageData.todayCalory.todayCaloryCons - this.mainPageData.todayCalory.todayCaloryNorm;
    return Math.max(0, diff);
  }
  getFatsProgress(): number {
    if (!this.mainPageData?.todayFat) return 0;
    const { todayFatCons, todayFatNorm } = this.mainPageData.todayFat;
    if (todayFatNorm === 0) return 0;
    return Math.min((todayFatCons / todayFatNorm) * 100, 100);
  }
  getFatsOverPercentage(): number {
    if (!this.mainPageData?.todayFat) return 0;
    const { todayFatCons, todayFatNorm } = this.mainPageData.todayFat;
    if (todayFatNorm === 0) return 0;
    const over = ((todayFatCons - todayFatNorm) / todayFatNorm) * 100;
    return Math.max(0, over);
  }
  getProteinProgress(): number {
    if (!this.mainPageData?.todayProtein) return 0;
    const { todayProteinCons, todayProteinNorm } = this.mainPageData.todayProtein;
    if (todayProteinNorm === 0) return 0;
    return Math.min((todayProteinCons / todayProteinNorm) * 100, 100);
  }
  getProteinOverPercentage(): number {
    if (!this.mainPageData?.todayProtein) return 0;
    const { todayProteinCons, todayProteinNorm } = this.mainPageData.todayProtein;
    if (todayProteinNorm === 0) return 0;
    const over = ((todayProteinCons - todayProteinNorm) / todayProteinNorm) * 100;
    return Math.max(0, over);
  }
  getCarbsProgress(): number {
    if (!this.mainPageData?.todayCarbs) return 0;
    const { todayCarbsCons, todayCarbsNorm } = this.mainPageData.todayCarbs;
    if (todayCarbsNorm === 0) return 0;
    return Math.min((todayCarbsCons / todayCarbsNorm) * 100, 100);
  }
  getCarbsOverPercentage(): number {
    if (!this.mainPageData?.todayCarbs) return 0;
    const { todayCarbsCons, todayCarbsNorm } = this.mainPageData.todayCarbs;
    if (todayCarbsNorm === 0) return 0;
    const over = ((todayCarbsCons - todayCarbsNorm) / todayCarbsNorm) * 100;
    return Math.max(0, over);
  }
  getWaterProgress(): number {
    if (!this.mainPageData?.todayWater) return 0;
    const { todayWaterCons, todayWaterNeeds } = this.mainPageData.todayWater;
    if (todayWaterNeeds === 0) return 0;
    return Math.min((todayWaterCons / todayWaterNeeds) * 100, 100);
  }
  openAddWaterDialog(): void {
    const dialogRef = this.dialog.open(AddWaterDialogComponent, {
      width: '400px',
      data: { currentWater: this.mainPageData?.todayWater?.todayWaterCons || 0 }
    });
    dialogRef.afterClosed().subscribe(amount => {
      if (amount) {
        this.apiService.addWater(this.selectedDate, amount).subscribe({
          next: () => {
            this.loadDatePageData();
            this.snackBar.open('Water added successfully', 'Close', { duration: 2000 });
          },
          error: () => {
            this.snackBar.open('Failed to add water', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }
  getFatsConsumed(): number {
    return this.mainPageData?.todayFat?.todayFatCons || 0;
  }
  getFatsNorm(): number {
    return this.mainPageData?.todayFat?.todayFatNorm || 0;
  }
  getProteinConsumed(): number {
    return this.mainPageData?.todayProtein?.todayProteinCons || 0;
  }
  getProteinNorm(): number {
    return this.mainPageData?.todayProtein?.todayProteinNorm || 0;
  }
  getCarbsConsumed(): number {
    return this.mainPageData?.todayCarbs?.todayCarbsCons || 0;
  }
  getCarbsNorm(): number {
    return this.mainPageData?.todayCarbs?.todayCarbsNorm || 0;
  }
  getWaterConsumed(): number {
    return this.mainPageData?.todayWater?.todayWaterCons || 0;
  }
  getWaterNeeds(): number {
    return this.mainPageData?.todayWater?.todayWaterNeeds || 0;
  }
  getMeals(): any[] {
    const mealTypes = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'];
    const existingMeals = this.mainPageData?.mealPage || [];
    const mealMap = new Map(existingMeals.map(m => [m.mealType, m]));
    return mealTypes.map(type => {
      const existing = mealMap.get(type);
      return existing || { mealType: type, caloryCons: 0 };
    });
  }
  openAddMealDialog(mealType: string): void {
    const dialogRef = this.dialog.open(AddMealComponent, {
      width: '700px',
      maxHeight: '90vh',
      data: {
        mealType: mealType,
        date: this.selectedDateObj
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadDatePageData();
      }
    });
  }
  goBack(): void {
    this.router.navigate(['/dashboard/calendar']);
  }
}