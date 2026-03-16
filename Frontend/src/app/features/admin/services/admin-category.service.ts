import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type CategorySeason = 'SPRING' | 'SUMMER' | 'AUTUMN' | 'WINTER';

export interface AdminCategory {
    id: number;
    name: string;
    season: CategorySeason;
    description: string;
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
    private apiUrl = 'http://localhost:8081/api/v1/categories';

    getCategories(
        page: number = 0,
        size: number = 50,
        season?: CategorySeason | null
    ): Observable<PageResponse<AdminCategory>> {
        const seasonQuery = season ? `&season=${season}` : '';
        return this.http.get<PageResponse<AdminCategory>>(
            `${this.apiUrl}?page=${page}&size=${size}&sort=name,asc${seasonQuery}`
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
}
