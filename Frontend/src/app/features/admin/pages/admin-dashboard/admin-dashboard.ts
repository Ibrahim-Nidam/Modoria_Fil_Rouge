import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, map, of, retry } from 'rxjs';
import { StatCard } from '../../components/stat-card/stat-card';
import { AdminCategoryService, AdminCategory, PageResponse as CategoryPageResponse } from '../../services/admin-category.service';
import { AdminProductService, AdminProduct } from '../../services/admin-product.service';
import { AdminUser, AdminUserService } from '../../services/admin-user.service';
import { AdminDashboardService, AdminDashboardStats } from '../../services/admin-dashboard.service';

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
  private dashboardService = inject(AdminDashboardService);
  private readonly statsStorageKey = 'modoria_admin_dashboard_stats';
  private readonly currencyFormatter = new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' });

  readonly loading = signal(true);
  readonly loadError = signal(false);
  readonly stats = signal<DashboardStat[]>([
    { title: 'Total Users', value: '-', icon: 'group', trend: '', trendUp: null },
    { title: 'Enabled Users', value: '-', icon: 'verified_user', trend: '', trendUp: null },
    { title: 'Total Products', value: '-', icon: 'inventory_2', trend: '', trendUp: null },
    { title: 'Low Stock (<20)', value: '-', icon: 'warning', trend: '', trendUp: null },
    { title: 'Total Categories', value: '-', icon: 'category', trend: '', trendUp: null },
    { title: 'Completed Sales', value: '-', icon: 'payments', trend: '', trendUp: null },
    { title: 'Sales This Month', value: '-', icon: 'calendar_month', trend: '', trendUp: null },
    { title: 'Total Tickets', value: '-', icon: 'confirmation_number', trend: '', trendUp: null },
    { title: 'In Progress Tickets', value: '-', icon: 'support_agent', trend: '', trendUp: null },
    { title: 'Resolved Tickets', value: '-', icon: 'verified', trend: '', trendUp: null },
  ]);

  ngOnInit(): void {
    this.hydrateStatsFromStorage();
    this.loadStats();
  }

  private loadStats() {
    this.loading.set(true);
    this.loadError.set(false);

    const previous = this.stats();

    forkJoin({
      users: this.userService.getUsers().pipe(
        retry({ count: 1, delay: 250 }),
        map((data) => ({ ok: true, data }) as LoadResult<AdminUser[]>),
        catchError(() => {
          this.loadError.set(true);
          return of({ ok: false, data: null } as LoadResult<AdminUser[]>);
        })
      ),
      products: this.productService
        .getProducts(0, 1000)
        .pipe(
          retry({ count: 1, delay: 250 }),
          map((data) => ({ ok: true, data }) as LoadResult<CategoryPageResponse<AdminProduct>>),
          catchError(() => {
            this.loadError.set(true);
            return of({ ok: false, data: null } as LoadResult<CategoryPageResponse<AdminProduct>>);
          })
        ),
      categories: this.categoryService
        .getCategories(0, 1000)
        .pipe(
          retry({ count: 1, delay: 250 }),
          map((data) => ({ ok: true, data }) as LoadResult<CategoryPageResponse<AdminCategory>>),
          catchError(() => {
            this.loadError.set(true);
            return of({ ok: false, data: null } as LoadResult<CategoryPageResponse<AdminCategory>>);
          })
        ),
      dashboard: this.dashboardService.getStats().pipe(
        retry({ count: 1, delay: 250 }),
        map((data) => ({ ok: true, data }) as LoadResult<AdminDashboardStats>),
        catchError(() => {
          this.loadError.set(true);
          return of({ ok: false, data: null } as LoadResult<AdminDashboardStats>);
        })
      ),
    }).subscribe({
      next: ({ users, products, categories, dashboard }) => {
        const usersData = users.ok && users.data ? users.data : null;
        const productsData = products.ok && products.data ? products.data : null;
        const categoriesData = categories.ok && categories.data ? categories.data : null;
        const dashboardData = dashboard.ok && dashboard.data ? dashboard.data : null;

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

        this.stats.set([
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
          dashboardData
            ? {
                title: 'Completed Sales',
                value: this.currencyFormatter.format(dashboardData.completedSalesTotal ?? 0),
                icon: 'payments',
                trend: `${dashboardData.completedOrders} completed orders`,
                trendUp: dashboardData.completedOrders > 0 ? true : null,
              }
            : keepOr('Completed Sales', { icon: 'payments', trend: 'Unavailable', trendUp: null }),
          dashboardData
            ? {
                title: 'Sales This Month',
                value: this.currencyFormatter.format(dashboardData.completedSalesThisMonth ?? 0),
                icon: 'calendar_month',
                trend: `${dashboardData.ordersThisMonth} orders created this month`,
                trendUp: dashboardData.ordersThisMonth > 0 ? true : null,
              }
            : keepOr('Sales This Month', { icon: 'calendar_month', trend: 'Unavailable', trendUp: null }),
          dashboardData
            ? {
                title: 'Total Tickets',
                value: dashboardData.totalTickets.toString(),
                icon: 'confirmation_number',
                trend: `${dashboardData.openTickets} open • ${dashboardData.unassignedTickets} unassigned`,
                trendUp: null,
              }
            : keepOr('Total Tickets', { icon: 'confirmation_number', trend: 'Unavailable', trendUp: null }),
          dashboardData
            ? {
                title: 'In Progress Tickets',
                value: dashboardData.inProgressTickets.toString(),
                icon: 'support_agent',
                trend: `${dashboardData.ticketsThisMonth} created this month`,
                trendUp: dashboardData.inProgressTickets > 0 ? false : true,
              }
            : keepOr('In Progress Tickets', { icon: 'support_agent', trend: 'Unavailable', trendUp: null }),
          dashboardData
            ? {
                title: 'Resolved Tickets',
                value: dashboardData.resolvedTickets.toString(),
                icon: 'verified',
                trend:
                  dashboardData.totalTickets > 0
                    ? `${Math.round((dashboardData.resolvedTickets / dashboardData.totalTickets) * 100)}% resolution rate`
                    : 'No tickets yet',
                trendUp: dashboardData.resolvedTickets > 0 ? true : null,
              }
            : keepOr('Resolved Tickets', { icon: 'verified', trend: 'Unavailable', trendUp: null }),
        ]);

        this.persistStats();

        this.loading.set(false);
      },
      error: () => {
        this.loadError.set(true);
        this.loading.set(false);
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
        this.stats.set(parsed);
      }
    } catch {
      localStorage.removeItem(this.statsStorageKey);
    }
  }

  private persistStats() {
    localStorage.setItem(this.statsStorageKey, JSON.stringify(this.stats()));
  }
}
