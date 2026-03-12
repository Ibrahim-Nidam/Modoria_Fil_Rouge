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
    private apiUrl = 'http://localhost:8081/api/auth';

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

        const isAdmin = response.user.roles?.some(
            (role: any) => role.name === 'ROLE_ADMIN' || role === 'ROLE_ADMIN'
        );
        this.router.navigate([isAdmin ? '/admin' : '/home']);
    }

    getToken(): string | null {
        return localStorage.getItem('modoria_token');
    }

    isAuthenticated(): boolean {
        return !!this.getToken();
    }

    isAdmin(): boolean {
        const user = this.currentUser();
        return user?.roles?.some(
            (role: any) => role.name === 'ROLE_ADMIN' || role === 'ROLE_ADMIN'
        ) ?? false;
    }
}
