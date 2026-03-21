import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserProfile {
  id: number;
  fullName: string;
  email: string;
  phoneNumber?: string | null;
  address?: string | null;
  roles: string[];
}

export interface UserProfileUpdatePayload {
  fullName: string;
  email: string;
  phoneNumber: string;
  address: string;
  password?: string;
}

export interface OrderHistoryItemProduct {
  id: number;
  name: string;
}

export interface OrderHistoryLineItem {
  id: number;
  product: OrderHistoryItemProduct;
  quantity: number;
  price: number;
}

export interface OrderHistoryItem {
  id: number;
  totalAmount: number;
  status: string;
  createdAt: string;
  updatedAt: string;
  items: OrderHistoryLineItem[];
}

export interface OpenSupportTicketPayload {
  orderId: number;
  subject: string;
  message: string;
}

export interface SupportTicket {
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

export interface SupportTicketMessage {
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

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/users/me';
  private readonly ordersUrl = '/api/v1/orders';
  private readonly supportUrl = '/api/v1/support/sessions';

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(this.apiUrl);
  }

  updateProfile(payload: UserProfileUpdatePayload): Observable<UserProfile> {
    return this.http.put<UserProfile>(this.apiUrl, payload);
  }

  deleteProfile(): Observable<void> {
    return this.http.delete<void>(this.apiUrl);
  }

  getOrderHistory(): Observable<OrderHistoryItem[]> {
    return this.http.get<OrderHistoryItem[]>(this.ordersUrl);
  }

  openSupportTicket(payload: OpenSupportTicketPayload): Observable<SupportTicket> {
    return this.http.post<SupportTicket>(`${this.supportUrl}/tickets/open`, payload);
  }

  getMySupportTicket(): Observable<SupportTicket | null> {
    return this.http.get<SupportTicket | null>(`${this.supportUrl}/tickets/mine`);
  }

  getMySupportTickets(): Observable<SupportTicket[]> {
    return this.http.get<SupportTicket[]>(`${this.supportUrl}/tickets/mine/all`);
  }

  getTicketConversation(sessionId: number): Observable<SupportTicketMessage[]> {
    return this.http.get<SupportTicketMessage[]>(`${this.supportUrl}/tickets/${sessionId}/conversation`);
  }

  replyToTicket(sessionId: number, message: string): Observable<SupportTicketMessage> {
    return this.http.post<SupportTicketMessage>(`${this.supportUrl}/${sessionId}/reply`, { message });
  }
}

