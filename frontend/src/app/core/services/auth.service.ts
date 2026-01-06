import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, catchError, map, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, RegistrationRequest, AuthResponse } from '../models/auth.models';
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<string | null>(this.getUserIdFromToken());
  public currentUser$ = this.currentUserSubject.asObservable();
  constructor(private http: HttpClient) {}
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials, {
      withCredentials: true
    }).pipe(
      tap(response => {
        this.setTokens(response);
        this.currentUserSubject.next(this.getUserIdFromToken());
      })
    );
  }
  register(userData: RegistrationRequest): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/registration`, userData, {
      responseType: 'text' as 'json',
      withCredentials: true
    });
  }
  refreshToken(): Observable<AuthResponse> {
    const refreshTokenFromStorage = this.getRefreshToken();
    const headers: { [key: string]: string } = {};
    if (refreshTokenFromStorage) {
      headers['X-Refresh-Token'] = refreshTokenFromStorage;
    }
    return this.http.get<AuthResponse>(`${this.apiUrl}/refreshtoken`, {
      withCredentials: true,
      headers: headers
    }).pipe(
      tap(response => {
        this.setTokens(response);
        this.currentUserSubject.next(this.getUserIdFromToken());
      })
    );
  }
  logout(): void {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('user_id');
    this.currentUserSubject.next(null);
  }
  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }
  getRefreshToken(): string | null {
    return localStorage.getItem('refresh_token');
  }
  getUserId(): string | null {
    return localStorage.getItem('user_id');
  }
  isAuthenticated(): boolean {
    const token = this.getToken();
    return !!token && !this.isTokenExpired(token);
  }
  attemptTokenRefreshIfNeeded(): Observable<boolean> {
    const token = this.getToken();
    const refreshToken = this.getRefreshToken();
    if (!token && !refreshToken) {
      return of(false);
    }
    if (token && !this.isTokenExpired(token)) {
      return of(true);
    }
    if (refreshToken) {
      return this.refreshToken().pipe(
        map(() => true),
        catchError(() => {
          this.logout();
          return of(false);
        })
      );
    }
    return of(false);
  }
  private setTokens(response: AuthResponse): void {
    localStorage.setItem('jwt_token', response.jwtToken);
    localStorage.setItem('refresh_token', response.refreshToken);
    const userId = this.getUserIdFromToken(response.jwtToken);
    if (userId) {
      localStorage.setItem('user_id', userId);
    }
  }
  private getUserIdFromToken(token?: string): string | null {
    const tokenToDecode = token || this.getToken();
    if (!tokenToDecode) {
      return null;
    }
    try {
      const payload = JSON.parse(atob(tokenToDecode.split('.')[1]));
      return payload.sub || payload.userId || null;
    } catch {
      return null;
    }
  }
  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiry = payload.exp * 1000;
      return Date.now() >= expiry;
    } catch {
      return true;
    }
  }
}