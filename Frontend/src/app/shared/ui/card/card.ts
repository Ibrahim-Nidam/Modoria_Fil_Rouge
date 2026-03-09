import { Component, Input, computed } from '@angular/core';

@Component({
 selector: 'app-card',
 standalone: true,
 imports: [],
 templateUrl: './card.html',
 styleUrl: './card.css',
})
export class Card {
 @Input() padding: 'none' | 'sm' | 'md' = 'none';

 paddingClass = computed(() => {
 switch (this.padding) {
 case 'sm': return 'p-4';
 case 'md': return 'p-8';
 default: return 'p-0';
 }
 });
}
