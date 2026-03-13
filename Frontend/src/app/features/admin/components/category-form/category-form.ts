import { CommonModule } from '@angular/common';
import { Component, OnDestroy, effect, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Button } from '../../../../shared/ui/button/button';
import { InputComponent } from '../../../../shared/ui/input/input';

export interface CategoryFormInitialValue {
  name: string;
  description: string;
  imagePath?: string | null;
}

export interface CategoryFormValue {
  name: string;
  description: string;
  imageFile: File | null;
}

@Component({
  selector: 'app-category-form',
  imports: [CommonModule, ReactiveFormsModule, InputComponent, Button],
  templateUrl: './category-form.html',
  styleUrl: './category-form.css',
})
export class CategoryForm implements OnDestroy {
  private fb = inject(FormBuilder);
  private backendBaseUrl = 'http://localhost:8081';

  initialValue = input<CategoryFormInitialValue | null>(null);
  mode = input<'create' | 'edit'>('create');
  submitting = input(false);

  submitted = output<CategoryFormValue>();
  cancelled = output<void>();

  selectedImageFile: File | null = null;
  imagePreviewUrl: string | null = null;
  imagePreviewObjectUrl: string | null = null;

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
      this.resetSelectedImage();
      this.imagePreviewUrl = this.resolveImageUrl(initialValue?.imagePath ?? null);
    });
  }

  ngOnDestroy(): void {
    this.revokeObjectUrl();
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

  onImageSelection(event: Event) {
    const target = event.target as HTMLInputElement;
    const selectedFile = target.files?.[0] ?? null;

    this.resetSelectedImage();

    if (selectedFile && selectedFile.type.startsWith('image/')) {
      this.selectedImageFile = selectedFile;
      this.imagePreviewObjectUrl = URL.createObjectURL(selectedFile);
      this.imagePreviewUrl = this.imagePreviewObjectUrl;
    }

    target.value = '';
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
      imageFile: this.selectedImageFile,
    });
  }

  private resetSelectedImage() {
    this.selectedImageFile = null;
    this.revokeObjectUrl();
  }

  private revokeObjectUrl() {
    if (this.imagePreviewObjectUrl) {
      URL.revokeObjectURL(this.imagePreviewObjectUrl);
      this.imagePreviewObjectUrl = null;
    }
  }

  private resolveImageUrl(imagePath: string | null): string | null {
    if (!imagePath) {
      return null;
    }

    if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
      return imagePath;
    }

    return `${this.backendBaseUrl}${imagePath.startsWith('/') ? imagePath : `/${imagePath}`}`;
  }
}
