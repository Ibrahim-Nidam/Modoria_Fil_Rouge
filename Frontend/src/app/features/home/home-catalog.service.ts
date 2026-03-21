import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface HomeProduct {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  season: string | null;
  category: { id: number; name: string };
  primaryImagePath: string | null;
  images: { id: number; imagePath: string; primary: boolean }[];
}

export interface HomeCategory {
  id: number;
  name: string;
  description: string;
  season: string;
  imagePath?: string | null;
  productCount: number;
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

@Injectable({ providedIn: 'root' })
export class HomeCatalogService {
  private http = inject(HttpClient);
  private readonly baseUrl = '/api/v1';

  getSeasonProducts(season: string, page = 0, size = 12): Observable<PageResponse<HomeProduct>> {
    const seasonUpper = season.toUpperCase();
    return this.http.get<PageResponse<HomeProduct>>(
      `${this.baseUrl}/products/season/${seasonUpper}?page=${page}&size=${size}&sort=name,asc`
    );
  }

  getSeasonCategories(season: string, page = 0, size = 10): Observable<PageResponse<HomeCategory>> {
    const seasonUpper = season.toUpperCase();
    return this.http.get<PageResponse<HomeCategory>>(
      `${this.baseUrl}/categories?season=${seasonUpper}&page=${page}&size=${size}&sort=name,asc`
    );
  }
}

