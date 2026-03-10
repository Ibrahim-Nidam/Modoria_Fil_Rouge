import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
    {
        path: 'auth',
        loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
    },
    {
        path: 'home',
        canActivate: [authGuard],
        loadComponent: () => import('./shared/layout/header/header').then(m => m.Header) // Temporary placeholder
    },
    {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
    }
];
