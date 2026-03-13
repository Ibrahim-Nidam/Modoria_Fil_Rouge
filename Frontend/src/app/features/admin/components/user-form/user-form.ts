import { CommonModule } from '@angular/common';
import { Component, effect, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Button } from '../../../../shared/ui/button/button';
import { Checkbox } from '../../../../shared/ui/checkbox/checkbox';
import { InputComponent } from '../../../../shared/ui/input/input';
import { AdminRole } from '../../services/admin-user.service';

export interface UserFormValue {
  fullName: string;
  email: string;
  password?: string;
  enabled: boolean;
  role: AdminRole;
}

@Component({
  selector: 'app-user-form',
  imports: [CommonModule, ReactiveFormsModule, InputComponent, Checkbox, Button],
  templateUrl: './user-form.html',
  styleUrl: './user-form.css',
})
export class UserForm {
  private fb = inject(FormBuilder);

  initialValue = input<UserFormValue | null>(null);
  mode = input<'create' | 'edit'>('create');
  submitting = input(false);

  submitted = output<UserFormValue>();
  cancelled = output<void>();

  readonly roleOptions: { value: AdminRole; label: string }[] = [
    { value: 'CLIENT', label: 'Client' },
    { value: 'AGENT', label: 'Agent' },
    { value: 'ADMIN', label: 'Admin' },
  ];

  form = this.fb.nonNullable.group({
    fullName: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.minLength(6)]],
    enabled: [true],
    role: ['CLIENT' as AdminRole, [Validators.required]],
  });

  constructor() {
    effect(() => {
      const mode = this.mode();
      const initialValue = this.initialValue();

      this.form.controls.password.setValidators(
        mode === 'create'
          ? [Validators.required, Validators.minLength(6)]
          : [Validators.minLength(6)]
      );
      this.form.controls.password.updateValueAndValidity({ emitEvent: false });

      this.form.reset({
        fullName: initialValue?.fullName ?? '',
        email: initialValue?.email ?? '',
        password: '',
        enabled: initialValue?.enabled ?? true,
        role: initialValue?.role ?? 'CLIENT',
      });
    });
  }

  get fullNameError(): string | undefined {
    const control = this.form.controls.fullName;
    if (!control.touched) {
      return undefined;
    }

    if (control.hasError('required')) {
      return 'Full name is required';
    }

    if (control.hasError('maxlength')) {
      return 'Full name must be 100 characters or fewer';
    }

    return undefined;
  }

  get emailError(): string | undefined {
    const control = this.form.controls.email;
    if (!control.touched) {
      return undefined;
    }

    if (control.hasError('required')) {
      return 'Email is required';
    }

    if (control.hasError('email')) {
      return 'Enter a valid email address';
    }

    return undefined;
  }

  get passwordError(): string | undefined {
    const control = this.form.controls.password;
    if (!control.touched) {
      return undefined;
    }

    if (control.hasError('required')) {
      return 'Password is required';
    }

    if (control.hasError('minlength')) {
      return 'Password must be at least 6 characters';
    }

    return undefined;
  }

  get submitLabel(): string {
    return this.mode() === 'create' ? 'Create User' : 'Save Changes';
  }

  onCancel() {
    this.cancelled.emit();
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    this.submitted.emit({
      fullName: raw.fullName,
      email: raw.email,
      password: raw.password || undefined,
      enabled: raw.enabled,
      role: raw.role,
    });
  }
}
