import { Component, Input, computed } from '@angular/core';

@Component({
  selector: 'app-badge',
  imports: [],
  templateUrl: './badge.html',
  styleUrl: './badge.css',
})
export class Badge {
  @Input() variant: 'default' | 'outline' | 'dark' = 'default';

  variantClass = computed(() => {
    switch (this.variant) {
      case 'outline': return 'border-current text-current bg-transparent';
      case 'dark': return 'border-transparent bg-black text-white dark:bg-white dark:text-black';
      default: return 'border-transparent bg-gray-100 text-gray-800 dark:bg-gray-800 dark:text-gray-100';
    }
  });
}
