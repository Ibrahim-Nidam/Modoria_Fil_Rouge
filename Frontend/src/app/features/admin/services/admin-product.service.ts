import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type ProductSeason = 'SPRING' | 'SUMMER' | 'AUTUMN' | 'WINTER';

export interface AdminProductCategory {
  id: number;
  name: string;
  description?: string;
  productCount?: number;
}

export interface AdminProduct {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  season: ProductSeason | null;
  deleted: boolean;
  category: AdminProductCategory;
  primaryImagePath: string | null;
  images: AdminProductImage[];
}

export interface AdminProductImage {
  id: number;
  imagePath: string;
  primary: boolean;
}

export interface ProductPayload {
  name: string;
  description: string;
  price: number;
  stock: number;
  season: ProductSeason | null;
  categoryId: number;
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
  providedIn: 'root',
})
export class AdminProductService {
  private http = inject(HttpClient);
  private apiUrl = '/api/v1/products';

  getProducts(
    page: number = 0,
    size: number = 50,
    includeDeleted: boolean = false
  ): Observable<PageResponse<AdminProduct>> {
    return this.http.get<PageResponse<AdminProduct>>(
      `${this.apiUrl}?page=${page}&size=${size}&sort=name,asc&includeDeleted=${includeDeleted}`
    );
  }

  getProductsByCategory(
    categoryId: number,
    page: number = 0,
    size: number = 100,
    includeDeleted: boolean = false
  ): Observable<PageResponse<AdminProduct>> {
    return this.http.get<PageResponse<AdminProduct>>(
      `${this.apiUrl}/search?categoryId=${categoryId}&page=${page}&size=${size}&sort=name,asc&includeDeleted=${includeDeleted}`
    );
  }

  createProduct(payload: ProductPayload): Observable<AdminProduct> {
    return this.http.post<AdminProduct>(this.apiUrl, payload);
  }

  getProductById(id: number, includeDeleted: boolean = false): Observable<AdminProduct> {
    return this.http.get<AdminProduct>(`${this.apiUrl}/${id}?includeDeleted=${includeDeleted}`);
  }

  updateProduct(id: number, payload: ProductPayload): Observable<AdminProduct> {
    return this.http.put<AdminProduct>(`${this.apiUrl}/${id}`, payload);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  restoreProduct(id: number): Observable<AdminProduct> {
    return this.http.patch<AdminProduct>(`${this.apiUrl}/${id}/restore`, {});
  }

  uploadProductImages(
    id: number,
    files: File[],
    primaryIndex?: number | null
  ): Observable<AdminProductImage[]> {
    const formData = new FormData();
    for (const file of files) {
      formData.append('files', file);
    }

    if (primaryIndex !== null && primaryIndex !== undefined) {
      formData.append('primaryIndex', String(primaryIndex));
    }

    return this.http.post<AdminProductImage[]>(`${this.apiUrl}/${id}/images/upload`, formData);
  }

  deleteProductImage(productId: number, imageId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${productId}/images/${imageId}`);
  }

  setPrimaryProductImage(productId: number, imageId: number): Observable<AdminProduct> {
    return this.http.put<AdminProduct>(`${this.apiUrl}/${productId}/images/${imageId}/primary`, {});
  }
}
