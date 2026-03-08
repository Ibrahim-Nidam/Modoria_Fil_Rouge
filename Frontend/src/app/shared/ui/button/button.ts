import { Component, Input, computed } from '@angular/core';

export type ButtonVariant = 'primary' | 'secondary' | 'outline' | 'ghost' | 'link';
export type ButtonSize = 'sm' | 'md' | 'lg' | 'full' | 'icon' | 'none';

@Component({
  selector: 'app-button',
  imports: [],
  templateUrl: './button.html',
  styleUrl: './button.css',
})
export class Button {
  @Input() variant: ButtonVariant = 'primary';
  @Input() size: ButtonSize = 'md';
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Input() disabled = false;
  @Input() loading = false;
  @Input() customClass = '';

  computedClasses = computed(() => {
    let classes = 'group ';

    // Size
    switch (this.size) {
      case 'sm': classes += 'px-4 py-2 text-xs '; break;
      case 'lg': classes += 'px-8 py-4 text-sm '; break;
      case 'full': classes += 'w-full px-6 py-3.5 text-sm '; break;
      case 'icon': classes += 'p-2 '; break;
      case 'none': classes += 'p-0 '; break;
      default: classes += 'px-6 py-3 text-xs md:text-sm '; break;
    }

    // Variant
    switch (this.variant) {
      case 'primary':
        classes += 'bg-black text-white hover:bg-black/90 dark:bg-white dark:text-black dark:hover:bg-white/90 border border-transparent ';
        break;
      case 'secondary':
        classes += 'bg-gray-100 text-black hover:bg-gray-200 dark:bg-gray-900 dark:text-white dark:hover:bg-gray-800 border border-transparent ';
        break;
      case 'outline':
        classes += 'bg-transparent border border-black text-black hover:bg-black/5 dark:border-white dark:text-white dark:hover:bg-white/5 ';
        break;
      case 'ghost':
        classes += 'bg-transparent text-black hover:bg-black/5 dark:text-white dark:hover:bg-white/5 border border-transparent ';
        break;
      case 'link':
        classes += 'bg-transparent text-black dark:text-white hover:underline underline-offset-4 border-transparent p-0 ';
        break;
    }

    if (this.disabled || this.loading) {
      classes += 'opacity-50 cursor-not-allowed ';
    }

    if (this.customClass) {
      classes += this.customClass;
    }

    return classes;
  });
}
