import { Component, Input, computed } from '@angular/core';

@Component({
 selector: 'app-spinner',
 imports: [],
 templateUrl: './spinner.html',
 styleUrl: './spinner.css',
})
export class Spinner {
 @Input() size: 'sm' | 'md' | 'lg' = 'md';

 sizeClass = computed(() => {
 switch (this.size) {
 case 'sm': return 'h-4 w-4 border-2';
 case 'lg': return 'h-12 w-12 border-4';
 default: return 'h-8 w-8 border-2';
 }
 });
}
