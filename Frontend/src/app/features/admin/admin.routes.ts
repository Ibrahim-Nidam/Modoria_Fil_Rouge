import { Routes } from '@angular/router';

export const ADMIN_ROUTES: Routes = [
    {
        path: '',
        loadComponent: () =>
            import('./layout/admin-layout/admin-layout').then(m => m.AdminLayout),
        children: [
            {
                path: '',
                loadComponent: () =>
                    import('./pages/admin-dashboard/admin-dashboard').then(m => m.AdminDashboard),
            },
            {
                path: 'categories',
                loadComponent: () =>
                    import('./pages/admin-categories/admin-categories').then(m => m.AdminCategories),
            },
            {
                path: 'products',
                loadComponent: () =>
                    import('./pages/admin-products/admin-products').then(m => m.AdminProducts),
            },
            {
                path: 'users',
                loadComponent: () =>
                    import('./pages/admin-users/admin-users').then(m => m.AdminUsers),
            },
            {
                path: 'orders',
                loadComponent: () =>
                    import('./pages/admin-orders/admin-orders').then(m => m.AdminOrders),
            },
            {
                path: 'tickets',
                loadComponent: () =>
                    import('./pages/admin-tickets/admin-tickets').then(m => m.AdminTickets),
            },
        ],
    },
];
