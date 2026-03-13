import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AdminCategory {
    id: number;
    name: string;
    description: string;
    productCount: number;
}

export interface CategoryPayload {
    name: string;
    description: string;
}

export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
    first: boolean;
    last: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class AdminCategoryService {
    private http = inject(HttpClient);
    private apiUrl = 'http://localhost:8081/api/v1/categories';

    getCategories(page: number = 0, size: number = 50): Observable<PageResponse<AdminCategory>> {
        return this.http.get<PageResponse<AdminCategory>>(
            `${this.apiUrl}?page=${page}&size=${size}&sort=name,asc`
        );
    }

    createCategory(payload: CategoryPayload): Observable<AdminCategory> {
        return this.http.post<AdminCategory>(this.apiUrl, payload);
    }

    updateCategory(id: number, payload: CategoryPayload): Observable<AdminCategory> {
        return this.http.put<AdminCategory>(`${this.apiUrl}/${id}`, payload);
    }

    deleteCategory(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
