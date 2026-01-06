import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, switchMap, filter, take } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    if (request.url.includes('/auth/login') ||
        request.url.includes('/auth/registration') ||
        request.url.includes('/auth/refreshtoken')) {
      if (request.url.includes('/auth/refreshtoken')) {
        request = request.clone({
          withCredentials: true
        });
      }
      return next.handle(request);
    }
    if (token) {
      request = this.addTokenHeader(request, token);
    }
    request = request.clone({
      withCredentials: true
    });
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && !request.url.includes('/auth/')) {
          return this.handle401Error(request, next);
        }
        return throwError(() => error);
      })
    );
  }
  private addTokenHeader(request: HttpRequest<any>, token: string): HttpRequest<any> {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);
      return this.authService.refreshToken().pipe(
        switchMap((response: any) => {
          this.isRefreshing = false;
          this.refreshTokenSubject.next(response.jwtToken);
          const retryRequest = this.addTokenHeader(request, response.jwtToken).clone({
            withCredentials: true
          });
          return next.handle(retryRequest);
        }),
        catchError((err) => {
          this.isRefreshing = false;
          this.refreshTokenSubject.next(null);
          this.authService.logout();
          this.router.navigate(['/login']);
          return throwError(() => err);
        })
      );
    }
    return this.refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap((token) => {
        const retryRequest = this.addTokenHeader(request, token).clone({
          withCredentials: true
        });
        return next.handle(retryRequest);
      }),
      catchError((err) => {
        if (err.status === 401) {
          this.isRefreshing = false;
          this.refreshTokenSubject.next(null);
          this.authService.logout();
          this.router.navigate(['/login']);
        }
        return throwError(() => err);
      })
    );
  }
}