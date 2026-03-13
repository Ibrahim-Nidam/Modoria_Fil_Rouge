import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { ToastService } from '../../../../core/toast/toast.service';
import { Button } from '../../../../shared/ui/button/button';
import { Modal } from '../../../../shared/ui/modal/modal';
import { UserForm, UserFormValue } from '../../components/user-form/user-form';
import { AdminUser, AdminUserPayload, AdminUserService } from '../../services/admin-user.service';

@Component({
  selector: 'app-admin-users',
  imports: [CommonModule, Button, Modal, UserForm],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.css',
})
export class AdminUsers implements OnInit {
  private userService = inject(AdminUserService);
  private toastService = inject(ToastService);

  users = signal<AdminUser[]>([]);
  loading = signal(true);
  formOpen = signal(false);
  deleteOpen = signal(false);
  submitting = signal(false);
  deleting = signal(false);
  selectedUser = signal<AdminUser | null>(null);

  selectedUserAsFormValue = computed((): UserFormValue | null => {
    const user = this.selectedUser();
    if (!user) return null;
    return {
      fullName: user.fullName,
      email: user.email,
      enabled: user.enabled,
      role: user.roles[0] ?? 'CLIENT',
    };
  });

  ngOnInit() {
    this.loadUsers();
  }

  get formTitle(): string {
    return this.selectedUser() ? 'Edit User' : 'Create User';
  }

  openCreateModal() {
    this.selectedUser.set(null);
    this.formOpen.set(true);
  }

  openEditModal(user: AdminUser) {
    this.selectedUser.set(user);
    this.formOpen.set(true);
  }

  openDeleteModal(user: AdminUser) {
    this.selectedUser.set(user);
    this.deleteOpen.set(true);
  }

  closeFormModal() {
    this.formOpen.set(false);
    this.selectedUser.set(null);
  }

  closeDeleteModal() {
    this.deleteOpen.set(false);
    this.selectedUser.set(null);
  }

  onFormModalChange(isOpen: boolean) {
    if (!isOpen) {
      this.closeFormModal();
    }
  }

  onDeleteModalChange(isOpen: boolean) {
    if (!isOpen) {
      this.closeDeleteModal();
    }
  }

  saveUser(payload: UserFormValue) {
    const selectedUser = this.selectedUser();
    const apiPayload: AdminUserPayload = {
      fullName: payload.fullName,
      email: payload.email,
      password: payload.password,
      enabled: payload.enabled,
      roles: [payload.role],
    };
    const request$ = selectedUser
      ? this.userService.updateUser(selectedUser.id, apiPayload)
      : this.userService.createUser(apiPayload as Required<AdminUserPayload>);

    this.submitting.set(true);

    request$
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: () => {
          this.toastService.success(
            selectedUser ? 'User updated successfully.' : 'User created successfully.',
            'Users'
          );
          this.closeFormModal();
          this.loadUsers();
        },
        error: (error) => {
          this.toastService.error(
            error.error?.message ?? 'Unable to save the user.',
            'Users'
          );
        },
      });
  }

  deleteUser() {
    const selectedUser = this.selectedUser();
    if (!selectedUser) {
      return;
    }

    this.deleting.set(true);

    this.userService
      .deleteUser(selectedUser.id)
      .pipe(finalize(() => this.deleting.set(false)))
      .subscribe({
        next: () => {
          this.toastService.success('User deleted successfully.', 'Users');
          this.closeDeleteModal();
          this.loadUsers();
        },
        error: (error) => {
          this.toastService.error(
            error.error?.message ?? 'Unable to delete the user.',
            'Users'
          );
        },
      });
  }

  toggleEnabled(user: AdminUser) {
    const payload: AdminUserPayload = {
      fullName: user.fullName,
      email: user.email,
      enabled: !user.enabled,
      roles: user.roles,
    };

    this.userService.updateUser(user.id, payload).subscribe({
      next: () => {
        this.toastService.success(
          `User ${!user.enabled ? 'enabled' : 'disabled'} successfully.`,
          'Users'
        );
        this.loadUsers();
      },
      error: (error) => {
        this.toastService.error(
          error.error?.message ?? 'Unable to update user status.',
          'Users'
        );
      },
    });
  }

  private loadUsers() {
    this.loading.set(true);

    this.userService
      .getUsers()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (users) => {
          this.users.set(users);
        },
        error: (error) => {
          this.toastService.error(
            error.error?.message ?? 'Unable to load users.',
            'Users'
          );
        },
      });
  }
}
