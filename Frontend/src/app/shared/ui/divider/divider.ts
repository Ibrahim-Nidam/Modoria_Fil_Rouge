import { Component, Input, computed } from '@angular/core';

@Component({
 selector: 'app-divider',
 imports: [],
 templateUrl: './divider.html',
 styleUrl: './divider.css',
})
export class Divider {
 @Input() margin: 'none' | 'sm' | 'md' | 'lg' = 'md';

 marginClass = computed(() => {
 switch (this.margin) {
 case 'none': return 'my-0';
 case 'sm': return 'my-4';
 case 'lg': return 'my-12';
 default: return 'my-8';
 }
 });
}
