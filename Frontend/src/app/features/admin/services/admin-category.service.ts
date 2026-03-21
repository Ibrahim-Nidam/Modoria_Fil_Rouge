import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type CategorySeason = 'SPRING' | 'SUMMER' | 'AUTUMN' | 'WINTER';

export interface AdminCategory {
    id: number;
    name: string;
    season: CategorySeason;
    description: string;
    deleted: boolean;
    imagePath?: string | null;
    productCount: number;
}

export interface CategoryPayload {
    name: string;
    season: CategorySeason;
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
    private apiUrl = '/api/v1/categories';

    getCategories(
        page: number = 0,
        size: number = 50,
        season?: CategorySeason | null,
        includeDeleted: boolean = false
    ): Observable<PageResponse<AdminCategory>> {
        const seasonQuery = season ? `&season=${season}` : '';
        const includeDeletedQuery = `&includeDeleted=${includeDeleted}`;
        return this.http.get<PageResponse<AdminCategory>>(
            `${this.apiUrl}?page=${page}&size=${size}&sort=name,asc${seasonQuery}${includeDeletedQuery}`
        );
    }

    createCategory(payload: CategoryPayload): Observable<AdminCategory> {
        return this.http.post<AdminCategory>(this.apiUrl, payload);
    }

    updateCategory(id: number, payload: CategoryPayload): Observable<AdminCategory> {
        return this.http.put<AdminCategory>(`${this.apiUrl}/${id}`, payload);
    }

    uploadCategoryImage(id: number, file: File): Observable<AdminCategory> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post<AdminCategory>(`${this.apiUrl}/${id}/image`, formData);
    }

    deleteCategory(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    restoreCategory(id: number): Observable<AdminCategory> {
        return this.http.patch<AdminCategory>(`${this.apiUrl}/${id}/restore`, {});
    }
}

