import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
 selector: 'app-radio',
 imports: [],
 templateUrl: './radio.html',
 styleUrl: './radio.css',
})
export class Radio {
 @Input() label?: string;
 @Input() value: any;
 @Input() name: string = '';
 @Input() checked: boolean = false;
 @Input() disabled: boolean = false;

 @Output() valueChange = new EventEmitter<any>();

 onChange(event: Event) {
 const target = event.target as HTMLInputElement;
 if (target.checked) {
 this.checked = true;
 this.valueChange.emit(this.value);
 }
 }
}
