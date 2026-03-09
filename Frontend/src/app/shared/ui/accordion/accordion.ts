import { Component, Input } from '@angular/core';
import { Button } from '../button/button';

@Component({
 selector: 'app-accordion',
 standalone: true,
 imports: [Button],
 templateUrl: './accordion.html',
 styleUrl: './accordion.css',
})
export class Accordion {
 @Input() title: string = '';
 @Input() initiallyOpen: boolean = false;

 isOpen: boolean = false;

 ngOnInit() {
 this.isOpen = this.initiallyOpen;
 }

 toggle() {
 this.isOpen = !this.isOpen;
 }
}
