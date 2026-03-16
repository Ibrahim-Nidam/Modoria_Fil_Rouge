import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/toast/toast.service';
import { Header } from '../../shared/layout/header/header';
import {
  OrderHistoryItem,
  ProfileService,
  SupportTicket,
  SupportTicketMessage,
  UserProfile,
} from './profile.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, Header],
  templateUrl: './profile.html',
})
export class ProfileComponent {
  readonly authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private profileService = inject(ProfileService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  readonly loading = signal(true);
  readonly submitting = signal(false);
  readonly deleting = signal(false);
  readonly profile = signal<UserProfile | null>(null);

  readonly activeTab = signal<'account' | 'orders' | 'support'>('account');
  readonly orderHistoryLoading = signal(false);
  readonly orders = signal<OrderHistoryItem[]>([]);
  readonly expandedOrderIds = signal<number[]>([]);

  readonly ticketComposerOpen = signal(false);
  readonly ticketOrderId = signal<number | null>(null);
  readonly ticketSubmitting = signal(false);
  readonly ticketsLoading = signal(false);
  readonly tickets = signal<SupportTicket[]>([]);
  readonly selectedTicketId = signal<number | null>(null);
  readonly conversationLoading = signal(false);
  readonly conversation = signal<SupportTicketMessage[]>([]);
  readonly replyingToTicket = signal(false);

  readonly form = this.fb.nonNullable.group({
    fullName: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
    phoneNumber: ['', [Validators.maxLength(20)]],
    address: ['', [Validators.maxLength(255)]],
    password: ['', [Validators.minLength(8), Validators.maxLength(128)]],
  });

  readonly ticketForm = this.fb.nonNullable.group({
    subject: ['', [Validators.required, Validators.maxLength(150)]],
    message: ['', [Validators.required, Validators.maxLength(4000)]],
  });

  readonly ticketReplyForm = this.fb.nonNullable.group({
    message: ['', [Validators.required, Validators.maxLength(4000)]],
  });

  readonly isAgentUser = computed(() => this.hasRole('AGENT'));
  readonly canViewOrders = computed(() => !this.isAgentUser());
  readonly canOpenTickets = computed(() => !this.isAgentUser());
  readonly canDeleteAccount = computed(() => !this.isAgentUser());

  readonly currentTicket = computed(() => {
    const selectedId = this.selectedTicketId();
    const all = this.tickets();
    if (all.length === 0) {
      return null;
    }

    if (!selectedId) {
      return all[0] ?? null;
    }

    return all.find((ticket) => ticket.id === selectedId) ?? all[0] ?? null;
  });

  constructor() {
    if (this.authService.currentUser()) {
      this.loadProfile();
      if (this.canViewOrders()) {
        this.loadOrderHistory();
      }
      if (this.canOpenTickets()) {
        this.loadTickets();
      }
    } else {
      this.loading.set(false);
    }
  }

  setActiveTab(tab: 'account' | 'orders' | 'support') {
    if (tab === 'orders' && !this.canViewOrders()) {
      return;
    }

    if (tab === 'support' && !this.canOpenTickets()) {
      return;
    }

    this.activeTab.set(tab);
  }

  loadOrderHistory() {
    if (!this.canViewOrders()) {
      this.orders.set([]);
      return;
    }

    this.orderHistoryLoading.set(true);
    this.profileService.getOrderHistory().subscribe({
      next: (orders) => {
        this.orders.set(orders ?? []);
        this.orderHistoryLoading.set(false);
      },
      error: (error) => {
        this.orderHistoryLoading.set(false);
        this.toastService.error(error.error?.message ?? 'Unable to load order history.', 'Orders');
      },
    });
  }

  loadTickets() {
    if (!this.canOpenTickets()) {
      this.tickets.set([]);
      return;
    }

    this.ticketsLoading.set(true);
    this.profileService.getMySupportTickets().subscribe({
      next: (tickets) => {
        this.ticketsLoading.set(false);
        this.tickets.set(tickets ?? []);

        const selected = this.selectedTicketId();
        const nextSelected = selected && (tickets ?? []).some((ticket) => ticket.id === selected)
          ? selected
          : (tickets?.[0]?.id ?? null);
        this.selectedTicketId.set(nextSelected);

        if (nextSelected) {
          this.loadConversation(nextSelected);
        } else {
          this.conversation.set([]);
        }
      },
      error: () => {
        this.ticketsLoading.set(false);
        this.tickets.set([]);
        this.conversation.set([]);
      },
    });
  }

  selectTicket(ticketId: number) {
    this.selectedTicketId.set(ticketId);
    this.ticketReplyForm.reset({ message: '' });
    this.loadConversation(ticketId);
  }

  loadConversation(ticketId: number) {
    this.conversationLoading.set(true);
    this.profileService.getTicketConversation(ticketId).subscribe({
      next: (messages) => {
        this.conversationLoading.set(false);
        this.conversation.set(messages ?? []);
      },
      error: () => {
        this.conversationLoading.set(false);
        this.conversation.set([]);
      },
    });
  }

  toggleOrderExpanded(orderId: number) {
    const current = this.expandedOrderIds();
    if (current.includes(orderId)) {
      this.expandedOrderIds.set(current.filter((id) => id !== orderId));
      return;
    }

    this.expandedOrderIds.set([...current, orderId]);
  }

  isOrderExpanded(orderId: number): boolean {
    return this.expandedOrderIds().includes(orderId);
  }

  openTicketComposer(order: OrderHistoryItem) {
    if (!this.canOpenTickets()) {
      return;
    }

    this.ticketOrderId.set(order.id);
    this.ticketComposerOpen.set(true);
    this.ticketReplyForm.reset({ message: '' });

    this.ticketForm.patchValue({
      subject: `Order #${order.id} support request`,
    });
  }

  closeTicketComposer() {
    this.ticketComposerOpen.set(false);
    this.ticketOrderId.set(null);
    this.ticketForm.reset({ subject: '', message: '' });
  }

  submitTicket() {
    if (!this.canOpenTickets()) {
      return;
    }

    this.ticketForm.markAllAsTouched();
    if (this.ticketForm.invalid) {
      return;
    }

    const orderId = this.ticketOrderId();
    if (!orderId) {
      this.toastService.warning('Select an order to open a support ticket.', 'Support');
      return;
    }

    this.ticketSubmitting.set(true);
    this.profileService
      .openSupportTicket({
        orderId,
        subject: this.ticketForm.controls.subject.value.trim(),
        message: this.ticketForm.controls.message.value.trim(),
      })
      .subscribe({
        next: (ticket) => {
          this.ticketSubmitting.set(false);
          this.closeTicketComposer();
          this.activeTab.set('support');
          this.loadTickets();
          this.selectedTicketId.set(ticket.id);
          this.loadConversation(ticket.id);
          this.toastService.success('Support ticket submitted. An agent will follow up soon.', 'Support');
        },
        error: (error) => {
          this.ticketSubmitting.set(false);
          this.toastService.error(error.error?.message ?? 'Unable to open support ticket.', 'Support');
        },
      });
  }

  sendTicketReply() {
    if (!this.canOpenTickets()) {
      return;
    }

    const ticket = this.currentTicket();
    if (!ticket) {
      return;
    }

    this.ticketReplyForm.markAllAsTouched();
    if (this.ticketReplyForm.invalid) {
      return;
    }

    this.replyingToTicket.set(true);
    this.profileService.replyToTicket(ticket.id, this.ticketReplyForm.controls.message.value.trim()).subscribe({
      next: () => {
        this.replyingToTicket.set(false);
        this.ticketReplyForm.reset({ message: '' });
        this.loadConversation(ticket.id);
      },
      error: (error) => {
        this.replyingToTicket.set(false);
        this.toastService.error(error.error?.message ?? 'Unable to send ticket reply.', 'Support');
      },
    });
  }

  orderHasTicket(orderId: number): boolean {
    return this.tickets().some((ticket) => ticket.orderId === orderId);
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' }).format(price ?? 0);
  }

  formatDate(value: string): string {
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? value : date.toLocaleString('fr-MA');
  }

  lineTotal(quantity: number, unitPrice: number): number {
    return (quantity ?? 0) * (unitPrice ?? 0);
  }

  logout() {
    this.authService.logout();
  }

  loadProfile() {
    this.loading.set(true);
    this.profileService.getProfile().subscribe({
      next: (p) => {
        this.profile.set(p);
        this.form.reset({
          fullName: p.fullName ?? '',
          email: p.email ?? '',
          phoneNumber: p.phoneNumber ?? '',
          address: p.address ?? '',
          password: '',
        });
        this.loading.set(false);
      },
      error: (error) => {
        this.loading.set(false);
        this.toastService.error(error.error?.message ?? 'Unable to load profile.', 'Profile');
      },
    });
  }

  saveProfile() {
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      return;
    }

    this.submitting.set(true);

    const currentEmail = this.profile()?.email ?? this.authService.currentUser()?.email ?? '';
    const nextEmail = this.form.controls.email.value.trim();
    const changingEmail = nextEmail !== currentEmail;
    const changingPassword = this.form.controls.password.value.trim().length > 0;

    this.profileService
      .updateProfile({
        fullName: this.form.controls.fullName.value.trim(),
        email: nextEmail,
        phoneNumber: this.form.controls.phoneNumber.value.trim(),
        address: this.form.controls.address.value.trim(),
        password: this.form.controls.password.value.trim() || undefined,
      })
      .subscribe({
        next: (p) => {
          this.profile.set(p);
          const current = this.authService.currentUser();
          if (current) {
            const nextUser = { ...current, fullName: p.fullName, email: p.email, roles: p.roles };
            this.authService.currentUser.set(nextUser);
            localStorage.setItem('modoria_user', JSON.stringify(nextUser));
          }

          this.submitting.set(false);
          this.form.controls.password.setValue('');

          if (changingEmail || changingPassword) {
            this.toastService.success('Profile updated. Please sign in again after changing credentials.', 'Profile');
            this.authService.logout();
            return;
          }

          this.toastService.success('Your profile has been updated.', 'Profile');
        },
        error: (error) => {
          this.submitting.set(false);
          this.toastService.error(error.error?.message ?? 'Unable to update profile.', 'Profile');
        },
      });
  }

  deleteAccount() {
    if (!this.canDeleteAccount()) {
      this.toastService.warning('Account deletion is not available for agent accounts.', 'Profile');
      return;
    }

    const confirmed = window.confirm('Delete your account permanently? This action cannot be undone.');
    if (!confirmed) {
      return;
    }

    this.deleting.set(true);
    this.profileService.deleteProfile().subscribe({
      next: () => {
        this.deleting.set(false);
        this.toastService.warning('Your account has been deleted.', 'Profile');
        localStorage.removeItem('modoria_token');
        localStorage.removeItem('modoria_refresh_token');
        localStorage.removeItem('modoria_user');
        this.authService.currentUser.set(null);
        this.router.navigate(['/home']);
      },
      error: (error) => {
        this.deleting.set(false);
        this.toastService.error(error.error?.message ?? 'Unable to delete account.', 'Profile');
      },
    });
  }

  private hasRole(expectedRole: string): boolean {
    const roles = this.authService.currentUser()?.roles as Array<{ name?: string } | string> | undefined;
    const expected = this.normalizeRoleName(expectedRole);
    return (
      roles?.some((role) => {
        const roleName = typeof role === 'string' ? role : role?.name;
        return !!roleName && this.normalizeRoleName(roleName) === expected;
      }) ?? false
    );
  }

  private normalizeRoleName(roleName: string): string {
    const normalized = roleName.trim().toUpperCase();
    return normalized.startsWith('ROLE_') ? normalized.slice(5) : normalized;
  }
}
