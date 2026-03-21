import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type AdminOrderStatus = 'PENDING' | 'COMPLETED' | 'CANCELLED';

export interface AdminOrderProduct {
  id: number;
  name: string;
}

export interface AdminOrderItem {
  id: number;
  product: AdminOrderProduct;
  quantity: number;
  price: number;
}

export interface AdminOrder {
  id: number;
  customerId: number;
  customerName: string;
  customerEmail: string;
  totalAmount: number;
  status: AdminOrderStatus;
  createdAt: string;
  updatedAt: string;
  items: AdminOrderItem[];
}

@Injectable({
  providedIn: 'root',
})
export class AdminOrderService {
  private http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/admin/orders';

  getOrders(): Observable<AdminOrder[]> {
    return this.http.get<AdminOrder[]>(this.apiUrl);
  }

  updateOrderStatus(id: number, status: AdminOrderStatus): Observable<AdminOrder> {
    return this.http.patch<AdminOrder>(`${this.apiUrl}/${id}/status`, { status });
  }
}
