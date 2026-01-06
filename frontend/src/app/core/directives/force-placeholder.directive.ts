import { Directive, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';
@Directive({
  selector: '[appForcePlaceholder]'
})
export class ForcePlaceholderDirective implements AfterViewInit, OnDestroy {
  private observer?: MutationObserver;
  constructor(
    private el: ElementRef<HTMLInputElement>
  ) {}
  ngAfterViewInit(): void {
    try {
      setTimeout(() => {
        const input = this.el.nativeElement;
        if (input && input.placeholder && typeof input.closest === 'function') {
          const formField = input.closest('.mat-mdc-form-field');
          if (formField) {
            const forceShowPlaceholder = () => {
              try {
                if (formField && formField.classList) {
                  formField.classList.remove('mat-form-field-hide-placeholder');
                }
              } catch (e) {
              }
            };
            forceShowPlaceholder();
            try {
              if (typeof MutationObserver !== 'undefined') {
                this.observer = new MutationObserver(() => {
                  forceShowPlaceholder();
                });
                this.observer.observe(formField, {
                  attributes: true,
                  attributeFilter: ['class']
                });
              }
            } catch (e) {
            }
            try {
              input.addEventListener('blur', forceShowPlaceholder);
              input.addEventListener('focus', forceShowPlaceholder);
              input.addEventListener('input', forceShowPlaceholder);
            } catch (e) {
            }
          }
        }
      }, 100);
    } catch (e) {
      console.error('ForcePlaceholderDirective error:', e);
    }
  }
  ngOnDestroy(): void {
    if (this.observer) {
      this.observer.disconnect();
    }
  }
}