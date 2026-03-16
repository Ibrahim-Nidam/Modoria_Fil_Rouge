import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { Header } from '../../shared/layout/header/header';
import { ToastService } from '../../core/toast/toast.service';
import { ShopCheckoutService } from '../shop/services/shop-checkout.service';
import { ShopStateService } from '../shop/services/shop-state.service';

interface ConfirmedOrder {
  id: number;
  totalAmount: number;
  status: string;
}

@Component({
  selector: 'app-checkout-success',
  standalone: true,
  imports: [CommonModule, RouterLink, Header],
  templateUrl: './checkout-success.html',
})
export class CheckoutSuccessComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly checkoutService = inject(ShopCheckoutService);
  private readonly shopState = inject(ShopStateService);
  private readonly toastService = inject(ToastService);

  readonly loading = signal(true);
  readonly order = signal<ConfirmedOrder | null>(null);
  readonly errorMessage = signal<string | null>(null);

  constructor() {
    void this.confirmCheckoutSession();
  }

  private async confirmCheckoutSession() {
    const sessionId = this.route.snapshot.queryParamMap.get('session_id');
    if (!sessionId) {
      this.errorMessage.set('Missing Stripe session id.');
      this.loading.set(false);
      return;
    }

    try {
      const order = await firstValueFrom(this.checkoutService.confirmCheckoutSession(sessionId));
      this.order.set(order as ConfirmedOrder);
      this.shopState.clearCart();
      this.toastService.success('Payment confirmed. Your order is complete.', 'Checkout');
    } catch (error: any) {
      this.errorMessage.set(error?.error?.message ?? 'Unable to confirm payment session.');
    } finally {
      this.loading.set(false);
    }
  }

  formatPrice(amount: number): string {
    return new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' }).format(amount);
  }
}
