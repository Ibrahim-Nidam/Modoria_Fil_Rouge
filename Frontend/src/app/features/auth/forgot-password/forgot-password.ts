import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/toast/toast.service';
import { Button } from '../../../shared/ui/button/button';
import { InputComponent } from '../../../shared/ui/input/input';
import { FormComponent } from '../../../shared/ui/form/form';
import { AuthHeader } from '../../../shared/layout/auth-header/auth-header';
import { ThemeService } from '../../../core/theme/theme.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    Button,
    InputComponent,
    FormComponent,
    AuthHeader
  ],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);
  public themeService = inject(ThemeService);
  private cdr = inject(ChangeDetectorRef);

  forgotPasswordForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });

  isLoading = false;

  get activeSeason(): string {
    return this.themeService.activeSeason();
  }

  onSubmit() {
    if (this.forgotPasswordForm.invalid) {
      this.forgotPasswordForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    const email = this.forgotPasswordForm.value.email;

    this.authService.forgotPassword(email).subscribe({
      next: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
        this.toastService.success('Email Sent', 'If an account exists, a reset link has been dispatched.');
        this.forgotPasswordForm.reset();
      },
      error: (err: any) => {
        this.isLoading = false;
        this.cdr.detectChanges();
        // Avoid email enumeration by showing success even on error unless network fails.
        this.toastService.success('Email Sent', 'If an account exists, a reset link has been dispatched.');
        console.error('Password reset request error:', err);
      }
    });
  }

  getFieldError(fieldName: string): string {
    const control = this.forgotPasswordForm.get(fieldName);
    if (control && control.touched && control.errors) {
      if (control.errors['required']) return `${fieldName} is required`;
      if (control.errors['email']) return 'Invalid email address';
    }
    return '';
  }
}
