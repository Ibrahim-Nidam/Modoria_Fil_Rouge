import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Observable, concatMap, finalize, from, map, of, switchMap, toArray } from 'rxjs';
import { ToastService } from '../../../../core/toast/toast.service';
import { Button } from '../../../../shared/ui/button/button';
import { Modal } from '../../../../shared/ui/modal/modal';
import { ProductForm, ProductFormInitialValue, ProductFormValue } from '../../components/product-form/product-form';
import { AdminCategory, AdminCategoryService } from '../../services/admin-category.service';
import { AdminProduct, AdminProductService, ProductPayload } from '../../services/admin-product.service';

@Component({
  selector: 'app-admin-products',
  imports: [CommonModule, Button, Modal, ProductForm],
  templateUrl: './admin-products.html',
  styleUrl: './admin-products.css',
})
export class AdminProducts implements OnInit {
  private productService = inject(AdminProductService);
  private categoryService = inject(AdminCategoryService);
  private toastService = inject(ToastService);
  private readonly backendBaseUrl = 'http://localhost:8081';

  products = signal<AdminProduct[]>([]);
  categories = signal<AdminCategory[]>([]);
  loading = signal(true);
  formOpen = signal(false);
  deleteOpen = signal(false);
  submitting = signal(false);
  deleting = signal(false);
  selectedProduct = signal<AdminProduct | null>(null);

  selectedProductAsFormValue = computed((): ProductFormInitialValue | null => {
    const product = this.selectedProduct();
    if (!product) return null;

    return {
      name: product.name,
      description: product.description ?? '',
      price: product.price,
      stock: product.stock,
      season: product.season,
      categoryId: product.category.id,
      images: product.images ?? [],
    };
  });

  ngOnInit() {
    this.loadCategories();
    this.loadProducts();
  }

  get formTitle(): string {
    return this.selectedProduct() ? 'Edit Product' : 'Create Product';
  }

  openCreateModal() {
    if (this.categories().length === 0) {
      this.toastService.warning('Create a category first before adding products.', 'Products');
      return;
    }

    this.selectedProduct.set(null);
    this.formOpen.set(true);
  }

  openEditModal(product: AdminProduct) {
    this.productService.getProductById(product.id).subscribe({
      next: (fullProduct) => {
        this.selectedProduct.set(fullProduct);
        this.formOpen.set(true);
      },
      error: (error) => {
        this.toastService.error(
          error.error?.message ?? 'Unable to load product details.',
          'Products'
        );
      },
    });
  }

  openDeleteModal(product: AdminProduct) {
    this.selectedProduct.set(product);
    this.deleteOpen.set(true);
  }

  closeFormModal() {
    this.formOpen.set(false);
    this.selectedProduct.set(null);
  }

  closeDeleteModal() {
    this.deleteOpen.set(false);
    this.selectedProduct.set(null);
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

  saveProduct(payload: ProductFormValue) {
    const selectedProduct = this.selectedProduct();
    const apiPayload: ProductPayload = {
      name: payload.name,
      description: payload.description,
      price: payload.price,
      stock: payload.stock,
      season: payload.season,
      categoryId: payload.categoryId,
    };

    const request$ = selectedProduct
      ? this.productService.updateProduct(selectedProduct.id, apiPayload)
      : this.productService.createProduct(apiPayload);

    this.submitting.set(true);

    request$
      .pipe(
        switchMap((savedProduct) => this.persistImages(savedProduct.id, payload)),
        switchMap((productId) => this.productService.getProductById(productId)),
        map(() => void 0),
        finalize(() => this.submitting.set(false))
      )
      .subscribe({
        next: () => {
          this.toastService.success(
            selectedProduct ? 'Product updated successfully.' : 'Product created successfully.',
            'Products'
          );
          this.closeFormModal();
          this.loadProducts();
        },
        error: (error) => {
          this.toastService.error(
            error.error?.message ?? 'Unable to save the product.',
            'Products'
          );
        },
      });
  }

  getPrimaryImageUrl(product: AdminProduct): string | null {
    const imagePath = product.primaryImagePath || product.images?.find(image => image.primary)?.imagePath;
    if (!imagePath) {
      return null;
    }

    if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
      return imagePath;
    }

    return `${this.backendBaseUrl}${imagePath.startsWith('/') ? imagePath : `/${imagePath}`}`;
  }

  deleteProduct() {
    const selectedProduct = this.selectedProduct();
    if (!selectedProduct) {
      return;
    }

    this.deleting.set(true);

    this.productService
      .deleteProduct(selectedProduct.id)
      .pipe(finalize(() => this.deleting.set(false)))
      .subscribe({
        next: () => {
          this.toastService.success('Product deleted successfully.', 'Products');
          this.closeDeleteModal();
          this.loadProducts();
        },
        error: (error) => {
          this.toastService.error(
            error.error?.message ?? 'Unable to delete the product.',
            'Products'
          );
        },
      });
  }

  private loadProducts() {
    this.loading.set(true);

    this.productService
      .getProducts()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          this.products.set(response.content);
        },
        error: (error) => {
          this.toastService.error(
            error.error?.message ?? 'Unable to load products.',
            'Products'
          );
        },
      });
  }

  private loadCategories() {
    this.categoryService
      .getCategories(0, 200)
      .subscribe({
        next: (response) => {
          this.categories.set(response.content);
        },
        error: (error) => {
          this.toastService.error(
            error.error?.message ?? 'Unable to load categories for product form.',
            'Products'
          );
        },
      });
  }

  private persistImages(productId: number, payload: ProductFormValue) {
    let flow$: Observable<unknown> = of(null);

    if (payload.existingImageIdsToRemove.length > 0) {
      flow$ = flow$.pipe(
        switchMap(() =>
          from(payload.existingImageIdsToRemove).pipe(
            concatMap((imageId) => this.productService.deleteProductImage(productId, imageId)),
            toArray()
          )
        )
      );
    }

    if (payload.newImages.length > 0) {
      flow$ = flow$.pipe(
        switchMap(() =>
          this.productService.uploadProductImages(
            productId,
            payload.newImages,
            payload.primaryNewImageIndex
          )
        )
      );
    }

    if (payload.primaryExistingImageId !== null) {
      flow$ = flow$.pipe(
        switchMap(() => this.productService.setPrimaryProductImage(productId, payload.primaryExistingImageId!))
      );
    }

    return flow$.pipe(map(() => productId));
  }
}
