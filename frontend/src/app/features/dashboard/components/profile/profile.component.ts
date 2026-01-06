import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ThemeService, Theme } from '../../../../core/services/theme.service';
import { UserInfoResponse, UserDetailsDto, UpdateUserDetailsDto } from '../../../../core/models/user.models';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { WarningDialogComponent } from './warning-dialog.component';
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
  private initialHeight: number | null = null;
  private initialBirthDay: Date | null = null;
  private initialSex: string | null = null;
  private isInitializing = false;
  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private snackBar: MatSnackBar,
    private authService: AuthService,
    private router: Router,
    private themeService: ThemeService,
    private dialog: MatDialog
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
    this.setupFieldChangeListeners();
  }
  setupFieldChangeListeners(): void {
    this.profileForm.get('height')?.valueChanges.subscribe(() => {
      this.checkSensitiveFieldChange('height');
    });
    this.profileForm.get('birthDay')?.valueChanges.subscribe(() => {
      this.checkSensitiveFieldChange('birthDay');
    });
    this.profileForm.get('sex')?.valueChanges.subscribe(() => {
      this.checkSensitiveFieldChange('sex');
    });
  }
  checkSensitiveFieldChange(fieldName: string): void {
    if (this.isInitializing || !this.detailsExist) {
      return;
    }
    const currentValue = this.profileForm.get(fieldName)?.value;
    let initialValue: any = null;
    switch (fieldName) {
      case 'height':
        initialValue = this.initialHeight;
        if (currentValue !== null && initialValue !== null) {
          if (Math.abs(Number(currentValue) - Number(initialValue)) < 0.01) {
            return;
          }
        }
        break;
      case 'birthDay':
        initialValue = this.initialBirthDay;
        if (currentValue && initialValue) {
          const currentDate = new Date(currentValue);
          const initialDate = new Date(initialValue);
          if (currentDate.toDateString() === initialDate.toDateString()) {
            return;
          }
        } else if (!currentValue && !initialValue) {
          return;
        }
        break;
      case 'sex':
        initialValue = this.initialSex;
        if (currentValue === initialValue) {
          return;
        }
        break;
    }
    if (currentValue !== null && currentValue !== '' && initialValue !== null && currentValue !== initialValue) {
      this.showWarningDialog(fieldName);
    }
  }
  showWarningDialog(fieldName: string): void {
    const dialogRef = this.dialog.open(WarningDialogComponent, {
      width: '550px',
      maxWidth: '90vw',
      disableClose: true,
      panelClass: 'warning-dialog-container',
      data: { fieldName: fieldName },
      autoFocus: true,
      hasBackdrop: true
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
      }
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
    this.isInitializing = true;
    this.apiService.getUserInfo().subscribe({
      next: (data) => {
        this.userInfo = data;
        let birthDay: Date | null = null;
        if (data.birthday_date) {
          birthDay = new Date(data.birthday_date);
        }
        let activityLevel = '';
        if (data.activity_type) {
          const activityMap: { [key: string]: string } = {
            '1.2': 'SEDENTARY',
            '1.375': 'LIGHTLY_ACTIVE',
            '1.55': 'MODERATELY_ACTIVE',
            '1.725': 'VERY_ACTIVE'
          };
          activityLevel = activityMap[data.activity_type.toString()] || data.activity_type.toString();
        }
        let goal = '';
        if (data.goalType) {
          const goalMap: { [key: string]: string } = {
            'LOSS': 'LOSE_WEIGHT',
            'MAINTENANCE': 'MAINTAIN_WEIGHT',
            'GAIN': 'GAIN_WEIGHT'
          };
          goal = goalMap[data.goalType] || data.goalType;
        }
        this.initialHeight = data.height || null;
        this.initialBirthDay = birthDay;
        this.initialSex = data.sex || null;
        this.profileForm.patchValue({
          height: data.height,
          weight: data.weight,
          birthDay: birthDay,
          sex: data.sex,
          activityLevel: activityLevel,
          goal: goal
        });
        setTimeout(() => {
          this.isInitializing = false;
        }, 100);
        this.loading = false;
      },
      error: () => {
        this.isInitializing = false;
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
          wantedWeight: formData.weight || 0,
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