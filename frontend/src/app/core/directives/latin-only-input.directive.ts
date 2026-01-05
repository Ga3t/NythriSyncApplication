import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[appLatinOnly]'
})
export class LatinOnlyInputDirective {
  private latinPattern = /^[a-zA-Z0-9\s.,!?@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/]*$/;

  constructor(private el: ElementRef<HTMLInputElement>) {}

  @HostListener('input', ['$event'])
  onInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = input.value;

    const latinValue = value.split('').filter(char => 
      this.latinPattern.test(char)
    ).join('');

    if (value !== latinValue) {
      input.value = latinValue;
      input.dispatchEvent(new Event('input', { bubbles: true }));
    }
  }

  @HostListener('keypress', ['$event'])
  onKeyPress(event: KeyboardEvent): void {
    const char = String.fromCharCode(event.which || event.keyCode);
    
    if (event.ctrlKey || event.metaKey || event.altKey || event.key === 'Backspace' || event.key === 'Delete' || event.key === 'Tab' || event.key === 'ArrowLeft' || event.key === 'ArrowRight') {
      return;
    }

    if (!this.latinPattern.test(char)) {
      event.preventDefault();
    }
  }

  @HostListener('paste', ['$event'])
  onPaste(event: ClipboardEvent): void {
    event.preventDefault();
    const paste = (event.clipboardData || (window as any).clipboardData).getData('text');
    
    const latinPaste = paste.split('').filter((char: string) => 
      this.latinPattern.test(char)
    ).join('');

    const input = this.el.nativeElement;
    const start = input.selectionStart || 0;
    const end = input.selectionEnd || 0;
    const currentValue = input.value;
    
    input.value = currentValue.substring(0, start) + latinPaste + currentValue.substring(end);
    input.setSelectionRange(start + latinPaste.length, start + latinPaste.length);
    
    input.dispatchEvent(new Event('input', { bubbles: true }));
  }
}

