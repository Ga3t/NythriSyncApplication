import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function latinOnlyValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) {
      return null;
    }

    const latinPattern = /^[a-zA-Z0-9\s.,!?@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/]*$/;
    
    if (!latinPattern.test(control.value)) {
      return { latinOnly: { value: control.value } };
    }

    return null;
  };
}










