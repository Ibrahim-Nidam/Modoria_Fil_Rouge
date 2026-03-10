import { Injectable, signal, computed } from '@angular/core';

export type ToastType = 'success' | 'error' | 'info' | 'warning';

export interface ToastMessage {
    id: string;
    type: ToastType;
    message: string;
    title?: string;
}

@Injectable({
    providedIn: 'root'
})
export class ToastService {
    private toasts = signal<ToastMessage[]>([]);
    public readonly toastsSignal = computed(() => this.toasts());

    show(message: string, type: ToastType = 'info', title?: string, duration: number = 5000) {
        const id = Math.random().toString(36).substring(2, 9);
        this.toasts.update(current => [...current, { id, type, message, title }]);

        if (duration > 0) {
            setTimeout(() => this.remove(id), duration);
        }
    }

    success(message: string, title?: string, duration?: number) {
        this.show(message, 'success', title, duration);
    }

    error(message: string, title?: string, duration?: number) {
        this.show(message, 'error', title, duration);
    }

    warning(message: string, title?: string, duration?: number) {
        this.show(message, 'warning', title, duration);
    }

    info(message: string, title?: string, duration?: number) {
        this.show(message, 'info', title, duration);
    }

    remove(id: string) {
        this.toasts.update(current => current.filter(t => t.id !== id));
    }
}
