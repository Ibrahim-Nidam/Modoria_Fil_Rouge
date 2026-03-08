import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-tooltip',
  imports: [],
  templateUrl: './tooltip.html',
  styleUrl: './tooltip.css',
})
export class Tooltip {
  @Input({ required: true }) text!: string;
  @Input() position: 'top' | 'bottom' | 'left' | 'right' = 'top';
}
