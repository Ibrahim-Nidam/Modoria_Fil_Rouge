import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ThemeService, Season } from '../../../core/theme/theme.service';
import { AuthHeader } from '../../../shared/layout/auth-header/auth-header';
import { FormComponent } from '../../../shared/ui/form/form';
import { Button } from '../../../shared/ui/button/button';
import { InputComponent } from '../../../shared/ui/input/input';
import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/toast/toast.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        InputComponent,
        AuthHeader,
        FormComponent,
        Button
    ],
    templateUrl: './login.html',
    styleUrl: './login.css'
})
export class LoginComponent {
    private fb = inject(FormBuilder);
    private router = inject(Router);
    private authService = inject(AuthService);
    private toastService = inject(ToastService);
    public themeService = inject(ThemeService);

    public loginForm = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        rememberMe: [false]
    });

    public get activeSeason(): Season {
        return this.themeService.activeSeason();
    }

    public get isDark(): boolean {
        return this.themeService.isDarkMode();
    }

    public get isFormEmpty(): boolean {
        return !this.loginForm.get('email')?.value && !this.loginForm.get('password')?.value;
    }

    onSubmit() {
        if (this.loginForm.valid) {
            this.authService.login(this.loginForm.value).subscribe({
                next: () => {
                    this.toastService.success('Welcome back to Modoria.', 'Authentication Success');
                },
                error: (err) => {
                    const message = err.error?.message || 'Invalid credentials. Please try again.';
                    this.toastService.error(message, 'Login Failed');
                }
            });
        } else {
            this.loginForm.markAllAsTouched();
            this.toastService.warning('Please complete the required identity fields.', 'Registration Incomplete');
        }
    }

    navigateToRegister() {
        this.router.navigate(['/auth/register']);
    }
}
