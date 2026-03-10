import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: 'auth',
        loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
    },
    {
        path: 'home',
        loadComponent: () => import('./shared/layout/header/header').then(m => m.Header) // Temporary placeholder
    },
    {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
    }
];
