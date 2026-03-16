import { Routes } from '@angular/router';
import { authGuard } from '../../core/auth/auth.guard';

export const ROOT_ROUTES: Routes = [
  {
    path: 'home',
    loadComponent: () => import('../home/home').then((m) => m.HomeComponent),
  },
  {
    path: 'catalog',
    loadComponent: () => import('../shop/catalog/catalog').then((m) => m.CatalogComponent),
  },
  {
    path: 'product/:id',
    loadComponent: () => import('../shop/product-details/product-details').then((m) => m.ProductDetailsComponent),
  },
  {
    path: 'cart',
    canActivate: [authGuard],
    loadComponent: () => import('../shop/cart/cart').then((m) => m.CartComponent),
  },
  {
    path: 'checkout-success',
    loadComponent: () => import('../checkout-success/checkout-success').then((m) => m.CheckoutSuccessComponent),
  },
  {
    path: 'checkout-cancel',
    loadComponent: () => import('../checkout-cancel/checkout-cancel').then((m) => m.CheckoutCancelComponent),
  },
  {
    path: 'wishlist',
    canActivate: [authGuard],
    loadComponent: () => import('../shop/wishlist/wishlist').then((m) => m.WishlistComponent),
  },
  {
    path: 'stylist',
    loadComponent: () => import('../stylist/stylist').then((m) => m.StylistComponent),
  },
  {
    path: 'journal',
    loadComponent: () => import('../journal/journal').then((m) => m.JournalComponent),
  },
  {
    path: 'about',
    loadComponent: () => import('../about/about').then((m) => m.AboutComponent),
  },
  {
    path: 'contact',
    loadComponent: () => import('../contact/contact').then((m) => m.ContactComponent),
  },
  {
    path: 'profile',
    loadComponent: () => import('../profile/profile').then((m) => m.ProfileComponent),
  },
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path: '**',
    redirectTo: 'home',
  },
];
