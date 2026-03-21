import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AdminSupportTicket {
  id: number;
  customerId: number;
  customerName: string;
  agentId?: number | null;
  agentName?: string | null;
  orderId?: number | null;
  orderTotal?: number | null;
  orderStatus?: string | null;
  orderCreatedAt?: string | null;
  subject?: string | null;
  initialMessage?: string | null;
  status: string;
  resolvedById?: number | null;
  resolvedByName?: string | null;
  closedAt?: string | null;
  resolutionMinutes?: number | null;
  createdAt: string;
  updatedAt: string;
}

export interface SupportChatMessage {
  id: number;
  supportSessionId?: number | null;
  senderId: number;
  senderName: string;
  receiverId: number;
  receiverName: string;
  content: string;
  timestamp: string;
  status: string;
}

export interface AdminOrderLineProduct {
  id: number;
  name: string;
}

export interface AdminOrderLineItem {
  id: number;
  product: AdminOrderLineProduct;
  quantity: number;
  price: number;
}

export interface AdminOrderDetails {
  id: number;
  totalAmount: number;
  status: string;
  createdAt: string;
  updatedAt: string;
  items: AdminOrderLineItem[];
}

@Injectable({
  providedIn: 'root',
})
export class AdminTicketService {
  private http = inject(HttpClient);
  private readonly supportBaseUrl = '/api/v1/support/sessions';
  private readonly adminOrdersBaseUrl = '/api/v1/admin/orders';

  getTickets(): Observable<AdminSupportTicket[]> {
    return this.http.get<AdminSupportTicket[]>(`${this.supportBaseUrl}/tickets`);
  }

  getAssignedTickets(): Observable<AdminSupportTicket[]> {
    return this.http.get<AdminSupportTicket[]>(`${this.supportBaseUrl}/tickets/assigned`);
  }

  assignAgent(sessionId: number, agentId: number): Observable<AdminSupportTicket> {
    return this.http.post<AdminSupportTicket>(`${this.supportBaseUrl}/${sessionId}/assign/${agentId}`, {});
  }

  replyToTicket(sessionId: number, message: string): Observable<SupportChatMessage> {
    return this.http.post<SupportChatMessage>(`${this.supportBaseUrl}/${sessionId}/reply`, { message });
  }

  getConversation(sessionId: number): Observable<SupportChatMessage[]> {
    return this.http.get<SupportChatMessage[]>(`${this.supportBaseUrl}/tickets/${sessionId}/conversation`);
  }

  updateStatus(sessionId: number, status: string): Observable<AdminSupportTicket> {
    return this.http.patch<AdminSupportTicket>(`${this.supportBaseUrl}/${sessionId}/status`, { status });
  }

  getOrderById(orderId: number): Observable<AdminOrderDetails> {
    return this.http.get<AdminOrderDetails>(`${this.adminOrdersBaseUrl}/${orderId}`);
  }
}

