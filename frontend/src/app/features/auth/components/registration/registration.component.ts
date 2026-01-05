import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit {
  registrationForm!: FormGroup;
  loading = false;
  hidePassword = true;

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
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.registrationForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    } else {
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
    let i = 0;
    const typeSub = () => {
      if (i < this.subText.length) {
        this.displayedSubText += this.subText.charAt(i);
        i++;
        setTimeout(typeSub, 50);
      } else {
        this.subTypingComplete = true;
        setTimeout(() => {
          this.subTypingComplete = false;
        }, 100);
      }
    };
    typeSub();
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.registrationForm.valid) {
      this.loading = true;
      const { confirmPassword, ...userData } = this.registrationForm.value;
      this.authService.register(userData).subscribe({
        next: () => {
          this.snackBar.open('Registration successful! Please login.', 'Close', {
            duration: 5000
          });
          this.router.navigate(['/login']);
          this.loading = false;
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

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  navigateToRegister(): void {

  }
}


