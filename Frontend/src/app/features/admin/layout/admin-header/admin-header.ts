import { Component, output, inject } from '@angular/core';
import { ThemeService } from '../../../../core/theme/theme.service';
import { AuthService } from '../../../../core/auth/auth.service';

@Component({
  selector: 'app-admin-header',
  imports: [],
  templateUrl: './admin-header.html',
  styleUrl: './admin-header.css',
})
export class AdminHeader {
  toggleSidebar = output<void>();

  public themeService = inject(ThemeService);
  public authService = inject(AuthService);

  get userName(): string {
    return this.authService.currentUser()?.fullName ?? 'Admin';
  }

  onToggleDarkMode() {
    this.themeService.toggleDarkMode();
  }

  onToggleSidebar() {
    this.toggleSidebar.emit();
  }

  logout() {
    this.authService.logout();
  }
}
