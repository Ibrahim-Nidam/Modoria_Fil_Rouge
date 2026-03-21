import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type AdminRole = 'ADMIN' | 'CLIENT' | 'AGENT';

export interface AdminUser {
    id: number;
    fullName: string;
    email: string;
    enabled: boolean;
    deleted: boolean;
    roles: AdminRole[];
}

export interface AdminUserPayload {
    fullName: string;
    email: string;
    password?: string;
    enabled: boolean;
    roles: AdminRole[];
}

@Injectable({
    providedIn: 'root'
})
export class AdminUserService {
    private http = inject(HttpClient);
    private apiUrl = '/api/v1/admin/users';

    getUsers(): Observable<AdminUser[]> {
        return this.http.get<AdminUser[]>(this.apiUrl);
    }

    createUser(payload: Required<AdminUserPayload>): Observable<AdminUser> {
        return this.http.post<AdminUser>(this.apiUrl, payload);
    }

    updateUser(id: number, payload: AdminUserPayload): Observable<AdminUser> {
        return this.http.put<AdminUser>(`${this.apiUrl}/${id}`, payload);
    }

    deleteUser(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}

