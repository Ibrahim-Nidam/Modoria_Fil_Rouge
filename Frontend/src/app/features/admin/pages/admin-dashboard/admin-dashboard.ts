import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { StatCard } from '../../components/stat-card/stat-card';

@Component({
  selector: 'app-admin-dashboard',
  imports: [StatCard, RouterLink],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard {
  stats = [
    { title: 'Total Users', value: '1,248', icon: 'group', trend: '+12% this month', trendUp: true },
    { title: 'Total Products', value: '356', icon: 'inventory_2', trend: '+5 new', trendUp: true },
    { title: 'Total Categories', value: '24', icon: 'category', trend: '', trendUp: null },
    { title: 'Total Orders', value: '892', icon: 'shopping_cart', trend: '+8% this week', trendUp: true },
    { title: 'Revenue', value: '$48,290', icon: 'payments', trend: '+15% vs last month', trendUp: true },
    { title: 'Pending Reviews', value: '37', icon: 'rate_review', trend: '-3 resolved', trendUp: false },
  ];
}
