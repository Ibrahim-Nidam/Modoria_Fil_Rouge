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
    selector: 'app-register',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        InputComponent,
        AuthHeader,
        FormComponent,
        Button
    ],
    templateUrl: './register.html',
    styleUrl: './register.css'
})
export class RegisterComponent {
    private fb = inject(FormBuilder);
    private router = inject(Router);
    private authService = inject(AuthService);
    private toastService = inject(ToastService);
    public themeService = inject(ThemeService);

    public registerForm = this.fb.group({
        fullName: ['', [Validators.required]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', [Validators.required]]
    });

    public get activeSeason(): Season {
        return this.themeService.activeSeason();
    }

    public get isDark(): boolean {
        return this.themeService.isDarkMode();
    }

    public get isFormEmpty(): boolean {
        return !this.registerForm.get('fullName')?.value &&
            !this.registerForm.get('email')?.value &&
            !this.registerForm.get('password')?.value &&
            !this.registerForm.get('confirmPassword')?.value;
    }

    onSubmit() {
        if (this.registerForm.valid) {
            this.authService.register(this.registerForm.value).subscribe({
                next: () => {
                    this.toastService.success('Your account has been created.', 'Maison Joined');
                },
                error: (err) => {
                    const message = err.error?.message || 'Registration failed. Please check your details.';
                    this.toastService.error(message, 'Registration Error');
                }
            });
        } else {
            this.registerForm.markAllAsTouched();
            this.toastService.warning('Please refine your registration details.', 'Incomplete Form');
        }
    }

    navigateToLogin() {
        this.router.navigate(['/auth/login']);
    }
}
