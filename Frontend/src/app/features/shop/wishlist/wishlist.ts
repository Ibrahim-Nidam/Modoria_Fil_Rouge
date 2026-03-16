import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Header } from '../../../shared/layout/header/header';
import { ShopProduct } from '../services/shop-catalog.service';
import { ShopStateService } from '../services/shop-state.service';
import { ToastService } from '../../../core/toast/toast.service';

@Component({
  selector: 'app-wishlist',
  standalone: true,
  imports: [CommonModule, RouterLink, Header],
  templateUrl: './wishlist.html',
})
export class WishlistComponent {
  readonly shopState = inject(ShopStateService);
  private toastService = inject(ToastService);
  readonly backendBaseUrl = 'http://localhost:8081';

  getImageUrl(imagePath: string | null | undefined): string {
    if (!imagePath) {
      return '';
    }

    if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
      return imagePath;
    }

    return `${this.backendBaseUrl}${imagePath.startsWith('/') ? imagePath : '/' + imagePath}`;
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' }).format(price);
  }

  addToCartFromWishlist(product: ShopProduct) {
    if (this.shopState.addToCart(product)) {
      this.toastService.success(`${product.name} added to cart.`, 'Cart');
    }
  }

  removeFromWishlist(product: ShopProduct) {
    const result = this.shopState.toggleWishlist(product);
    if (result === 'removed') {
      this.toastService.info(`${product.name} removed from wishlist.`, 'Wishlist');
    }
  }
}
