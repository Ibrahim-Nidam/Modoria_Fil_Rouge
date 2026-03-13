import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, map, of, retry } from 'rxjs';
import { StatCard } from '../../components/stat-card/stat-card';
import { AdminCategoryService, AdminCategory, PageResponse as CategoryPageResponse } from '../../services/admin-category.service';
import { AdminProductService, AdminProduct } from '../../services/admin-product.service';
import { AdminUser, AdminUserService } from '../../services/admin-user.service';

interface DashboardStat {
  title: string;
  value: string;
  icon: string;
  trend: string;
  trendUp: boolean | null;
}

interface LoadResult<T> {
  ok: boolean;
  data: T | null;
}

@Component({
  selector: 'app-admin-dashboard',
  imports: [StatCard, RouterLink],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard implements OnInit {
  private userService = inject(AdminUserService);
  private productService = inject(AdminProductService);
  private categoryService = inject(AdminCategoryService);
  private readonly statsStorageKey = 'modoria_admin_dashboard_stats';

  loading = true;
  loadError = false;
  stats: DashboardStat[] = [
    { title: 'Total Users', value: '-', icon: 'group', trend: '', trendUp: null },
    { title: 'Enabled Users', value: '-', icon: 'verified_user', trend: '', trendUp: null },
    { title: 'Total Products', value: '-', icon: 'inventory_2', trend: '', trendUp: null },
    { title: 'Low Stock (<20)', value: '-', icon: 'warning', trend: '', trendUp: null },
    { title: 'Total Categories', value: '-', icon: 'category', trend: '', trendUp: null },
  ];

  ngOnInit(): void {
    this.hydrateStatsFromStorage();
    this.loadStats();
  }

  private loadStats() {
    this.loading = true;
    this.loadError = false;

    const previous = this.stats;

    forkJoin({
      users: this.userService.getUsers().pipe(
        retry({ count: 1, delay: 250 }),
        map((data) => ({ ok: true, data }) as LoadResult<AdminUser[]>),
        catchError(() => {
          this.loadError = true;
          return of({ ok: false, data: null } as LoadResult<AdminUser[]>);
        })
      ),
      products: this.productService
        .getProducts(0, 1000)
        .pipe(
          retry({ count: 1, delay: 250 }),
          map((data) => ({ ok: true, data }) as LoadResult<CategoryPageResponse<AdminProduct>>),
          catchError(() => {
            this.loadError = true;
            return of({ ok: false, data: null } as LoadResult<CategoryPageResponse<AdminProduct>>);
          })
        ),
      categories: this.categoryService
        .getCategories(0, 1000)
        .pipe(
          retry({ count: 1, delay: 250 }),
          map((data) => ({ ok: true, data }) as LoadResult<CategoryPageResponse<AdminCategory>>),
          catchError(() => {
            this.loadError = true;
            return of({ ok: false, data: null } as LoadResult<CategoryPageResponse<AdminCategory>>);
          })
        ),
    }).subscribe({
      next: ({ users, products, categories }) => {
        const usersData = users.ok && users.data ? users.data : null;
        const productsData = products.ok && products.data ? products.data : null;
        const categoriesData = categories.ok && categories.data ? categories.data : null;

        const enabledUsers = usersData ? usersData.filter(user => user.enabled).length : null;
        const disabledUsers = usersData ? usersData.length - enabledUsers! : null;
        const productList = productsData?.content ?? null;
        const lowStockCount = productList ? productList.filter((product) => product.stock < 20).length : null;

        const previousByTitle = new Map(previous.map((stat) => [stat.title, stat]));
        const keepOr = (title: string, fallback: Partial<DashboardStat>): DashboardStat => {
          const previousStat = previousByTitle.get(title);
          return {
            title,
            value: previousStat?.value ?? fallback.value ?? '-',
            icon: previousStat?.icon ?? fallback.icon ?? 'info',
            trend: previousStat?.trend ?? fallback.trend ?? '',
            trendUp: previousStat?.trendUp ?? fallback.trendUp ?? null,
          };
        };

        this.stats = [
          usersData
            ? {
                title: 'Total Users',
                value: usersData.length.toString(),
                icon: 'group',
                trend: disabledUsers! > 0 ? `${disabledUsers} disabled` : 'All enabled',
                trendUp: disabledUsers! > 0 ? false : true,
              }
            : keepOr('Total Users', { icon: 'group', trend: 'Unavailable', trendUp: null }),
          usersData
            ? {
                title: 'Enabled Users',
                value: enabledUsers!.toString(),
                icon: 'verified_user',
                trend: `${usersData.length === 0 ? 0 : Math.round((enabledUsers! / usersData.length) * 100)}% of users`,
                trendUp: true,
              }
            : keepOr('Enabled Users', { icon: 'verified_user', trend: 'Unavailable', trendUp: null }),
          productsData
            ? {
                title: 'Total Products',
                value: productsData.totalElements.toString(),
                icon: 'inventory_2',
                trend: `${productList!.length} loaded for analysis`,
                trendUp: null,
              }
            : keepOr('Total Products', { icon: 'inventory_2', trend: 'Unavailable', trendUp: null }),
          productsData
            ? {
                title: 'Low Stock (<20)',
                value: lowStockCount!.toString(),
                icon: 'warning',
                trend: lowStockCount! > 0 ? 'Needs restock attention' : 'Stock levels healthy',
                trendUp: lowStockCount! > 0 ? false : true,
              }
            : keepOr('Low Stock (<20)', { icon: 'warning', trend: 'Unavailable', trendUp: null }),
          categoriesData
            ? {
                title: 'Total Categories',
                value: categoriesData.totalElements.toString(),
                icon: 'category',
                trend: `${categoriesData.content.length} loaded`,
                trendUp: null,
              }
            : keepOr('Total Categories', { icon: 'category', trend: 'Unavailable', trendUp: null }),
        ];

        this.persistStats();

        this.loading = false;
      },
      error: () => {
        this.loadError = true;
        this.loading = false;
      },
    });
  }

  private hydrateStatsFromStorage() {
    const raw = localStorage.getItem(this.statsStorageKey);
    if (!raw) {
      return;
    }

    try {
      const parsed = JSON.parse(raw) as DashboardStat[];
      if (Array.isArray(parsed) && parsed.length > 0) {
        this.stats = parsed;
      }
    } catch {
      localStorage.removeItem(this.statsStorageKey);
    }
  }

  private persistStats() {
    localStorage.setItem(this.statsStorageKey, JSON.stringify(this.stats));
  }
}
