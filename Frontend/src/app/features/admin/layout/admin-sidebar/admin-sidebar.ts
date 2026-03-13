import { Component, input, output, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { ThemeService } from '../../../../core/theme/theme.service';

export interface SidebarItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-admin-sidebar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './admin-sidebar.html',
  styleUrl: './admin-sidebar.css',
})
export class AdminSidebar {
  collapsed = input<boolean>(false);
  closeMobile = output<void>();

  private themeService = inject(ThemeService);

  navItems: SidebarItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/admin' },
    { label: 'Categories', icon: 'category', route: '/admin/categories' },
    { label: 'Products', icon: 'inventory_2', route: '/admin/products' },
    { label: 'Users', icon: 'group', route: '/admin/users' },
  ];

  get logoSource(): string {
    return this.themeService.isDarkMode() ? '/assets/logo-dark.png' : '/assets/logo-light.png';
  }

  onNavClick() {
    this.closeMobile.emit();
  }
}
