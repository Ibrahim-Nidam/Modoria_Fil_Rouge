import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AdminDashboardStats {
  totalOrders: number;
  completedOrders: number;
  pendingOrders: number;
  cancelledOrders: number;
  ordersThisMonth: number;
  completedSalesTotal: number;
  completedSalesThisMonth: number;
  totalTickets: number;
  openTickets: number;
  inProgressTickets: number;
  resolvedTickets: number;
  unassignedTickets: number;
  ticketsThisMonth: number;
}

@Injectable({ providedIn: 'root' })
export class AdminDashboardService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8081/api/v1/admin/dashboard';

  getStats(): Observable<AdminDashboardStats> {
    return this.http.get<AdminDashboardStats>(`${this.baseUrl}/stats`);
  }
}
