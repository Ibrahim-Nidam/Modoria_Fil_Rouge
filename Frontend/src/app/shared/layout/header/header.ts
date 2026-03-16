import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Button } from '../../ui/button/button';
import { ThemeService, Season } from '../../../core/theme/theme.service';
import { AuthService } from '../../../core/auth/auth.service';
import { ShopStateService } from '../../../features/shop/services/shop-state.service';

@Component({
    selector: 'app-header',
    standalone: true,
    imports: [Button, RouterLink],
    templateUrl: './header.html',
    styleUrl: './header.css',
})
export class Header {
    public themeService = inject(ThemeService);
    public authService = inject(AuthService);
    public shopState = inject(ShopStateService);
    private router = inject(Router);
    public availableSeasons: Season[] = ['spring', 'summer', 'autumn', 'winter'];
    public searchQuery = '';

    get logoLink(): string {
        if (this.authService.isAdmin()) {
            return '/admin';
        }

        if (this.authService.isAgent()) {
            return '/agent/tickets';
        }

        return '/home';
    }

    logout() {
        this.authService.logout();
    }

    onSeasonChange(event: Event) {
        const target = event.target as HTMLSelectElement;
        if (target && target.value) {
            this.themeService.setSeason(target.value as Season);
        }
    }

    toggleDarkMode() {
        this.themeService.toggleDarkMode();
    }

    get logoSource(): string {
        return this.themeService.isDarkMode() ? '/assets/logo-dark.png' : '/assets/logo-light.png';
    }

    get collectionName(): string {
        switch (this.themeService.activeSeason()) {
            case 'spring': return 'Spring Blossom';
            case 'summer': return 'Ocean Summer';
            case 'autumn': return 'Autumn Heritage';
            case 'winter': return 'Winter Noir';
            default: return 'Modoria Canonical';
        }
    }

    getCollectionIcon(season: Season): string {
        switch (season) {
            case 'spring': return 'local_florist';
            case 'summer': return 'wb_sunny';
            case 'autumn': return 'energy_savings_leaf';
            case 'winter': return 'ac_unit';
            default: return 'local_florist';
        }
    }

    get collectionIcon(): string {
        return this.getCollectionIcon(this.themeService.activeSeason());
    }

    onSearchInput(event: Event) {
        this.searchQuery = (event.target as HTMLInputElement).value;
    }

    onSearchSubmit() {
        const query = this.searchQuery.trim();
        this.router.navigate(['/catalog'], {
            queryParams: {
                q: query || null,
                category: null,
                section: null,
            }
        });
    }
}
