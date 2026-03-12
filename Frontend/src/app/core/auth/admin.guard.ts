import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from './auth.service';

export const adminGuard: CanActivateFn = () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (!authService.isAuthenticated()) {
        router.navigate(['/auth/login']);
        return false;
    }

    const user = authService.currentUser();
    const isAdmin = user?.roles?.some(
        (role: any) => role.name === 'ROLE_ADMIN' || role === 'ROLE_ADMIN'
    );

    if (!isAdmin) {
        router.navigate(['/home']);
        return false;
    }

    return true;
};
