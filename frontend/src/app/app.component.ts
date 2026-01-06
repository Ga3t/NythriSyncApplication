import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from './core/services/auth.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'NutriSync';
  constructor(
    private router: Router,
    private authService: AuthService
  ) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      console.log('Navigation ended to:', event.url);
    });
  }
  ngOnInit(): void {
    console.log('AppComponent initialized');
    console.log('Current URL:', window.location.href);
    console.log('Router config:', this.router.config);
    this.authService.attemptTokenRefreshIfNeeded().subscribe({
      next: (isAuthenticated) => {
        if (isAuthenticated) {
          console.log('Token refreshed successfully on app initialization');
        } else {
          console.log('No valid tokens found, user needs to login');
        }
      },
      error: (error) => {
        console.error('Error during token refresh on initialization:', error);
      }
    });
  }
}