import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { UserDetailsDto } from '../../../../core/models/user.models';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-setup-profile',
  templateUrl: './setup-profile.component.html',
  styleUrls: ['./setup-profile.component.scss']
})
export class SetupProfileComponent implements OnInit {
  profileForm!: FormGroup;
  loading = false;
  minDate: Date;
  maxDate: Date;

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {

    const today = new Date();
    this.maxDate = new Date(today.getFullYear() - 13, today.getMonth(), today.getDate());
    this.minDate = new Date(today.getFullYear() - 120, today.getMonth(), today.getDate());
  }

  ngOnInit(): void {
    this.profileForm = this.fb.group({
      height: [null, [Validators.required, Validators.min(50), Validators.max(250)]],
      currentWeight: [null, [Validators.required, Validators.min(20), Validators.max(500)]],
      birthDay: [null, [Validators.required]],
      sex: [null, [Validators.required]],
      activityType: [null, [Validators.required]],
      goalType: [null, [Validators.required]]
    });
  }

  getActivityLevelDisplay(): string {
    const value = this.profileForm.get('activityType')?.value;
    if (!value) return 'Select your activity level';
    
    const activityMap: { [key: number]: string } = {
      1.2: 'Sedentary (little or no exercise)',
      1.375: 'Lightly Active (light exercise 1-3 days/week)',
      1.55: 'Moderately Active (moderate exercise 3-5 days/week)',
      1.725: 'Very Active (hard exercise 6-7 days/week)',
      1.9: 'Extremely Active (very hard exercise, physical job)'
    };
    
    return activityMap[value] || 'Select your activity level';
  }

  getGoalDisplay(): string {
    const value = this.profileForm.get('goalType')?.value;
    if (!value || value === '') return 'Select your goal';
    
    const goalMap: { [key: string]: string } = {
      'LOSS': 'Lose Weight',
      'MAINTENANCE': 'Maintain Weight',
      'GAIN': 'Gain Weight'
    };
    
    return goalMap[value] || 'Select your goal';
  }

  getGenderDisplay(): string {
    const value = this.profileForm.get('sex')?.value;
    if (!value || value === '') return 'Select your gender';
    
    const genderMap: { [key: string]: string } = {
      'MALE': 'Male',
      'FEMALE': 'Female'
    };
    
    return genderMap[value] || 'Select your gender';
  }

  onSubmit(): void {
    if (this.profileForm.valid) {
      this.loading = true;
      const formValue = this.profileForm.value;

      const birthDay = formValue.birthDay ? new Date(formValue.birthDay).toISOString().split('T')[0] : null;
      
      const userDetails: UserDetailsDto = {
        height: formValue.height,
        currentWeight: formValue.currentWeight,
        wantedWeight: formValue.currentWeight,
        birthDay: birthDay!,
        sex: formValue.sex,
        activityType: formValue.activityType,
        goalType: formValue.goalType
      };

      this.apiService.setUserDetails(userDetails).subscribe({
        next: () => {
          this.snackBar.open('Profile setup complete!', 'Close', {
            duration: 3000
          });
          this.router.navigate(['/dashboard']);
          this.loading = false;
        },
        error: () => {
          this.loading = false;
          this.snackBar.open('Failed to save profile. Please try again.', 'Close', {
            duration: 5000
          });
        }
      });
    }
  }
}

