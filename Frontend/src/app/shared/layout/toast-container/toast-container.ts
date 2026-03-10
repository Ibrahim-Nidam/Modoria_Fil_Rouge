import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../core/toast/toast.service';
import { Toast } from '../../ui/toast/toast';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule, Toast],
  template: `
    <div class="fixed top-24 right-8 z-[100] flex flex-col gap-4 pointer-events-none">
      @for (toast of toastService.toastsSignal(); track toast.id) {
        <app-toast 
          [visible]="true" 
          (visibleChange)="!$event && toastService.remove(toast.id)"
          class="pointer-events-auto">
          <div class="flex flex-col gap-1 pr-8">
            <div class="flex items-center gap-2">
                @if (toast.type === 'error') {
                    <span class="material-symbols-outlined text-red-500 text-lg">error</span>
                } @else if (toast.type === 'success') {
                    <span class="material-symbols-outlined text-green-500 text-lg">check_circle</span>
                } @else {
                    <span class="material-symbols-outlined text-primary text-lg">info</span>
                }
                @if (toast.title) {
                  <span class="font-black text-[10px] uppercase tracking-[0.2em] text-background/50">{{ toast.title }}</span>
                }
            </div>
            <span class="text-xs font-medium tracking-wide text-background/90">{{ toast.message }}</span>
          </div>
        </app-toast>
      }
    </div>
  `,
  styles: [`
    :host { display: block; }
  `]
})
export class ToastContainer {
  public toastService = inject(ToastService);
}
