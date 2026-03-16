import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { throwError, BehaviorSubject, catchError, filter, take, switchMap } from 'rxjs';
import { AuthService, AuthResponse } from './auth.service';

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
    const authService = inject(AuthService);

    // Ignore auth specific endpoints for attachment/interception
    if (req.url.includes('/api/auth/login') || req.url.includes('/api/auth/register') || req.url.includes('/api/auth/refresh')) {
        return next(req);
    }

    const token = authService.getToken();
    let authReq = req;

    if (token) {
        authReq = addTokenHeader(req, token);
    }

    return next(authReq).pipe(
        catchError((error: HttpErrorResponse) => {
            // Guest requests should not trigger refresh/logout flows.
            if (!token) {
                return throwError(() => error);
            }

            if ((error.status === 401 || error.status === 403) && !authReq.url.includes('/api/auth/')) {
                return handle401Error(authReq, next, authService);
            }
            return throwError(() => error);
        })
    );
};

function addTokenHeader(request: HttpRequest<any>, token: string) {
    return request.clone({
        headers: request.headers.set('Authorization', `Bearer ${token}`)
    });
}

function handle401Error(request: HttpRequest<any>, next: HttpHandlerFn, authService: AuthService) {
    if (!isRefreshing) {
        isRefreshing = true;
        refreshTokenSubject.next(null);

        const refreshToken = localStorage.getItem('modoria_refresh_token');

        if (refreshToken) {
            return authService.refreshToken(refreshToken).pipe(
                switchMap((response: AuthResponse) => {
                    isRefreshing = false;

                    localStorage.setItem('modoria_token', response.accessToken);
                    localStorage.setItem('modoria_refresh_token', response.refreshToken);

                    refreshTokenSubject.next(response.accessToken);
                    return next(addTokenHeader(request, response.accessToken));
                }),
                catchError((err) => {
                    isRefreshing = false;
                    authService.logout();
                    return throwError(() => err);
                })
            );
        } else {
            isRefreshing = false;
            authService.logout();
            return throwError(() => new Error('No refresh token available'));
        }
    }

    return refreshTokenSubject.pipe(
        filter(token => token !== null),
        take(1),
        switchMap((token) => {
            return next(addTokenHeader(request, token!));
        })
    );
}
