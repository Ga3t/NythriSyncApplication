import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LatinOnlyInputDirective } from '../core/directives/latin-only-input.directive';
import { ForcePlaceholderDirective } from '../core/directives/force-placeholder.directive';
@NgModule({
  declarations: [
    LatinOnlyInputDirective,
    ForcePlaceholderDirective
  ],
  exports: [
    LatinOnlyInputDirective,
    ForcePlaceholderDirective
  ],
  imports: [
    CommonModule
  ]
})
export class SharedModule { }