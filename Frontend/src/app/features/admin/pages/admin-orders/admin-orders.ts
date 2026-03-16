import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { ToastService } from '../../../../core/toast/toast.service';
import { Button } from '../../../../shared/ui/button/button';
import { AdminOrder, AdminOrderService, AdminOrderStatus } from '../../services/admin-order.service';

@Component({
  selector: 'app-admin-orders',
  imports: [CommonModule, Button],
  templateUrl: './admin-orders.html',
})
export class AdminOrders implements OnInit {
  private orderService = inject(AdminOrderService);
  private toastService = inject(ToastService);

  readonly orders = signal<AdminOrder[]>([]);
  readonly loading = signal(true);
  readonly updatingOrderId = signal<number | null>(null);
  readonly selectedOrderId = signal<number | null>(null);
  readonly selectedStatusByOrder = signal<Record<number, AdminOrderStatus>>({});

  readonly statusOptions: AdminOrderStatus[] = ['PENDING', 'COMPLETED', 'CANCELLED'];

  readonly selectedOrder = computed(() => {
    const id = this.selectedOrderId();
    if (!id) {
      return null;
    }

    return this.orders().find((order) => order.id === id) ?? null;
  });

  ngOnInit() {
    this.loadOrders();
  }

  selectOrder(order: AdminOrder) {
    this.selectedOrderId.set(order.id);
    this.selectedStatusByOrder.set({
      ...this.selectedStatusByOrder(),
      [order.id]: order.status,
    });
  }

  setSelectedStatus(orderId: number, status: string) {
    this.selectedStatusByOrder.set({
      ...this.selectedStatusByOrder(),
      [orderId]: status as AdminOrderStatus,
    });
  }

  updateStatus(order: AdminOrder) {
    const status = this.selectedStatusByOrder()[order.id] ?? order.status;
    if (status === order.status) {
      return;
    }

    this.updatingOrderId.set(order.id);
    this.orderService
      .updateOrderStatus(order.id, status)
      .pipe(finalize(() => this.updatingOrderId.set(null)))
      .subscribe({
        next: (updatedOrder) => {
          this.orders.set(this.orders().map((current) => current.id === updatedOrder.id ? updatedOrder : current));
          if (this.selectedOrderId() === updatedOrder.id) {
            this.selectedOrderId.set(updatedOrder.id);
          }
          this.selectedStatusByOrder.set({
            ...this.selectedStatusByOrder(),
            [updatedOrder.id]: updatedOrder.status,
          });
          this.toastService.success('Order status updated.', 'Orders');
        },
        error: (error) => {
          this.toastService.error(error.error?.message ?? 'Unable to update order status.', 'Orders');
        },
      });
  }

  formatPrice(value: number): string {
    return new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' }).format(value ?? 0);
  }

  formatDate(value: string): string {
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? value : date.toLocaleString('fr-MA');
  }

  lineTotal(quantity: number, price: number): number {
    return (quantity ?? 0) * (price ?? 0);
  }

  private loadOrders() {
    this.loading.set(true);
    this.orderService
      .getOrders()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (orders) => {
          this.orders.set(orders);
          this.selectedStatusByOrder.set(
            orders.reduce<Record<number, AdminOrderStatus>>((acc, order) => {
              acc[order.id] = this.selectedStatusByOrder()[order.id] ?? order.status;
              return acc;
            }, {}),
          );

          const selectedId = this.selectedOrderId();
          const selectedOrder = selectedId ? orders.find((order) => order.id === selectedId) : null;
          if (!selectedOrder && orders.length > 0) {
            this.selectOrder(orders[0]);
          }
        },
        error: (error) => {
          this.toastService.error(error.error?.message ?? 'Unable to load orders.', 'Orders');
        },
      });
  }
}