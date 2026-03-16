import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../core/toast/toast.service';
import { Toast } from '../../ui/toast/toast';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule, Toast],
  templateUrl: './toast-container.html',
  styleUrl: './toast-container.css',
})
export class ToastContainer {
  public toastService = inject(ToastService);
}
