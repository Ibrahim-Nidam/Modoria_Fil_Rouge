import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ThemeService, Season } from '../../../core/theme/theme.service';
import { ShopStateService } from '../../../features/shop/services/shop-state.service';

@Component({
    selector: 'app-auth-header',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './auth-header.html',
    styleUrl: './auth-header.css'
})
export class AuthHeader {
    public themeService = inject(ThemeService);
    public shopState = inject(ShopStateService);
    private router = inject(Router);
    public availableSeasons: Season[] = ['spring', 'summer', 'autumn', 'winter'];
    public searchQuery = '';

    onSeasonChange(event: Event) {
        const target = event.target as HTMLSelectElement;
        if (target && target.value) {
            this.themeService.setSeason(target.value as Season);
        }
    }

    get logoSource(): string {
        return this.themeService.isDarkMode() ? '/assets/logo-dark.png' : '/assets/logo-light.png';
    }

    toggleDarkMode() {
        this.themeService.toggleDarkMode();
    }

    get isDark(): boolean {
        return this.themeService.isDarkMode();
    }

    onSearchInput(event: Event) {
        this.searchQuery = (event.target as HTMLInputElement).value;
    }

    onSearchSubmit() {
        const query = this.searchQuery.trim();
        this.router.navigate(['/catalog'], {
            queryParams: {
                q: query || null,
            }
        });
    }
}
