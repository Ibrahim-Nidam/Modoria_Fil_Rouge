import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ShopProduct {
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

export interface ShopCategory {
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
export class ShopCatalogService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8081/api/v1';

  getSeasonProducts(season: string, page = 0, size = 60): Observable<PageResponse<ShopProduct>> {
    const seasonUpper = season.toUpperCase();
    return this.http.get<PageResponse<ShopProduct>>(
      `${this.baseUrl}/products/season/${seasonUpper}?page=${page}&size=${size}&sort=name,asc`
    );
  }

  getSeasonCategories(season: string, page = 0, size = 20): Observable<PageResponse<ShopCategory>> {
    const seasonUpper = season.toUpperCase();
    return this.http.get<PageResponse<ShopCategory>>(
      `${this.baseUrl}/categories?season=${seasonUpper}&page=${page}&size=${size}&sort=name,asc`
    );
  }

  getProductById(id: number): Observable<ShopProduct> {
    return this.http.get<ShopProduct>(`${this.baseUrl}/products/${id}`);
  }
}
