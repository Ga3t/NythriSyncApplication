import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { ApiService } from '../../../../core/services/api.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  registrationForm!: FormGroup;
  loading = false;
  returnUrl = '';
  hidePassword = true;
  activeTab: 'login' | 'signup' = 'login';

  mainText = 'Eat consciously. Live easily.';
  subText = 'Your personal food diary that turns self-care into a simple habit.';
  displayedMainText = '';
  displayedSubText = '';
  mainTextComplete = false;
  subTypingComplete = false;
  typingComplete = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private apiService: ApiService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    console.log('LoginComponent constructor called');
    try {
      this.loginForm = this.fb.group({
        login: ['', [Validators.required]],
        password: ['', [Validators.required, Validators.minLength(6)]]
      });
      
      this.registrationForm = this.fb.group({
        username: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', [Validators.required]]
      }, { validators: this.passwordMatchValidator });
      
      console.log('LoginComponent forms created successfully');
    } catch (error) {
      console.error('Error creating forms:', error);
      throw error;
    }
  }

  passwordMatchValidator(form: AbstractControl) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  ngOnInit(): void {
    console.log('LoginComponent ngOnInit called');
    console.log('LoginForm status:', this.loginForm ? 'exists' : 'missing');
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
    if (this.authService.isAuthenticated()) {
      console.log('User already authenticated, redirecting to:', this.returnUrl);
      this.router.navigate([this.returnUrl]);
    } else {
      console.log('User not authenticated, showing login form');
      console.log('LoginComponent should be visible now');
      this.startTypewriter();
    }
  }

  startTypewriter(): void {

    let i = 0;
    const typeMain = () => {
      if (i < this.mainText.length) {
        this.displayedMainText += this.mainText.charAt(i);
        i++;
        setTimeout(typeMain, 80);
      } else {
        this.mainTextComplete = true;
        this.typingComplete = true;
        setTimeout(() => {
          this.typingComplete = false;
        }, 100);

        setTimeout(() => this.typeSubText(), 500);
      }
    };
    setTimeout(typeMain, 500);
  }

  typeSubText(): void {
    this.displayedSubText = '';
    let i = 0;
    const typeSub = () => {
      if (i < this.subText.length) {
        this.displayedSubText += this.subText.charAt(i);
        i++;
        setTimeout(typeSub, 50);
      } else {
        this.subTypingComplete = true;
      }
    };
    setTimeout(typeSub, 300);
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.loading = true;
      this.authService.login(this.loginForm.value).subscribe({
        next: () => {
          this.checkUserDetailsAndNavigate();
          this.loading = false;
        },
        error: (error) => {
          this.loading = false;
          this.snackBar.open('Login failed. Please check your credentials.', 'Close', {
            duration: 5000
          });
        }
      });
    }
  }

  checkUserDetailsAndNavigate(): void {
    this.apiService.userDetailsExists().subscribe({
      next: (exists) => {
        if (!exists) {

          this.router.navigate(['/login/setup-profile']);
        } else {

          this.router.navigate([this.returnUrl]);
        }
      },
      error: () => {

        this.router.navigate([this.returnUrl]);
      }
    });
  }

  navigateToRegister(): void {
    this.activeTab = 'signup';
  }

  navigateToLogin(): void {
    this.activeTab = 'login';
  }

  onRegisterSubmit(): void {
    if (this.registrationForm.valid) {
      this.loading = true;
      const { confirmPassword, ...userData } = this.registrationForm.value;
      this.authService.register(userData).subscribe({
        next: () => {

          this.authService.login({
            login: userData.username,
            password: userData.password
          }).subscribe({
            next: () => {
              this.checkUserDetailsAndNavigate();
              this.loading = false;
            },
            error: () => {
              this.snackBar.open('Registration successful! Please login.', 'Close', {
                duration: 5000
              });
              this.activeTab = 'login';
              this.loading = false;
            }
          });
        },
        error: (error) => {
          this.loading = false;
          this.snackBar.open('Registration failed. Please try again.', 'Close', {
            duration: 5000
          });
        }
      });
    }
  }
}


