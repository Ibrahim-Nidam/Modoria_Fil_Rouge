import { Routes } from '@angular/router';
import { adminGuard } from './core/auth/admin.guard';
import { agentGuard } from './core/auth/agent.guard';

export const routes: Routes = [
    {
        path: 'auth',
        loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
    },
    {
        path: 'admin',
        canActivate: [adminGuard],
        loadChildren: () => import('./features/admin/admin.routes').then(m => m.ADMIN_ROUTES)
    },
    {
        path: 'agent',
        canActivate: [agentGuard],
        loadChildren: () => import('./features/agent/agent.routes').then(m => m.AGENT_ROUTES)
    },
    {
        path: '',
        loadChildren: () => import('./features/root/root.routes').then(m => m.ROOT_ROUTES)
    }
];
