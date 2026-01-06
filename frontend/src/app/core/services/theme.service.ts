import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
export type Theme = 'light' | 'dark';
@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly THEME_KEY = 'app-theme';
  private themeSubject = new BehaviorSubject<Theme>(this.getInitialTheme());
  public theme$: Observable<Theme> = this.themeSubject.asObservable();
  constructor() {
    const initialTheme = this.getInitialTheme();
    this.applyTheme(initialTheme);
  }
  private getInitialTheme(): Theme {
    const savedTheme = localStorage.getItem(this.THEME_KEY) as Theme;
    return savedTheme || 'light';
  }
  getCurrentTheme(): Theme {
    return this.themeSubject.value;
  }
  toggleTheme(): void {
    const newTheme = this.getCurrentTheme() === 'light' ? 'dark' : 'light';
    this.setTheme(newTheme);
  }
  setTheme(theme: Theme): void {
    this.themeSubject.next(theme);
    localStorage.setItem(this.THEME_KEY, theme);
    this.applyTheme(theme);
  }
  private applyTheme(theme: Theme): void {
    const body = document.body;
    const html = document.documentElement;
    body.classList.remove('dark-theme', 'light-theme');
    html.classList.remove('dark-theme', 'light-theme');
    if (theme === 'dark') {
      body.classList.add('dark-theme');
      html.classList.add('dark-theme');
    } else {
      body.classList.add('light-theme');
      html.classList.add('light-theme');
    }
  }
}