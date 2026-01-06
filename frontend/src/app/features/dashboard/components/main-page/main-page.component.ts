import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { MainPageResponse } from '../../../../core/models/calorie.models';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AddWaterDialogComponent } from '../add-water-dialog/add-water-dialog.component';
import { AddMealComponent } from '../add-meal/add-meal.component';
import { WeightDialogComponent } from '../weight-dialog/weight-dialog.component';
@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent implements OnInit {
  mainPageData: MainPageResponse | null = null;
  loading = true;
  weekDays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  currentWeight: number = 0;
  showDownloadBanner: boolean = false;
  constructor(
    private apiService: ApiService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}
  ngOnInit(): void {
    this.loadMainPageData();
    this.loadCalendarData();
    this.loadUserWeight();
    this.checkBannerVisibility();
  }
  checkBannerVisibility(): void {
    const bannerDismissed = localStorage.getItem('downloadBannerDismissed');
    this.showDownloadBanner = bannerDismissed !== 'true';
  }
  closeBanner(): void {
    this.showDownloadBanner = false;
    localStorage.setItem('downloadBannerDismissed', 'true');
  }
  downloadApk(): void {
    const link = document.createElement('a');
    link.href = '/assets/NythriSync.apk';
    link.download = 'NythriSync.apk';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
  loadUserWeight(): void {
    this.apiService.getUserInfo().subscribe({
      next: (data) => {
        this.currentWeight = data.weight || 0;
      },
      error: () => {
      }
    });
  }
  openWeightDialog(): void {
    const dialogRef = this.dialog.open(WeightDialogComponent, {
      width: '500px',
      data: { currentWeight: this.currentWeight }
    });
    dialogRef.afterClosed().subscribe(weight => {
      if (weight && weight > 0) {
        this.apiService.addWeight(weight).subscribe({
          next: (updatedWeight) => {
            this.currentWeight = updatedWeight;
            this.snackBar.open('Weight updated successfully', 'Close', { duration: 2000 });
          },
          error: () => {
            this.snackBar.open('Failed to update weight', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }
  loadMainPageData(): void {
    this.loading = true;
    this.apiService.getMainPageInfoNew().subscribe({
      next: (data) => {
        this.mainPageData = data;
        this.loading = false;
        if (this.calendarData) {
          this.loadCalendarData();
        }
      },
      error: (error) => {
        this.loading = false;
        this.snackBar.open('Failed to load dashboard data', 'Close', { duration: 3000 });
      }
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
  getWeekData(): any[] {
    if (!this.mainPageData?.weekCalory) return [];
    const cons = this.mainPageData.weekCalory.thisWeekCaloryCons || {};
    const norm = this.mainPageData.weekCalory.thisWeekCaloryNorm || {};
    const dayOrder = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    const allDays = dayOrder.map(dayKey => {
      const consumed = cons[dayKey] || 0;
      const normal = norm[dayKey] || 0;
      const diff = consumed - normal;
      const percentage = normal > 0 ? (consumed / normal) * 100 : 0;
      return {
        day: dayKey,
        consumed,
        normal,
        diff,
        percentage: Math.min(percentage, 100),
        hasData: consumed > 0 || normal > 0
      };
    });
    let lastIndexWithData = -1;
    for (let i = allDays.length - 1; i >= 0; i--) {
      if (allDays[i].hasData) {
        lastIndexWithData = i;
        break;
      }
    }
    if (lastIndexWithData >= 0) {
      return allDays.slice(0, lastIndexWithData + 1);
    }
    return [];
  }
  getMaxWeekValue(): number {
    const data = this.getWeekData();
    if (data.length === 0) return 100;
    const maxConsumed = Math.max(...data.map(d => d.consumed), 0);
    const maxNormal = Math.max(...data.map(d => d.normal), 0);
    return Math.max(maxConsumed, maxNormal, 100);
  }
  getLineChartWidth(): number {
    return 900;
  }
  getLineChartHeight(): number {
    return 200;
  }
  getLineChartPadding(): { top: number; right: number; bottom: number; left: number } {
    return { top: 15, right: 30, bottom: 35, left: 50 };
  }
  getYAxisLabels(): { value: number; y: number }[] {
    const maxValue = this.getMaxWeekValue();
    const padding = this.getLineChartPadding();
    const height = this.getLineChartHeight();
    const chartHeight = height - padding.top - padding.bottom;
    const labels: { value: number; y: number }[] = [];
    const numLabels = 5;
    for (let i = 0; i <= numLabels; i++) {
      const value = (maxValue / numLabels) * (numLabels - i);
      const y = padding.top + (i / numLabels) * chartHeight;
      labels.push({ value, y });
    }
    return labels;
  }
  getYAxisTitleTransform(): string {
    const height = this.getLineChartHeight();
    const centerY = height / 2;
    return `rotate(-90, 15, ${centerY})`;
  }
  getChartDataPoints(): { consumed: { x: number; y: number }[]; norm: { x: number; y: number }[] } {
    const data = this.getWeekData();
    if (data.length === 0) return { consumed: [], norm: [] };
    const maxValue = this.getMaxWeekValue();
    const padding = this.getLineChartPadding();
    const width = this.getLineChartWidth();
    const height = this.getLineChartHeight();
    const chartWidth = width - padding.left - padding.right;
    const chartHeight = height - padding.top - padding.bottom;
    const consumedPoints: { x: number; y: number }[] = [];
    const normPoints: { x: number; y: number }[] = [];
    data.forEach((day, index) => {
      const x = padding.left + (index / (data.length - 1 || 1)) * chartWidth;
      const consumedY = padding.top + chartHeight - (day.consumed / maxValue) * chartHeight;
      const normY = padding.top + chartHeight - (day.normal / maxValue) * chartHeight;
      consumedPoints.push({ x, y: consumedY });
      normPoints.push({ x, y: normY });
    });
    return { consumed: consumedPoints, norm: normPoints };
  }
  getLinePath(points: { x: number; y: number }[]): string {
    if (points.length === 0) return '';
    return points.map((point, index) => {
      return index === 0 ? `M ${point.x} ${point.y}` : `L ${point.x} ${point.y}`;
    }).join(' ');
  }
  getLastDataPoint(): { consumed: { x: number; y: number } | null; norm: { x: number; y: number } | null } {
    const { consumed: consumedPoints, norm: normPoints } = this.getChartDataPoints();
    const lastConsumed = consumedPoints.length > 0 ? consumedPoints[consumedPoints.length - 1] : null;
    const lastNorm = normPoints.length > 0 ? normPoints[normPoints.length - 1] : null;
    return { consumed: lastConsumed, norm: lastNorm };
  }
  getAreaPaths(): { deficit: string; surplus: string } {
    const data = this.getWeekData();
    if (data.length === 0) return { deficit: '', surplus: '' };
    const { consumed: consumedPoints, norm: normPoints } = this.getChartDataPoints();
    const deficitSegments: string[] = [];
    const surplusSegments: string[] = [];
    for (let i = 0; i < consumedPoints.length - 1; i++) {
      const c1 = consumedPoints[i];
      const c2 = consumedPoints[i + 1];
      const n1 = normPoints[i];
      const n2 = normPoints[i + 1];
      const isDeficit1 = c1.y > n1.y;
      const isDeficit2 = c2.y > n2.y;
      if (isDeficit1 && isDeficit2) {
        deficitSegments.push(`M ${c1.x} ${c1.y} L ${c2.x} ${c2.y} L ${n2.x} ${n2.y} L ${n1.x} ${n1.y} Z`);
      } else if (!isDeficit1 && !isDeficit2) {
        surplusSegments.push(`M ${n1.x} ${n1.y} L ${n2.x} ${n2.y} L ${c2.x} ${c2.y} L ${c1.x} ${c1.y} Z`);
      } else {
        const denom = (c2.y - c1.y) - (n2.y - n1.y);
        if (Math.abs(denom) > 0.001) {
          const t = (n1.y - c1.y) / denom;
          const ix = c1.x + t * (c2.x - c1.x);
          const iy = c1.y + t * (c2.y - c1.y);
          if (isDeficit1) {
            deficitSegments.push(`M ${c1.x} ${c1.y} L ${ix} ${iy} L ${n1.x} ${n1.y} Z`);
            surplusSegments.push(`M ${ix} ${iy} L ${c2.x} ${c2.y} L ${n2.x} ${n2.y} Z`);
          } else {
            surplusSegments.push(`M ${n1.x} ${n1.y} L ${ix} ${iy} L ${c1.x} ${c1.y} Z`);
            deficitSegments.push(`M ${ix} ${iy} L ${n2.x} ${n2.y} L ${c2.x} ${c2.y} Z`);
          }
        }
      }
    }
    return {
      deficit: deficitSegments.join(' '),
      surplus: surplusSegments.join(' ')
    };
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
    return (todayWaterCons / todayWaterNeeds) * 100;
  }
  getWaterProgressForChart(): number {
    return Math.min(this.getWaterProgress(), 100);
  }
  openAddWaterDialog(): void {
    const dialogRef = this.dialog.open(AddWaterDialogComponent, {
      width: '400px',
      data: { currentWater: this.mainPageData?.todayWater?.todayWaterCons || 0 }
    });
    dialogRef.afterClosed().subscribe(amount => {
      if (amount) {
        const today = new Date().toISOString().split('T')[0];
        this.apiService.addWater(today, amount).subscribe({
          next: () => {
            this.loadMainPageData();
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
        date: new Date()
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadMainPageData();
      }
    });
  }
  calendarData: any = null;
  calendarLoading = false;
  selectedMonth: Date = new Date();
  earliestDate: Date | null = null;
  loadCalendarData(): void {
    this.calendarLoading = true;
    const year = this.selectedMonth.getFullYear();
    this.apiService.getCalendar(year).subscribe({
      next: (data) => {
        this.calendarData = this.processCalendarData(data);
        this.calendarLoading = false;
      },
      error: (error) => {
        this.calendarLoading = false;
        console.error('Failed to load calendar data:', error);
      }
    });
  }
  processCalendarData(data: any): any {
    if (!data || !data.calendar || !Array.isArray(data.calendar)) return null;
    const selectedYear = this.selectedMonth.getFullYear();
    const selectedMonthIndex = this.selectedMonth.getMonth();
    const monthDays = data.calendar
      .map((d: any) => {
        const dateStr = d.date;
        let date: Date;
        if (typeof dateStr === 'string') {
          date = new Date(dateStr + 'T00:00:00');
        } else {
          date = new Date(dateStr);
        }
        const adjustedDate = new Date(date.getTime() + date.getTimezoneOffset() * 60000);
        if (isNaN(adjustedDate.getTime())) return null;
        return {
          date: adjustedDate,
          consumed: d.caloryCons != null ? Number(d.caloryCons) : 0,
          norm: d.caloryNorm != null ? Number(d.caloryNorm) : null
        };
      })
      .filter((d: any) => d !== null && d.date.getFullYear() === selectedYear && d.date.getMonth() === selectedMonthIndex)
      .sort((a: any, b: any) => a.date.getTime() - b.date.getTime());
    if (monthDays.length === 0) return null;
    const allDates = data.calendar
      .map((d: any) => {
        const dateStr = d.date;
        if (typeof dateStr === 'string') {
          return new Date(dateStr + 'T00:00:00');
        }
        return new Date(dateStr);
      })
      .filter((d: Date) => !isNaN(d.getTime()))
      .sort((a: Date, b: Date) => a.getTime() - b.getTime());
    if (allDates.length > 0) {
      this.earliestDate = allDates[0];
    }
    const weeks = this.organizeByWeeks(monthDays);
    const maxConsumed = Math.max(...monthDays.map((d: any) => d.consumed), 0);
    const maxNorm = Math.max(...monthDays.map((d: any) => d.norm), 0);
    return {
      weeks: weeks,
      days: monthDays,
      maxValue: Math.max(maxConsumed, maxNorm, 2000)
    };
  }
  organizeByWeeks(days: any[]): any[] {
    if (days.length === 0) return [];
    const weeks: any[] = [];
    let weekNumber = 1;
    let currentWeek: any = { weekNumber: weekNumber, days: [] };
    days.forEach((day, index) => {
      const dayOfWeek = day.date.getDay();
      const adjustedDay = dayOfWeek === 0 ? 7 : dayOfWeek;
      if (adjustedDay === 1 && currentWeek.days.length > 0) {
        if (currentWeek.days.length > 0) {
          weeks.push(currentWeek);
          weekNumber++;
        }
        currentWeek = { weekNumber: weekNumber, days: [] };
      }
      currentWeek.days.push(day);
    });
    if (currentWeek.days.length > 0) {
      weeks.push(currentWeek);
    }
    return weeks;
  }
  goToPreviousMonth(): void {
    const newDate = new Date(this.selectedMonth);
    newDate.setMonth(newDate.getMonth() - 1);
    this.selectedMonth = newDate;
    this.loadCalendarData();
  }
  goToNextMonth(): void {
    const newDate = new Date(this.selectedMonth);
    newDate.setMonth(newDate.getMonth() + 1);
    this.selectedMonth = newDate;
    this.loadCalendarData();
  }
  getMonthName(): string {
    return this.selectedMonth.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
  }
  canGoBackward(): boolean {
    if (!this.earliestDate) return true;
    const firstDayOfMonth = new Date(this.selectedMonth.getFullYear(), this.selectedMonth.getMonth(), 1);
    return firstDayOfMonth > this.earliestDate;
  }
  canGoForward(): boolean {
    const today = new Date();
    const lastDayOfMonth = new Date(this.selectedMonth.getFullYear(), this.selectedMonth.getMonth() + 1, 0);
    return lastDayOfMonth < today;
  }
  getDayName(dayIndex: number): string {
    const dayNames = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
    return dayNames[dayIndex];
  }
  getDayNameForDate(date: Date): string {
    const dayNames = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
    return dayNames[date.getDay()];
  }
  getMaxValueForWeek(week: any): number {
    if (!week || !week.days || week.days.length === 0) return 2000;
    const maxConsumed = Math.max(...week.days.map((d: any) => d.consumed || 0), 0);
    return Math.max(maxConsumed, 2000);
  }
}