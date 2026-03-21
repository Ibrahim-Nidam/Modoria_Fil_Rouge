import { Injectable, inject, signal, Injector } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    user: {
        id: number;
        fullName: string;
        email: string;
        enabled: boolean;
        roles: any[];
    };
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private http = inject(HttpClient);
    private injector = inject(Injector);
    private _router?: Router;

    public currentUser = signal<any>(null);
    private apiUrl = '/api/auth';

    private normalizeRoleName(roleName: string): string {
        const normalized = roleName.trim().toUpperCase();
        return normalized.startsWith('ROLE_') ? normalized.slice(5) : normalized;
    }

    private extractRoleName(role: any): string | null {
        if (typeof role === 'string') {
            return role;
        }

        if (role && typeof role === 'object' && typeof role.name === 'string') {
            return role.name;
        }

        return null;
    }

    private hasRole(roles: any[] | undefined, expectedRole: string): boolean {
        const expected = this.normalizeRoleName(expectedRole);
        return (
            roles?.some((role: any) => {
                const roleName = this.extractRoleName(role);
                return !!roleName && this.normalizeRoleName(roleName) === expected;
            }) ?? false
        );
    }

    private get router(): Router {
        if (!this._router) {
            this._router = this.injector.get(Router);
        }
        return this._router;
    }

    constructor() {
        const savedUser = localStorage.getItem('modoria_user');
        if (savedUser && savedUser !== 'undefined') {
            try {
                this.currentUser.set(JSON.parse(savedUser));
            } catch (e) {
                console.error('Error parsing saved user', e);
                localStorage.removeItem('modoria_user');
            }
        }
    }

    login(credentials: any): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
            tap(response => this.handleAuthSuccess(response))
        );
    }

    register(userData: any): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData).pipe(
            tap(response => this.handleAuthSuccess(response))
        );
    }

    refreshToken(token: string): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, { refreshToken: token });
    }

    forgotPassword(email: string): Observable<any> {
        return this.http.post(`${this.apiUrl}/forgot-password`, { email }, { responseType: 'text' });
    }

    resetPassword(token: string, newPassword: string): Observable<any> {
        return this.http.post(`${this.apiUrl}/reset-password`, { token, newPassword }, { responseType: 'text' });
    }

    logout() {
        localStorage.removeItem('modoria_token');
        localStorage.removeItem('modoria_refresh_token');
        localStorage.removeItem('modoria_user');
        this.currentUser.set(null);
        this.router.navigate(['/auth/login']);
    }

    private handleAuthSuccess(response: AuthResponse) {
        localStorage.setItem('modoria_token', response.accessToken);
        localStorage.setItem('modoria_refresh_token', response.refreshToken);
        localStorage.setItem('modoria_user', JSON.stringify(response.user));
        this.currentUser.set(response.user);

        const isAdmin = this.hasRole(response.user.roles, 'ADMIN');
        const isAgent = this.hasRole(response.user.roles, 'AGENT');
        this.router.navigate([isAdmin ? '/admin' : isAgent ? '/agent/tickets' : '/home']);
    }

    getToken(): string | null {
        return localStorage.getItem('modoria_token');
    }

    isAuthenticated(): boolean {
        return !!this.getToken();
    }

    isAdmin(): boolean {
        const user = this.currentUser();
        return this.hasRole(user?.roles, 'ADMIN');
    }

    isAgent(): boolean {
        const user = this.currentUser();
        return this.hasRole(user?.roles, 'AGENT');
    }

    isClient(): boolean {
        const user = this.currentUser();
        return this.hasRole(user?.roles, 'CLIENT');
    }
}

