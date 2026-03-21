import { Component, computed, effect, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Header } from '../../shared/layout/header/header';
import { ThemeService } from '../../core/theme/theme.service';
import { HomeCatalogService, HomeCategory, HomeProduct } from './home-catalog.service';
import { ShopStateService } from '../shop/services/shop-state.service';
import { ToastService } from '../../core/toast/toast.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, Header],
  templateUrl: './home.html',
})
export class HomeComponent {
  private themeService = inject(ThemeService);
  private catalogService = inject(HomeCatalogService);
  private shopState = inject(ShopStateService);
  private toastService = inject(ToastService);

  readonly activeSeason = this.themeService.activeSeason;
  readonly isDarkMode = this.themeService.isDarkMode;

  products = signal<HomeProduct[]>([]);
  categories = signal<HomeCategory[]>([]);
  loading = signal(true);

  readonly backendBaseUrl = '';

  heroProduct = computed(() => this.products()[0] ?? null);
  heroStack = computed(() => this.products().slice(1, 4));
  productGrid = computed(() => this.products().slice(0, 8));

  constructor() {
    effect(() => {
      const season = this.activeSeason();
      this.loading.set(true);
      this.products.set([]);
      this.categories.set([]);

      this.catalogService.getSeasonProducts(season, 0, 12).subscribe({
        next: (res) => {
          this.products.set(res.content);
          this.loading.set(false);
        },
        error: () => this.loading.set(false),
      });

      this.catalogService.getSeasonCategories(season, 0, 10).subscribe({
        next: (res) => this.categories.set(res.content),
      });
    });
  }

  getImageUrl(imagePath: string | null | undefined): string | null {
    if (!imagePath) return null;
    if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) return imagePath;
    return `${this.backendBaseUrl}${imagePath.startsWith('/') ? imagePath : '/' + imagePath}`;
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' }).format(price);
  }

  addToCart(product: HomeProduct, event: Event) {
    event.preventDefault();
    event.stopPropagation();
    if (this.shopState.addToCart(product)) {
      this.toastService.success(`${product.name} added to cart.`, 'Cart');
    }
  }

  toggleWishlist(product: HomeProduct, event: Event) {
    event.preventDefault();
    event.stopPropagation();
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

  isInWishlist(productId: number): boolean {
    return this.shopState.isInWishlist(productId);
  }
}


