import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { ApiService } from '../services/api.service';
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private apiService: ApiService,
    private router: Router
  ) {}
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
    console.log('AuthGuard checking authentication for:', state.url);
    return this.authService.attemptTokenRefreshIfNeeded().pipe(
      switchMap((isAuthenticated) => {
        if (!isAuthenticated) {
          console.log('AuthGuard: User not authenticated after refresh attempt, redirecting to login');
          this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
          return of(false);
        }
        console.log('AuthGuard: User is authenticated');
        if (state.url.startsWith('/dashboard')) {
          console.log('AuthGuard: Checking user details before accessing dashboard');
          return this.apiService.userDetailsExists().pipe(
            map((exists) => {
              if (!exists) {
                console.log('AuthGuard: User details do not exist, redirecting to setup-profile');
                this.router.navigate(['/login/setup-profile']);
                return false;
              }
              console.log('AuthGuard: User details exist, allowing access to dashboard');
              return true;
            }),
            catchError((error) => {
              console.error('AuthGuard: Error checking user details', error);
              return of(true);
            })
          );
        }
        return of(true);
      }),
      catchError((error) => {
        console.error('AuthGuard: Error during authentication check', error);
        this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
        return of(false);
      })
    );
  }
}