import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { Header } from '../../../../shared/layout/header/header';
import { ToastService } from '../../../../core/toast/toast.service';
import { AdminSupportTicket, AdminTicketService, SupportChatMessage } from '../../../admin/services/admin-ticket.service';

@Component({
  selector: 'app-agent-tickets',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, Header],
  templateUrl: './agent-tickets.html',
})
export class AgentTicketsComponent implements OnInit {
  private ticketService = inject(AdminTicketService);
  private toastService = inject(ToastService);
  private fb = inject(FormBuilder);

  readonly loading = signal(true);
  readonly tickets = signal<AdminSupportTicket[]>([]);
  readonly selectedTicketId = signal<number | null>(null);
  readonly conversationLoading = signal(false);
  readonly conversation = signal<SupportChatMessage[]>([]);
  readonly replying = signal(false);
  readonly updatingStatus = signal(false);

  readonly statusOptions = ['OPEN', 'IN_PROGRESS', 'RESOLVED'];
  readonly statusValue = signal('IN_PROGRESS');

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

  ngOnInit(): void {
    this.loadAssignedTickets();
  }

  loadAssignedTickets() {
    this.loading.set(true);
    this.ticketService
      .getAssignedTickets()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (tickets) => {
          this.tickets.set(tickets ?? []);
          const selected = this.selectedTicketId();
          const active = selected ? tickets.find((ticket) => ticket.id === selected) : null;
          const nextSelected = active?.id ?? tickets?.[0]?.id ?? null;
          this.selectedTicketId.set(nextSelected);

          if (nextSelected) {
            const selectedTicket = tickets.find((ticket) => ticket.id === nextSelected);
            this.statusValue.set(selectedTicket?.status ?? 'IN_PROGRESS');
            this.loadConversation(nextSelected);
          } else {
            this.conversation.set([]);
          }
        },
        error: (error) => {
          this.toastService.error(error.error?.message ?? 'Unable to load assigned tickets.', 'Agent Desk');
        },
      });
  }

  selectTicket(ticket: AdminSupportTicket) {
    this.selectedTicketId.set(ticket.id);
    this.statusValue.set(ticket.status);
    this.replyForm.reset({ message: '' });
    this.loadConversation(ticket.id);
  }

  setStatus(value: string) {
    this.statusValue.set(value);
  }

  updateTicketStatus() {
    const ticket = this.selectedTicket();
    if (!ticket) {
      return;
    }

    const status = this.statusValue();
    if (!status || status === ticket.status) {
      return;
    }

    this.updatingStatus.set(true);
    this.ticketService
      .updateStatus(ticket.id, status)
      .pipe(finalize(() => this.updatingStatus.set(false)))
      .subscribe({
        next: () => {
          this.toastService.success('Ticket status updated.', 'Agent Desk');
          this.loadAssignedTickets();
        },
        error: (error) => {
          this.toastService.error(error.error?.message ?? 'Unable to update ticket status.', 'Agent Desk');
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
          this.replyForm.reset({ message: '' });
          this.loadConversation(ticket.id);
        },
        error: (error) => {
          this.toastService.error(error.error?.message ?? 'Unable to send reply.', 'Agent Desk');
        },
      });
  }

  formatDate(value: string): string {
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? value : date.toLocaleString('fr-MA');
  }

  private loadConversation(ticketId: number) {
    this.conversationLoading.set(true);
    this.ticketService
      .getConversation(ticketId)
      .pipe(finalize(() => this.conversationLoading.set(false)))
      .subscribe({
        next: (messages) => this.conversation.set(messages ?? []),
        error: () => this.conversation.set([]),
      });
  }
}
