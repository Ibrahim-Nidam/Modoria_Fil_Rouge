import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Header } from '../../../shared/layout/header/header';
import { ThemeService } from '../../../core/theme/theme.service';
import { ShopCatalogService, ShopCategory, ShopProduct } from '../services/shop-catalog.service';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, RouterLink, Header],
  templateUrl: './catalog.html',
})
export class CatalogComponent {
  private route = inject(ActivatedRoute);
  private themeService = inject(ThemeService);
  private shopService = inject(ShopCatalogService);

  readonly activeSeason = this.themeService.activeSeason;
  readonly backendBaseUrl = '';

  products = signal<ShopProduct[]>([]);
  categories = signal<ShopCategory[]>([]);
  loading = signal(true);
  selectedCategoryId = signal<number | null>(null);
  selectedSection = signal<string>('all');
  searchQuery = signal<string>('');

  readonly heading = computed(() => {
    if (this.searchQuery()) return `Search Results for "${this.searchQuery()}"`;
    const section = this.selectedSection();
    if (section === 'new-arrivals') return 'New Arrivals';
    if (section === 'designers') return 'Designer Selection';
    if (section === 'clothing') return 'Clothing';
    if (section === 'shoes') return 'Shoes';
    return 'Seasonal Catalog';
  });

  readonly filteredProducts = computed(() => {
    const section = this.selectedSection();
    const categoryId = this.selectedCategoryId();
    const query = this.searchQuery().toLowerCase().trim();

    let list = this.products();

    if (query) {
      list = list.filter((item) => {
        const haystack = `${item.name} ${item.description} ${item.category?.name ?? ''}`.toLowerCase();
        return haystack.includes(query);
      });
    }

    if (categoryId) {
      list = list.filter((item) => item.category?.id === categoryId);
    }

    if (section === 'new-arrivals') {
      return list.slice(0, 16);
    }

    if (section === 'clothing') {
      return list.filter((item) => /dress|shirt|jacket|coat|top|pant|trouser|skirt|blazer/i.test(item.category?.name ?? ''));
    }

    if (section === 'shoes') {
      return list.filter((item) => /shoe|boot|sneaker|heel|loafer|sandals/i.test(item.category?.name ?? ''));
    }

    if (section === 'designers') {
      return [...list].sort((a, b) => b.price - a.price);
    }

    return list;
  });

  constructor() {
    this.route.queryParamMap.subscribe((params) => {
      const category = Number(params.get('category'));
      const section = params.get('section') ?? 'all';
      const q = params.get('q') ?? '';
      this.selectedCategoryId.set(Number.isNaN(category) || category <= 0 ? null : category);
      this.selectedSection.set(section);
      this.searchQuery.set(q);
    });

    effect(() => {
      const season = this.activeSeason();
      this.loading.set(true);

      this.shopService.getSeasonProducts(season, 0, 60).subscribe({
        next: (response) => {
          this.products.set(response.content);
          this.loading.set(false);
        },
        error: () => {
          this.products.set([]);
          this.loading.set(false);
        },
      });

      this.shopService.getSeasonCategories(season, 0, 20).subscribe({
        next: (response) => this.categories.set(response.content),
        error: () => this.categories.set([]),
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

  formatPrice(price: number): string {
    return new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' }).format(price);
  }
}

