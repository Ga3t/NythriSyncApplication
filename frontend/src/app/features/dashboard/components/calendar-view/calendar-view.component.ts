import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../../../core/services/api.service';
import { CalendarResponse, CalendarDayData } from '../../../../core/models/calorie.models';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
interface CalendarDayDisplay {
  date: Date;
  totalCalories: number;
  hasMeals: boolean;
  caloryNorm?: number;
  caloryCons?: number;
}
@Component({
  selector: 'app-calendar-view',
  templateUrl: './calendar-view.component.html',
  styleUrls: ['./calendar-view.component.scss']
})
export class CalendarViewComponent implements OnInit {
  calendarData: CalendarResponse | null = null;
  loading = true;
  selectedYear: number = new Date().getFullYear();
  selectedMonth: number = new Date().getMonth() + 1;
  constructor(
    private apiService: ApiService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}
  ngOnInit(): void {
    this.loadCalendar();
  }
  loadCalendar(): void {
    this.loading = true;
    this.apiService.getCalendar(this.selectedYear).subscribe({
      next: (data) => {
        this.calendarData = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snackBar.open('Failed to load calendar data', 'Close', { duration: 3000 });
      }
    });
  }
  onYearChange(year: string | number): void {
    this.selectedYear = typeof year === 'string' ? parseInt(year, 10) : year;
    this.loadCalendar();
  }
  getMonthDays(month: number): CalendarDayDisplay[] {
    if (!this.calendarData || !this.calendarData.calendar) return [];
    const monthDays: CalendarDayDisplay[] = this.calendarData.calendar
      .map((d: CalendarDayData): CalendarDayDisplay | null => {
        const dateStr = d.date;
        let date: Date;
        if (typeof dateStr === 'string') {
          const parts = dateStr.split('-');
          if (parts.length === 3) {
            const year = parseInt(parts[0], 10);
            const month = parseInt(parts[1], 10) - 1;
            const day = parseInt(parts[2], 10);
            date = new Date(year, month, day);
          } else {
            date = new Date(dateStr);
          }
        } else {
          date = new Date(dateStr);
        }
        if (isNaN(date.getTime())) return null;
        return {
          date: date,
          totalCalories: Number(d.caloryCons) || 0,
          hasMeals: (Number(d.caloryCons) || 0) > 0,
          caloryNorm: Number(d.caloryNorm) || 0,
          caloryCons: Number(d.caloryCons) || 0
        };
      })
      .filter((d): d is CalendarDayDisplay =>
        d !== null &&
        d.date.getFullYear() === this.selectedYear &&
        d.date.getMonth() === month - 1
      )
      .sort((a, b) =>
        a.date.getTime() - b.date.getTime()
      );
    return monthDays;
  }
  getDaysOfWeek(): string[] {
    return ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
  }
  getMonthName(month: number): string {
    const date = new Date(this.selectedYear, month - 1, 1);
    return date.toLocaleString('default', { month: 'long' });
  }
  getDayColor(day: CalendarDayDisplay): string {
    if (!day.hasMeals || !day.caloryCons || day.caloryCons === 0) return '';
    if (day.caloryNorm && day.caloryCons > day.caloryNorm) {
      return 'green';
    }
    if (day.caloryCons > 0) {
      return 'transparent-green';
    }
    return '';
  }
  getCellClass(day: CalendarDayDisplay | null): string {
    if (!day) return 'empty';
    const colorClass = this.getDayColor(day);
    return colorClass || '';
  }
  onDayClick(day: CalendarDayDisplay | null): void {
    if (!day) return;
    const dateStr = this.getDayDateString(day);
    this.router.navigate(['/dashboard/date', dateStr]);
  }
  getWeeksForMonth(month: number): (CalendarDayDisplay | null)[][] {
    const days = this.getMonthDays(month);
    const weeks: (CalendarDayDisplay | null)[][] = [];
    let currentWeek: (CalendarDayDisplay | null)[] = [];
    const firstDay = new Date(this.selectedYear, month - 1, 1).getDay();
    for (let i = 0; i < firstDay; i++) {
      currentWeek.push(null);
    }
    for (let i = 0; i < days.length; i++) {
      if (currentWeek.length === 7) {
        weeks.push(currentWeek);
        currentWeek = [];
      }
      currentWeek.push(days[i]);
    }
    while (currentWeek.length < 7) {
      currentWeek.push(null);
    }
    if (currentWeek.length > 0) {
      weeks.push(currentWeek);
    }
    return weeks;
  }
  getDayNumber(day: CalendarDayDisplay): number {
    return day.date.getDate();
  }
  getDayDateString(day: CalendarDayDisplay): string {
    const year = day.date.getFullYear();
    const month = String(day.date.getMonth() + 1).padStart(2, '0');
    const dayNum = String(day.date.getDate()).padStart(2, '0');
    return `${year}-${month}-${dayNum}`;
  }
}