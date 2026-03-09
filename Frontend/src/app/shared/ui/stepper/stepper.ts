import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
 selector: 'app-stepper',
 imports: [],
 templateUrl: './stepper.html',
 styleUrl: './stepper.css',
})
export class Stepper {
 @Input() value: number = 1;
 @Input() min: number = 1;
 @Input() max: number = 99;
 @Input() disabled: boolean = false;

 @Output() valueChange = new EventEmitter<number>();

 increment() {
 if (!this.disabled && this.value < this.max) {
 this.value++;
 this.valueChange.emit(this.value);
 }
 }

 decrement() {
 if (!this.disabled && this.value> this.min) {
 this.value--;
 this.valueChange.emit(this.value);
 }
 }
}
