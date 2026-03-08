import { Component, Input, Output, EventEmitter } from '@angular/core';

export interface SelectOption {
  label: string;
  value: string | number;
}

@Component({
  selector: 'app-select',
  standalone: true,
  imports: [],
  templateUrl: './select.html',
  styleUrl: './select.css',
})
export class Select {
  @Input() id: string = `select-${Math.random().toString(36).substring(2, 9)}`;
  @Input() label?: string;
  @Input() placeholder?: string;
  @Input() options: SelectOption[] = [];
  @Input() value: string | number | null = null;
  @Input() error?: string;
  @Input() disabled: boolean = false;

  @Output() valueChange = new EventEmitter<string | number>();

  onChange(event: Event) {
    const target = event.target as HTMLSelectElement;
    this.value = target.value;
    this.valueChange.emit(this.value);
  }
}
