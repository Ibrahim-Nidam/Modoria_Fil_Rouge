import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { Button } from '../button/button';

@Component({
 selector: 'app-toast',
 standalone: true,
 imports: [Button],
 templateUrl: './toast.html',
 styleUrl: './toast.css',
})
export class Toast implements OnInit {
 @Input() visible: boolean = false;
 @Input() duration: number = 5000;
 @Output() visibleChange = new EventEmitter<boolean>();

 private timeoutId: any;

 ngOnInit() {
 if (this.visible && this.duration> 0) {
 this.startTimer();
 }
 }

 close() {
 this.visible = false;
 this.visibleChange.emit(this.visible);
 if (this.timeoutId) {
 clearTimeout(this.timeoutId);
 }
 }

 private startTimer() {
 this.timeoutId = setTimeout(() => {
 this.close();
 }, this.duration);
 }
}
