import { Injectable, computed, effect, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/toast/toast.service';
import { ShopProduct } from './shop-catalog.service';

export interface CartItem {
  product: ShopProduct;
  quantity: number;
}

export type WishlistToggleResult = 'added' | 'removed' | 'blocked';

@Injectable({ providedIn: 'root' })
export class ShopStateService {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);

  readonly cartItems = signal<CartItem[]>([]);
  readonly wishlistItems = signal<ShopProduct[]>([]);

  readonly cartCount = computed(() => this.cartItems().reduce((sum, item) => sum + item.quantity, 0));
  readonly wishlistCount = computed(() => this.wishlistItems().length);
  readonly cartSubtotal = computed(() =>
    this.cartItems().reduce((sum, item) => sum + item.product.price * item.quantity, 0)
  );

  constructor() {
    effect(() => {
      // Keep storage isolated per authenticated user id (or guest session).
      this.authService.currentUser()?.id;
      this.hydrate();
    }, { allowSignalWrites: true });
  }

  private get cartKey(): string {
    return `modoria_cart_items_${this.userScopeKey()}`;
  }

  private get wishlistKey(): string {
    return `modoria_wishlist_items_${this.userScopeKey()}`;
  }

  private userScopeKey(): string {
    const user = this.authService.currentUser();
    return user?.id ? `user_${user.id}` : 'guest';
  }

  private hydrate() {
    try {
      const cartRaw = localStorage.getItem(this.cartKey);
      const wishlistRaw = localStorage.getItem(this.wishlistKey);

      this.cartItems.set(cartRaw ? JSON.parse(cartRaw) : []);
      this.wishlistItems.set(wishlistRaw ? JSON.parse(wishlistRaw) : []);
    } catch {
      this.cartItems.set([]);
      this.wishlistItems.set([]);
    }
  }

  private persist() {
    localStorage.setItem(this.cartKey, JSON.stringify(this.cartItems()));
    localStorage.setItem(this.wishlistKey, JSON.stringify(this.wishlistItems()));
  }

  isInWishlist(productId: number): boolean {
    return this.wishlistItems().some((item) => item.id === productId);
  }

  toggleWishlist(product: ShopProduct): WishlistToggleResult {
    if (!this.requireAuthentication('wishlist')) {
      return 'blocked';
    }

    const exists = this.isInWishlist(product.id);
    if (exists) {
      this.wishlistItems.set(this.wishlistItems().filter((item) => item.id !== product.id));
    } else {
      this.wishlistItems.set([product, ...this.wishlistItems()]);
    }
    this.persist();

    return exists ? 'removed' : 'added';
  }

  addToCart(product: ShopProduct, quantity = 1): boolean {
    if (!this.requireAuthentication('cart')) {
      return false;
    }

    const current = this.cartItems();
    const existing = current.find((item) => item.product.id === product.id);

    if (existing) {
      this.cartItems.set(
        current.map((item) =>
          item.product.id === product.id
            ? { ...item, quantity: item.quantity + Math.max(1, quantity) }
            : item
        )
      );
    } else {
      this.cartItems.set([{ product, quantity: Math.max(1, quantity) }, ...current]);
    }

    this.persist();
    return true;
  }

  removeFromCart(productId: number) {
    this.cartItems.set(this.cartItems().filter((item) => item.product.id !== productId));
    this.persist();
  }

  setQuantity(productId: number, quantity: number) {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return;
    }

    this.cartItems.set(
      this.cartItems().map((item) =>
        item.product.id === productId ? { ...item, quantity } : item
      )
    );
    this.persist();
  }

  clearCart() {
    this.cartItems.set([]);
    this.persist();
  }

  private requireAuthentication(target: 'cart' | 'wishlist'): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    }

    const destination = target === 'cart' ? 'cart' : 'wishlist';
    this.toastService.warning(
      `Please sign in to add items to your ${destination}.`,
      'Authentication'
    );
    this.router.navigate(['/auth/login'], {
      queryParams: { redirectTo: this.router.url },
    });
    return false;
  }
}
