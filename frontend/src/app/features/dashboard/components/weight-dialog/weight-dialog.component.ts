import { Component, Inject, OnInit, HostListener, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-weight-dialog',
  templateUrl: './weight-dialog.component.html',
  styleUrls: ['./weight-dialog.component.scss']
})
export class WeightDialogComponent implements OnInit {
  @ViewChild('dialContainer', { static: false }) dialContainer!: ElementRef;
  
  weightForm: FormGroup;
  currentWeight: number;
  isEditing: boolean = false;
  isDragging: boolean = false;
  rotationAngle: number = 0;
  private startAngle: number = 0;
  private startRotation: number = 0;
  private readonly minWeight: number = 0.1;
  private readonly maxWeight: number = 500;
  private readonly increment: number = 0.1;
  private readonly sensitivity: number = 0.4;
  visibleWeights: number[] = [];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<WeightDialogComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: { currentWeight: number }
  ) {
    this.currentWeight = data.currentWeight || 70;
    this.weightForm = this.fb.group({
      weight: [this.currentWeight, [Validators.required, Validators.min(this.minWeight)]]
    });
    this.updateRotationFromWeight();
  }

  ngOnInit(): void {
    this.weightForm.get('weight')?.valueChanges.subscribe(value => {
      if (value !== null && value !== undefined && !this.isDragging) {
        this.currentWeight = Math.max(this.minWeight, value);
        this.updateRotationFromWeight();
        this.updateVisibleWeights();
        this.checkWeightWarning();
      }
    });
    this.updateVisibleWeights();
  }

  updateRotationFromWeight(): void {


    const normalizedWeight = (this.currentWeight - this.minWeight) / (this.maxWeight - this.minWeight);
    this.rotationAngle = (normalizedWeight - 0.5) * 270;
  }

  updateWeightFromRotation(): void {

    const normalizedRotation = (this.rotationAngle + 135) / 270;
    const rawWeight = this.minWeight + normalizedRotation * (this.maxWeight - this.minWeight);

    this.currentWeight = Math.round(rawWeight * 10) / 10;
    this.currentWeight = Math.max(this.minWeight, Math.min(this.maxWeight, this.currentWeight));
    this.weightForm.patchValue({ weight: this.currentWeight }, { emitEvent: false });
    this.updateVisibleWeights();
    this.checkWeightWarning();
  }

  updateVisibleWeights(): void {

    const weights: number[] = [];
    const centerWeight = Math.round(this.currentWeight);
    const range = 8;
    
    for (let i = -range; i <= range; i++) {
      const weight = centerWeight + i;
      if (weight >= 0 && weight <= 300) {
        weights.push(weight);
      }
    }
    this.visibleWeights = weights;
  }

  getWeightPosition(weight: number): { angle: number; x: number; y: number } {


    const normalizedWeight = (weight - this.minWeight) / (this.maxWeight - this.minWeight);
    const angle = -135 + normalizedWeight * 270;
    const radius = 90;
    const centerX = 100;
    const centerY = 100;
    const rad = (angle * Math.PI) / 180;
    const x = centerX + radius * Math.cos(rad);
    const y = centerY + radius * Math.sin(rad);
    return { angle, x, y };
  }

  isWeightVisible(weight: number): boolean {


    const normalizedWeight = (weight - this.minWeight) / (this.maxWeight - this.minWeight);
    const weightAngle = -135 + normalizedWeight * 270;

    if (weightAngle < -135 || weightAngle > 135) {
      return false;
    }

    const diff = Math.abs(weight - this.currentWeight);
    return diff <= 6;
  }

  isCurrentWeight(weight: number): boolean {
    return Math.round(weight) === Math.round(this.currentWeight);
  }

  roundWeight(weight: number): number {
    return Math.round(weight);
  }

  getWeightOpacity(weight: number): string {
    if (!this.isWeightVisible(weight)) {
      return '0';
    }
    if (this.isCurrentWeight(weight)) {
      return '1';
    }

    const diff = Math.abs(weight - this.currentWeight);
    const maxDiff = 6;
    const opacity = 1 - (diff / maxDiff) * 0.7;
    return Math.max(0.3, opacity).toString();
  }

  onDialMouseDown(event: MouseEvent): void {
    if (this.isEditing) return;
    this.isDragging = true;
    this.startAngle = this.getAngleFromEvent(event);
    this.startRotation = this.rotationAngle;
    event.preventDefault();
  }

  @HostListener('document:mousemove', ['$event'])
  onMouseMove(event: MouseEvent): void {
    if (!this.isDragging) return;
    
    const currentAngle = this.getAngleFromEvent(event);
    const deltaAngle = (currentAngle - this.startAngle) * this.sensitivity;
    this.rotationAngle = this.startRotation + deltaAngle;

    this.rotationAngle = Math.max(-135, Math.min(135, this.rotationAngle));
    
    this.updateWeightFromRotation();
  }

  @HostListener('document:mouseup', ['$event'])
  onMouseUp(event: MouseEvent): void {
    if (this.isDragging) {
      this.isDragging = false;
    }
  }

  onDialTouchStart(event: TouchEvent): void {
    if (this.isEditing) return;
    this.isDragging = true;
    this.startAngle = this.getAngleFromTouch(event);
    this.startRotation = this.rotationAngle;
    event.preventDefault();
  }

  @HostListener('document:touchmove', ['$event'])
  onTouchMove(event: TouchEvent): void {
    if (!this.isDragging) return;
    event.preventDefault();
    
    const currentAngle = this.getAngleFromTouch(event);
    const deltaAngle = (currentAngle - this.startAngle) * this.sensitivity;
    this.rotationAngle = this.startRotation + deltaAngle;

    this.rotationAngle = Math.max(-135, Math.min(135, this.rotationAngle));
    
    this.updateWeightFromRotation();
  }

  @HostListener('document:touchend', ['$event'])
  onTouchEnd(event: TouchEvent): void {
    if (this.isDragging) {
      this.isDragging = false;
    }
  }

  private getAngleFromEvent(event: MouseEvent): number {
    if (!this.dialContainer) return 0;
    const rect = this.dialContainer.nativeElement.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;
    const x = event.clientX - centerX;
    const y = event.clientY - centerY;
    return Math.atan2(y, x) * (180 / Math.PI);
  }

  private getAngleFromTouch(event: TouchEvent): number {
    if (!this.dialContainer || !event.touches[0]) return 0;
    const rect = this.dialContainer.nativeElement.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;
    const x = event.touches[0].clientX - centerX;
    const y = event.touches[0].clientY - centerY;
    return Math.atan2(y, x) * (180 / Math.PI);
  }

  onWeightClick(): void {
    this.isEditing = true;
    setTimeout(() => {
      const input = document.querySelector('.weight-input input') as HTMLInputElement;
      if (input) {
        input.focus();
        input.select();
      }
    }, 0);
  }

  onWeightBlur(): void {
    this.isEditing = false;
    const weightValue = this.weightForm.get('weight')?.value;
    if (weightValue === null || weightValue === undefined || weightValue <= 0 || weightValue < this.minWeight) {

      this.currentWeight = Math.max(this.minWeight, this.data.currentWeight || 70);
      this.weightForm.patchValue({ weight: this.currentWeight }, { emitEvent: false });
      this.updateRotationFromWeight();
      if (weightValue !== null && weightValue !== undefined && weightValue <= 0) {
        this.snackBar.open('Weight must be greater than 0', 'Close', { duration: 3000 });
      }
    } else {
      this.currentWeight = Math.max(this.minWeight, weightValue);
      this.weightForm.patchValue({ weight: this.currentWeight }, { emitEvent: false });
      this.updateRotationFromWeight();
      this.updateVisibleWeights();
    }
    this.checkWeightWarning();
  }

  checkWeightWarning(): void {

  }

  get showLowWarning(): boolean {
    return this.currentWeight > 0 && this.currentWeight < 20;
  }

  get showHighWarning(): boolean {
    return this.currentWeight > 200;
  }

  getArcLength(): string {


    const normalizedRotation = (this.rotationAngle + 135) / 270;
    const arcLength = 251.33 * normalizedRotation;
    return `${arcLength}, 251.33`;
  }

  getArcOffset(): string {

    return '0';
  }

  onSubmit(): void {
    if (this.weightForm.invalid) {
      this.snackBar.open('Please enter a valid weight', 'Close', { duration: 3000 });
      return;
    }

    const weight = this.weightForm.get('weight')?.value;
    if (weight <= 0) {
      this.snackBar.open('Weight must be greater than 0', 'Close', { duration: 3000 });
      return;
    }

    this.dialogRef.close(weight);
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}

