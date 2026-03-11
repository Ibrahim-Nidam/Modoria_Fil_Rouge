import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/toast/toast.service';
import { Button } from '../../../shared/ui/button/button';
import { InputComponent } from '../../../shared/ui/input/input';
import { FormComponent } from '../../../shared/ui/form/form';
import { AuthHeader } from '../../../shared/layout/auth-header/auth-header';
import { ThemeService } from '../../../core/theme/theme.service';

@Component({
  selector: 'app-reset-password',
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
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  public themeService = inject(ThemeService);

  resetPasswordForm: FormGroup = this.fb.group({
    newPassword: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', [Validators.required]]
  }, { validators: this.passwordMatchValidator });

  token: string = '';
  isLoading = false;

  ngOnInit() {
    this.token = this.route.snapshot.queryParams['token'];
    if (!this.token) {
      this.toastService.error('Invalid Request', 'No secure reset token found in the URL.');
      this.router.navigate(['/auth/login']);
    }
  }

  get activeSeason(): string {
    return this.themeService.activeSeason();
  }

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('newPassword');
    const confirmPassword = control.get('confirmPassword');
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit() {
    if (this.resetPasswordForm.invalid) {
      this.resetPasswordForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    const newPassword = this.resetPasswordForm.value.newPassword;

    this.authService.resetPassword(this.token, newPassword).subscribe({
      next: () => {
        this.isLoading = false;
        this.toastService.success('Success', 'Your password has been reset successfully.');
        this.router.navigate(['/auth/login']);
      },
      error: (err: any) => {
        this.isLoading = false;
        this.toastService.error('Reset Failed', 'The token may be expired or invalid. Please request a new one.');
        console.error('Password reset error:', err);
      }
    });
  }

  getFieldError(fieldName: string): string {
    const control = this.resetPasswordForm.get(fieldName);
    if (control && control.touched && control.errors) {
      if (control.errors['required']) return `${fieldName} is required`;
      if (control.errors['minlength']) return 'Min length 6 characters';
    }
    if (fieldName === 'confirmPassword' && this.resetPasswordForm.errors?.['passwordMismatch'] && control?.touched) {
      return 'Passwords do not match';
    }
    return '';
  }
}
