import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { AuthService } from '../../../../core/auth/auth.service';
import { ToastService } from '../../../../core/toast/toast.service';
import { Button } from '../../../../shared/ui/button/button';
import { AdminUser, AdminUserService } from '../../services/admin-user.service';
import {
  AdminOrderDetails,
  AdminSupportTicket,
  AdminTicketService,
  SupportChatMessage,
} from '../../services/admin-ticket.service';

@Component({
  selector: 'app-admin-tickets',
  imports: [CommonModule, ReactiveFormsModule, Button],
  templateUrl: './admin-tickets.html',
})
export class AdminTickets implements OnInit {
  private ticketService = inject(AdminTicketService);
  private userService = inject(AdminUserService);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);
  private fb = inject(FormBuilder);

  tickets = signal<AdminSupportTicket[]>([]);
  agents = signal<AdminUser[]>([]);
  conversation = signal<SupportChatMessage[]>([]);
  selectedOrder = signal<AdminOrderDetails | null>(null);

  loading = signal(true);
  conversationLoading = signal(false);
  orderLoading = signal(false);
  assigningSessionId = signal<number | null>(null);
  replying = signal(false);
  updatingStatusSessionId = signal<number | null>(null);
  selectedTicketId = signal<number | null>(null);
  conversationPaneVisible = signal(true);
  selectedAgentBySession = signal<Record<number, number | null>>({});
  selectedStatusBySession = signal<Record<number, string>>({});

  readonly statusOptions = ['OPEN', 'IN_PROGRESS', 'RESOLVED'];

  readonly replyForm = this.fb.nonNullable.group({
    message: ['', [Validators.required, Validators.maxLength(4000)]],
  });

  readonly selectedTicket = computed(() => {
    const id = this.selectedTicketId();
    if (!id) {
      return null;
    }

    return this.tickets().find((ticket) => ticket.id === id) ?? null;
  });

  ngOnInit() {
    this.loadTickets();
    this.loadAgents();
  }

  selectTicket(ticket: AdminSupportTicket) {
    this.selectedTicketId.set(ticket.id);
    this.selectedStatusBySession.set({
      ...this.selectedStatusBySession(),
      [ticket.id]: ticket.status,
    });
    this.replyForm.reset({ message: '' });
    this.loadConversation(ticket.id);
    this.loadSelectedOrder(ticket.orderId ?? null);
    if (!this.conversationPaneVisible()) {
      this.conversationPaneVisible.set(true);
    }
  }

  toggleConversationPane() {
    this.conversationPaneVisible.set(!this.conversationPaneVisible());
  }

  setSelectedAgent(sessionId: number, value: string) {
    const parsed = Number(value);
    this.selectedAgentBySession.set({
      ...this.selectedAgentBySession(),
      [sessionId]: Number.isNaN(parsed) || parsed <= 0 ? null : parsed,
    });
  }

  setSelectedStatus(sessionId: number, value: string) {
    this.selectedStatusBySession.set({
      ...this.selectedStatusBySession(),
      [sessionId]: value,
    });
  }

  assignSelectedAgent(ticket: AdminSupportTicket) {
    const selectedAgent = this.selectedAgentBySession()[ticket.id];
    if (!selectedAgent) {
      this.toastService.warning('Select an agent first.', 'Tickets');
      return;
    }

    this.assigningSessionId.set(ticket.id);
    this.ticketService
      .assignAgent(ticket.id, selectedAgent)
      .pipe(finalize(() => this.assigningSessionId.set(null)))
      .subscribe({
        next: () => {
          this.toastService.success('Ticket assigned successfully.', 'Tickets');
          this.loadTickets();
        },
        error: (error) => {
          this.toastService.error(error.error?.message ?? 'Unable to assign ticket.', 'Tickets');
        },
      });
  }

  takeOwnership(ticket: AdminSupportTicket) {
    const currentUserId = this.authService.currentUser()?.id;
    if (!currentUserId) {
      this.toastService.error('Unable to identify current admin user.', 'Tickets');
      return;
    }

    this.assigningSessionId.set(ticket.id);
    this.ticketService
      .assignAgent(ticket.id, currentUserId)
      .pipe(finalize(() => this.assigningSessionId.set(null)))
      .subscribe({
        next: () => {
          this.toastService.success('Ticket assigned to you.', 'Tickets');
          this.loadTickets();
        },
        error: (error) => {
          this.toastService.error(error.error?.message ?? 'Unable to take ownership.', 'Tickets');
        },
      });
  }

  sendReply() {
    const ticket = this.selectedTicket();
    if (!ticket) {
      return;
    }

    this.replyForm.markAllAsTouched();
    if (this.replyForm.invalid) {
      return;
    }

    this.replying.set(true);
    this.ticketService
      .replyToTicket(ticket.id, this.replyForm.controls.message.value.trim())
      .pipe(finalize(() => this.replying.set(false)))
      .subscribe({
        next: () => {
          this.toastService.success('Reply sent to customer.', 'Tickets');
          this.replyForm.reset({ message: '' });
          this.loadConversation(ticket.id);
        },
        error: (error) => {
          this.toastService.error(error.error?.message ?? 'Unable to send reply.', 'Tickets');
        },
      });
  }

  updateStatus(ticket: AdminSupportTicket) {
    const status = this.selectedStatusBySession()[ticket.id] ?? ticket.status;
    if (!status || status === ticket.status) {
      return;
    }

    this.updatingStatusSessionId.set(ticket.id);
    this.ticketService
      .updateStatus(ticket.id, status)
      .pipe(finalize(() => this.updatingStatusSessionId.set(null)))
      .subscribe({
        next: () => {
          this.toastService.success('Ticket status updated.', 'Tickets');
          this.loadTickets();
        },
        error: (error) => {
          this.toastService.error(error.error?.message ?? 'Unable to update ticket status.', 'Tickets');
        },
      });
  }

  formatDate(value: string): string {
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? value : date.toLocaleString('fr-MA');
  }

  formatPrice(value: number): string {
    return new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' }).format(value ?? 0);
  }

  lineTotal(quantity: number, price: number): number {
    return (quantity ?? 0) * (price ?? 0);
  }

  private loadTickets() {
    this.loading.set(true);
    this.ticketService
      .getTickets()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (tickets) => {
          this.tickets.set(tickets);

          this.selectedStatusBySession.set(
            tickets.reduce<Record<number, string>>((acc, ticket) => {
              acc[ticket.id] = this.selectedStatusBySession()[ticket.id] ?? ticket.status;
              return acc;
            }, {}),
          );

          const selected = this.selectedTicketId();
          const selectedTicket = selected ? tickets.find((ticket) => ticket.id === selected) : null;

          if (!selectedTicket && tickets.length > 0) {
            this.selectTicket(tickets[0]);
            return;
          }

          if (selectedTicket) {
            this.loadConversation(selectedTicket.id);
            this.loadSelectedOrder(selectedTicket.orderId ?? null);
          }
        },
        error: (error) => {
          this.toastService.error(error.error?.message ?? 'Unable to load tickets.', 'Tickets');
          this.selectedOrder.set(null);
        },
      });
  }

  private loadAgents() {
    this.userService.getUsers().subscribe({
      next: (users) => {
        this.agents.set(users.filter((user) => user.roles.includes('AGENT')));
      },
      error: () => {
        this.agents.set([]);
      },
    });
  }

  private loadConversation(ticketId: number) {
    this.conversationLoading.set(true);
    this.ticketService
      .getConversation(ticketId)
      .pipe(finalize(() => this.conversationLoading.set(false)))
      .subscribe({
        next: (messages) => this.conversation.set(messages),
        error: () => this.conversation.set([]),
      });
  }

  private loadSelectedOrder(orderId: number | null) {
    if (!orderId) {
      this.selectedOrder.set(null);
      return;
    }

    this.orderLoading.set(true);
    this.ticketService
      .getOrderById(orderId)
      .pipe(finalize(() => this.orderLoading.set(false)))
      .subscribe({
        next: (order) => this.selectedOrder.set(order),
        error: () => this.selectedOrder.set(null),
      });
  }
}
