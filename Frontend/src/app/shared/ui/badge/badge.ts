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
 case 'dark': return 'border-transparent bg-foreground text-background';
 default: return 'border-transparent bg-muted text-foreground';
 }
 });
}
