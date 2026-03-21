import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CartItem } from './shop-state.service';

export interface CheckoutSessionResponse {
  url: string;
  sessionId: string;
  orderId: number;
}

@Injectable({ providedIn: 'root' })
export class ShopCheckoutService {
  private http = inject(HttpClient);
  private readonly baseUrl = '/api/v1';

  createCheckoutSession(items: CartItem[]): Observable<CheckoutSessionResponse> {
    return this.http.post<CheckoutSessionResponse>(`${this.baseUrl}/payments/checkout-session`, {
      items: items.map((item) => ({
        productId: item.product.id,
        quantity: item.quantity,
      })),
    });
  }

  confirmCheckoutSession(sessionId: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/payments/confirm-session?sid=${encodeURIComponent(sessionId)}`, {});
  }
}

