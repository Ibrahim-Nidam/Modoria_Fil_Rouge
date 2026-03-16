import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/toast/toast.service';
import { Header } from '../../../shared/layout/header/header';
import { ShopCheckoutService } from '../services/shop-checkout.service';
import { ShopStateService } from '../services/shop-state.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterLink, Header],
  templateUrl: './cart.html',
})
export class CartComponent {
  readonly shopState = inject(ShopStateService);
  private readonly shopCheckout = inject(ShopCheckoutService);
  private readonly authService = inject(AuthService);
  private readonly toastService = inject(ToastService);
  private readonly router = inject(Router);

  readonly backendBaseUrl = 'http://localhost:8081';
  isProcessingCheckout = false;

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

  async checkout() {
    if (this.shopState.cartItems().length === 0) {
      this.toastService.warning('Your cart is empty.', 'Checkout');
      return;
    }

    if (!this.authService.isAuthenticated()) {
      this.toastService.info('Please sign in to continue checkout.', 'Checkout');
      this.router.navigate(['/auth/login']);
      return;
    }

    this.isProcessingCheckout = true;

    try {
      const session = await firstValueFrom(this.shopCheckout.createCheckoutSession(this.shopState.cartItems()));
      if (!session?.url) {
        this.toastService.error('Unable to start Stripe checkout. Please try again.', 'Checkout');
        return;
      }

      window.location.href = session.url;
    } catch (error: any) {
      const message = error?.error?.message ?? 'Checkout failed. Please try again.';
      this.toastService.error(message, 'Checkout');
    } finally {
      this.isProcessingCheckout = false;
    }
  }
}
