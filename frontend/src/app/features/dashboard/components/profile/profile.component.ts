import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ThemeService, Theme } from '../../../../core/services/theme.service';
import { UserInfoResponse, UserDetailsDto, UpdateUserDetailsDto } from '../../../../core/models/user.models';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  profileForm!: FormGroup;
  userInfo: UserInfoResponse | null = null;
  loading = false;
  detailsExist = false;
  minDate: Date;
  maxDate: Date;
  currentTheme: Theme = 'light';

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private snackBar: MatSnackBar,
    private authService: AuthService,
    private router: Router,
    private themeService: ThemeService
  ) {

    const today = new Date();
    this.maxDate = new Date(today.getFullYear() - 13, today.getMonth(), today.getDate());
    this.minDate = new Date(today.getFullYear() - 120, today.getMonth(), today.getDate());
    
    this.profileForm = this.fb.group({
      height: [null, [Validators.min(0)]],
      weight: [null, [Validators.min(0)]],
      birthDay: [null],
      sex: [''],
      activityLevel: [''],
      goal: ['']
    });
  }

  ngOnInit(): void {
    this.checkUserDetails();
    this.loadUserInfo();
    this.themeService.theme$.subscribe(theme => {
      this.currentTheme = theme;
    });
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  checkUserDetails(): void {
    this.apiService.userDetailsExists().subscribe({
      next: (exists) => {
        this.detailsExist = exists;
        if (exists) {
          this.loadUserInfo();
        }
      }
    });
  }

  loadUserInfo(): void {
    this.loading = true;
    this.apiService.getUserInfo().subscribe({
      next: (data) => {
        this.userInfo = data;

        let birthDay: Date | null = null;
        if (data.age) {
          const today = new Date();
          birthDay = new Date(today.getFullYear() - data.age, today.getMonth(), today.getDate());
        }
        
        this.profileForm.patchValue({
          height: data.height,
          weight: data.currentWeight,
          birthDay: birthDay,
          sex: data.sex,
          activityLevel: data.activityLevel,
          goal: data.goal
        });
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.profileForm.valid) {
      this.loading = true;
      const formData = this.profileForm.value;

      if (this.detailsExist) {

        const updateData: UpdateUserDetailsDto = {
          height: formData.height,
          weight: formData.weight,
          sex: formData.sex,
          activityLevel: formData.activityLevel,
          goal: formData.goal
        };
        if (formData.birthDay) {
          const today = new Date();
          const birthDate = new Date(formData.birthDay);
          const age = today.getFullYear() - birthDate.getFullYear();
          const monthDiff = today.getMonth() - birthDate.getMonth();
          const calculatedAge = monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate()) ? age - 1 : age;
          updateData.age = calculatedAge;
        }
        this.apiService.updateUserDetails(updateData).subscribe({
          next: () => {
            this.snackBar.open('Profile updated successfully', 'Close', { duration: 3000 });
            this.loadUserInfo();
            this.loading = false;
          },
          error: () => {
            this.loading = false;
            this.snackBar.open('Failed to update profile', 'Close', { duration: 3000 });
          }
        });
      } else {

        const userDetails: UserDetailsDto = {
          height: formData.height,
          currentWeight: formData.weight,
          wantedWeight: formData.weight || formData.currentWeight || 0,
          birthDay: formData.birthDay ? new Date(formData.birthDay).toISOString().split('T')[0] : '',
          sex: formData.sex,
          activityType: this.mapActivityLevelToType(formData.activityLevel),
          goalType: this.mapGoalToType(formData.goal)
        };
        this.apiService.setUserDetails(userDetails).subscribe({
          next: () => {
            this.snackBar.open('Profile saved successfully', 'Close', { duration: 3000 });
            this.detailsExist = true;
            this.loadUserInfo();
            this.loading = false;
          },
          error: () => {
            this.loading = false;
            this.snackBar.open('Failed to save profile', 'Close', { duration: 3000 });
          }
        });
      }
    }
  }

  private mapActivityLevelToType(activityLevel: string): number {
    const mapping: { [key: string]: number } = {
      'SEDENTARY': 1.2,
      'LIGHTLY_ACTIVE': 1.375,
      'MODERATELY_ACTIVE': 1.55,
      'VERY_ACTIVE': 1.725
    };
    return mapping[activityLevel] || 1.2;
  }

  private mapGoalToType(goal: string): string {
    const mapping: { [key: string]: string } = {
      'LOSE_WEIGHT': 'LOSS',
      'MAINTAIN_WEIGHT': 'MAINTENANCE',
      'GAIN_WEIGHT': 'GAIN'
    };
    return mapping[goal] || 'MAINTENANCE';
  }

  addWeight(): void {
    const weight = this.profileForm.get('weight')?.value;
    if (weight && weight > 0) {
      this.loading = true;
      this.apiService.addWeight(weight).subscribe({
        next: (newWeight) => {
          this.snackBar.open(`Weight logged successfully: ${newWeight}kg`, 'Close', { duration: 3000 });
          this.loadUserInfo();
          this.loading = false;
        },
        error: () => {
          this.loading = false;
          this.snackBar.open('Failed to log weight', 'Close', { duration: 3000 });
        }
      });
    }
  }

  downloadApk(): void {
    const link = document.createElement('a');
    link.href = '/assets/NythriSync.apk';
    link.download = 'NythriSync.apk';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}

