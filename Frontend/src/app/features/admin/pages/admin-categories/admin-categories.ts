import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { finalize, of, switchMap } from 'rxjs';
import { ToastService } from '../../../../core/toast/toast.service';
import { Button } from '../../../../shared/ui/button/button';
import { Modal } from '../../../../shared/ui/modal/modal';
import { CategoryForm, CategoryFormValue } from '../../components/category-form/category-form';
import { AdminCategory, AdminCategoryService } from '../../services/admin-category.service';

@Component({
  selector: 'app-admin-categories',
  imports: [CommonModule, Button, Modal, CategoryForm],
  templateUrl: './admin-categories.html',
  styleUrl: './admin-categories.css',
})
export class AdminCategories implements OnInit {
  private categoryService = inject(AdminCategoryService);
  private toastService = inject(ToastService);
  private backendBaseUrl = 'http://localhost:8081';

  categories = signal<AdminCategory[]>([]);
  loading = signal(true);
  formOpen = signal(false);
  deleteOpen = signal(false);
  submitting = signal(false);
  deleting = signal(false);
  selectedCategory = signal<AdminCategory | null>(null);

  ngOnInit() {
    this.loadCategories();
  }

  get formTitle(): string {
    return this.selectedCategory() ? 'Edit Category' : 'Create Category';
  }

  openCreateModal() {
    this.selectedCategory.set(null);
    this.formOpen.set(true);
  }

  openEditModal(category: AdminCategory) {
    this.selectedCategory.set(category);
    this.formOpen.set(true);
  }

  openDeleteModal(category: AdminCategory) {
    this.selectedCategory.set(category);
    this.deleteOpen.set(true);
  }

  closeFormModal() {
    this.formOpen.set(false);
    this.selectedCategory.set(null);
  }

  closeDeleteModal() {
    this.deleteOpen.set(false);
    this.selectedCategory.set(null);
  }

  onFormModalChange(isOpen: boolean) {
    if (!isOpen) {
      this.closeFormModal();
    }
  }

  onDeleteModalChange(isOpen: boolean) {
    if (!isOpen) {
      this.closeDeleteModal();
    }
  }

  saveCategory(payload: CategoryFormValue) {
    const selectedCategory = this.selectedCategory();
    const request$ = selectedCategory
      ? this.categoryService.updateCategory(selectedCategory.id, payload)
      : this.categoryService.createCategory(payload);

    this.submitting.set(true);

    request$
      .pipe(
        switchMap((savedCategory) => {
          if (!payload.imageFile) {
            return of(savedCategory);
          }

          return this.categoryService.uploadCategoryImage(savedCategory.id, payload.imageFile);
        }),
        finalize(() => this.submitting.set(false))
      )
      .subscribe({
        next: () => {
          this.toastService.success(
            selectedCategory ? 'Category updated successfully.' : 'Category created successfully.',
            'Categories'
          );
          this.closeFormModal();
          this.loadCategories();
        },
        error: (error) => {
          this.toastService.error(
            error.error?.message ?? 'Unable to save the category.',
            'Categories'
          );
        },
      });
  }

  getImageUrl(imagePath: string | null | undefined): string | null {
    if (!imagePath) {
      return null;
    }

    if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
      return imagePath;
    }

    return `${this.backendBaseUrl}${imagePath.startsWith('/') ? imagePath : `/${imagePath}`}`;
  }

  deleteCategory() {
    const selectedCategory = this.selectedCategory();
    if (!selectedCategory) {
      return;
    }

    this.deleting.set(true);

    this.categoryService
      .deleteCategory(selectedCategory.id)
      .pipe(finalize(() => this.deleting.set(false)))
      .subscribe({
        next: () => {
          this.toastService.success('Category deleted successfully.', 'Categories');
          this.closeDeleteModal();
          this.loadCategories();
        },
        error: (error) => {
          this.toastService.error(
            error.error?.message ?? 'Unable to delete the category.',
            'Categories'
          );
        },
      });
  }

  private loadCategories() {
    this.loading.set(true);

    this.categoryService
      .getCategories()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          this.categories.set(response.content);
        },
        error: (error) => {
          this.toastService.error(
            error.error?.message ?? 'Unable to load categories.',
            'Categories'
          );
        },
      });
  }
}
