import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Header } from '../../../shared/layout/header/header';
import { ShopCatalogService, ShopProduct } from '../services/shop-catalog.service';
import { ShopStateService } from '../services/shop-state.service';
import { ToastService } from '../../../core/toast/toast.service';

@Component({
  selector: 'app-product-details',
  standalone: true,
  imports: [CommonModule, RouterLink, Header],
  templateUrl: './product-details.html',
})
export class ProductDetailsComponent {
  private route = inject(ActivatedRoute);
  private shopService = inject(ShopCatalogService);
  private shopState = inject(ShopStateService);
  private toastService = inject(ToastService);

  readonly backendBaseUrl = 'http://localhost:8081';

  product = signal<ShopProduct | null>(null);
  loading = signal(true);
  selectedImage = signal<string | null>(null);

  constructor() {
    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));
      if (Number.isNaN(id) || id <= 0) {
        this.product.set(null);
        this.loading.set(false);
        return;
      }

      this.loading.set(true);
      this.shopService.getProductById(id).subscribe({
        next: (value) => {
          this.product.set(value);
          this.selectedImage.set(this.getImageUrl(value.primaryImagePath));
          this.loading.set(false);
        },
        error: () => {
          this.product.set(null);
          this.loading.set(false);
        },
      });
    });
  }

  getImageUrl(imagePath: string | null | undefined): string {
    if (!imagePath) {
      return '';
    }

    if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
      return imagePath;
    }

    return `${this.backendBaseUrl}${imagePath.startsWith('/') ? imagePath : '/' + imagePath}`;
  }

  selectImage(path: string | null | undefined) {
    this.selectedImage.set(this.getImageUrl(path));
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' }).format(price);
  }

  addToCart() {
    const product = this.product();
    if (!product) {
      return;
    }

    if (this.shopState.addToCart(product)) {
      this.toastService.success(`${product.name} added to cart.`, 'Cart');
    }
  }

  toggleWishlist() {
    const product = this.product();
    if (!product) {
      return;
    }

    const result = this.shopState.toggleWishlist(product);
    if (result === 'blocked') {
      return;
    }

    this.toastService.info(
      result === 'removed'
        ? `${product.name} removed from wishlist.`
        : `${product.name} added to wishlist.`,
      'Wishlist'
    );
  }

  inWishlist(): boolean {
    const product = this.product();
    return !!product && this.shopState.isInWishlist(product.id);
  }
}
