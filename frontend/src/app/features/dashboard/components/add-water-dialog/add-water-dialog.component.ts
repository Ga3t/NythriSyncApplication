import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
@Component({
  selector: 'app-add-water-dialog',
  templateUrl: './add-water-dialog.component.html',
  styleUrls: ['./add-water-dialog.component.scss']
})
export class AddWaterDialogComponent {
  waterForm: FormGroup;
  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddWaterDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { currentWater: number }
  ) {
    this.waterForm = this.fb.group({
      amount: [250, [Validators.required, Validators.min(1), Validators.max(5000)]]
    });
  }
  onSubmit(): void {
    if (this.waterForm.valid) {
      this.dialogRef.close(this.waterForm.value.amount);
    }
  }
  onCancel(): void {
    this.dialogRef.close();
  }
}