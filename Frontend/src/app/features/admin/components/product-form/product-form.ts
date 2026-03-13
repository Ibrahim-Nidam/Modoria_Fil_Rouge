import { CommonModule } from '@angular/common';
import { Component, OnDestroy, effect, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Button } from '../../../../shared/ui/button/button';
import { InputComponent } from '../../../../shared/ui/input/input';
import { AdminCategory } from '../../services/admin-category.service';
import { AdminProductImage, ProductSeason } from '../../services/admin-product.service';

interface ExistingImageItem extends AdminProductImage {
  markedForRemoval: boolean;
}

interface NewImageItem {
  file: File;
  previewUrl: string;
  primary: boolean;
}

export interface ProductFormInitialValue {
  name: string;
  description: string;
  price: number;
  stock: number;
  season: ProductSeason | null;
  categoryId: number;
  images: AdminProductImage[];
}

export interface ProductFormValue {
  name: string;
  description: string;
  price: number;
  stock: number;
  season: ProductSeason | null;
  categoryId: number;
  existingImageIdsToRemove: number[];
  newImages: File[];
  primaryExistingImageId: number | null;
  primaryNewImageIndex: number | null;
}

@Component({
  selector: 'app-product-form',
  imports: [CommonModule, ReactiveFormsModule, InputComponent, Button],
  templateUrl: './product-form.html',
  styleUrl: './product-form.css',
})
export class ProductForm implements OnDestroy {
  private fb = inject(FormBuilder);

  initialValue = input<ProductFormInitialValue | null>(null);
  categories = input<AdminCategory[]>([]);
  mode = input<'create' | 'edit'>('create');
  submitting = input(false);

  submitted = output<ProductFormValue>();
  cancelled = output<void>();

  readonly seasonOptions: ProductSeason[] = ['SPRING', 'SUMMER', 'AUTUMN', 'WINTER'];
  private readonly backendBaseUrl = 'http://localhost:8081';

  existingImages: ExistingImageItem[] = [];
  newImages: NewImageItem[] = [];
  imageError?: string;

  form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(120)]],
    description: ['', [Validators.maxLength(1000)]],
    price: ['', [Validators.required, Validators.min(0)]],
    stock: ['', [Validators.required, Validators.min(0), Validators.pattern('^[0-9]+$')]],
    season: [''],
    categoryId: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
  });

  constructor() {
    effect(() => {
      const initialValue = this.initialValue();
      this.form.reset({
        name: initialValue?.name ?? '',
        description: initialValue?.description ?? '',
        price: initialValue?.price?.toString() ?? '',
        stock: initialValue?.stock?.toString() ?? '',
        season: initialValue?.season ?? '',
        categoryId: initialValue?.categoryId?.toString() ?? '',
      });

      this.resetImageState(initialValue?.images ?? []);
    });
  }

  ngOnDestroy(): void {
    this.cleanupNewImagePreviews();
  }

  get nameError(): string | undefined {
    const control = this.form.controls.name;
    if (!control.touched) {
      return undefined;
    }

    if (control.hasError('required')) {
      return 'Product name is required';
    }

    if (control.hasError('maxlength')) {
      return 'Product name must be 120 characters or fewer';
    }

    return undefined;
  }

  get descriptionError(): string | undefined {
    const control = this.form.controls.description;
    if (!control.touched || !control.hasError('maxlength')) {
      return undefined;
    }

    return 'Description must be 1000 characters or fewer';
  }

  get priceError(): string | undefined {
    const control = this.form.controls.price;
    if (!control.touched) {
      return undefined;
    }

    if (control.hasError('required')) {
      return 'Price is required';
    }

    if (control.hasError('min')) {
      return 'Price cannot be negative';
    }

    return undefined;
  }

  get stockError(): string | undefined {
    const control = this.form.controls.stock;
    if (!control.touched) {
      return undefined;
    }

    if (control.hasError('required')) {
      return 'Stock is required';
    }

    if (control.hasError('min')) {
      return 'Stock cannot be negative';
    }

    if (control.hasError('pattern')) {
      return 'Stock must be a whole number';
    }

    return undefined;
  }

  get categoryError(): string | undefined {
    const control = this.form.controls.categoryId;
    if (!control.touched) {
      return undefined;
    }

    if (control.hasError('required')) {
      return 'Category is required';
    }

    if (control.hasError('pattern')) {
      return 'Invalid category';
    }

    return undefined;
  }

  get submitLabel(): string {
    return this.mode() === 'create' ? 'Create Product' : 'Save Changes';
  }

  get activeExistingImages(): ExistingImageItem[] {
    return this.existingImages.filter(image => !image.markedForRemoval);
  }

  get activeImageCount(): number {
    return this.activeExistingImages.length + this.newImages.length;
  }

  resolveImageUrl(imagePath: string): string {
    if (!imagePath) {
      return '';
    }

    if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
      return imagePath;
    }

    return `${this.backendBaseUrl}${imagePath.startsWith('/') ? imagePath : `/${imagePath}`}`;
  }

  onFileSelection(event: Event) {
    const target = event.target as HTMLInputElement;
    const fileList = target.files;

    if (!fileList || fileList.length === 0) {
      return;
    }

    for (const file of Array.from(fileList)) {
      if (!file.type.startsWith('image/')) {
        continue;
      }

      this.newImages.push({
        file,
        previewUrl: URL.createObjectURL(file),
        primary: false,
      });
    }

    target.value = '';
    this.ensurePrimarySelection();
    this.imageError = undefined;
  }

  markExistingForRemoval(imageId: number, marked: boolean) {
    const image = this.existingImages.find(item => item.id === imageId);
    if (!image) {
      return;
    }

    image.markedForRemoval = marked;
    if (marked && image.primary) {
      image.primary = false;
      this.ensurePrimarySelection();
    }
  }

  removeNewImage(index: number) {
    const image = this.newImages[index];
    if (!image) {
      return;
    }

    URL.revokeObjectURL(image.previewUrl);
    const wasPrimary = image.primary;
    this.newImages.splice(index, 1);

    if (wasPrimary) {
      this.ensurePrimarySelection();
    }
  }

  setPrimaryExistingImage(imageId: number) {
    this.clearPrimarySelection();

    const image = this.existingImages.find(item => item.id === imageId && !item.markedForRemoval);
    if (image) {
      image.primary = true;
      this.imageError = undefined;
    }
  }

  setPrimaryNewImage(index: number) {
    this.clearPrimarySelection();

    const image = this.newImages[index];
    if (image) {
      image.primary = true;
      this.imageError = undefined;
    }
  }

  onCancel() {
    this.cancelled.emit();
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (this.mode() === 'create' && this.activeImageCount === 0) {
      this.imageError = 'At least one image is required when creating a product';
      return;
    }

    this.ensurePrimarySelection();

    const primaryExistingImage = this.activeExistingImages.find(image => image.primary);
    const primaryNewImageIndex = this.newImages.findIndex(image => image.primary);

    const price = Number(this.form.controls.price.value);
    const stock = Number(this.form.controls.stock.value);
    const categoryId = Number(this.form.controls.categoryId.value);

    if (!Number.isFinite(price) || !Number.isFinite(stock) || !Number.isFinite(categoryId)) {
      this.form.markAllAsTouched();
      return;
    }

    const season = this.form.controls.season.value
      ? (this.form.controls.season.value as ProductSeason)
      : null;

    this.submitted.emit({
      name: this.form.controls.name.value.trim(),
      description: this.form.controls.description.value.trim(),
      price,
      stock,
      season,
      categoryId,
      existingImageIdsToRemove: this.existingImages
        .filter(image => image.markedForRemoval)
        .map(image => image.id),
      newImages: this.newImages.map(image => image.file),
      primaryExistingImageId: primaryExistingImage ? primaryExistingImage.id : null,
      primaryNewImageIndex: primaryNewImageIndex >= 0 ? primaryNewImageIndex : null,
    });
  }

  private resetImageState(images: AdminProductImage[]) {
    this.cleanupNewImagePreviews();

    this.existingImages = images
      .map(image => ({ ...image, markedForRemoval: false }))
      .sort((a, b) => Number(b.primary) - Number(a.primary) || a.id - b.id);
    this.newImages = [];
    this.imageError = undefined;

    this.ensurePrimarySelection();
  }

  private ensurePrimarySelection() {
    const hasPrimaryExisting = this.activeExistingImages.some(image => image.primary);
    const hasPrimaryNew = this.newImages.some(image => image.primary);

    if (hasPrimaryExisting || hasPrimaryNew || this.activeImageCount === 0) {
      return;
    }

    const firstExisting = this.activeExistingImages[0];
    if (firstExisting) {
      firstExisting.primary = true;
      return;
    }

    if (this.newImages.length > 0) {
      this.newImages[0].primary = true;
    }
  }

  private clearPrimarySelection() {
    for (const image of this.existingImages) {
      image.primary = false;
    }

    for (const image of this.newImages) {
      image.primary = false;
    }
  }

  private cleanupNewImagePreviews() {
    for (const image of this.newImages) {
      URL.revokeObjectURL(image.previewUrl);
    }
  }
}