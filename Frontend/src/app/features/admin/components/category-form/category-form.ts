import { CommonModule } from '@angular/common';
import { Component, effect, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Button } from '../../../../shared/ui/button/button';
import { InputComponent } from '../../../../shared/ui/input/input';

export interface CategoryFormValue {
  name: string;
  description: string;
}

@Component({
  selector: 'app-category-form',
  imports: [CommonModule, ReactiveFormsModule, InputComponent, Button],
  templateUrl: './category-form.html',
  styleUrl: './category-form.css',
})
export class CategoryForm {
  private fb = inject(FormBuilder);

  initialValue = input<CategoryFormValue | null>(null);
  mode = input<'create' | 'edit'>('create');
  submitting = input(false);

  submitted = output<CategoryFormValue>();
  cancelled = output<void>();

  form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    description: ['', [Validators.maxLength(500)]],
  });

  constructor() {
    effect(() => {
      const initialValue = this.initialValue();
      this.form.reset({
        name: initialValue?.name ?? '',
        description: initialValue?.description ?? '',
      });
    });
  }

  get nameError(): string | undefined {
    const control = this.form.controls.name;
    if (!control.touched) {
      return undefined;
    }

    if (control.hasError('required')) {
      return 'Category name is required';
    }

    if (control.hasError('maxlength')) {
      return 'Category name must be 100 characters or fewer';
    }

    return undefined;
  }

  get descriptionError(): string | undefined {
    const control = this.form.controls.description;
    if (!control.touched || !control.hasError('maxlength')) {
      return undefined;
    }

    return 'Description must be 500 characters or fewer';
  }

  get submitLabel(): string {
    return this.mode() === 'create' ? 'Create Category' : 'Save Changes';
  }

  onCancel() {
    this.cancelled.emit();
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitted.emit({
      name: this.form.controls.name.value.trim(),
      description: this.form.controls.description.value.trim(),
    });
  }
}
