import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThemeService, Season } from '../../../core/theme/theme.service';

@Component({
    selector: 'app-auth-header',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './auth-header.html',
    styleUrl: './auth-header.css'
})
export class AuthHeader {
    public themeService = inject(ThemeService);
    public availableSeasons: Season[] = ['spring', 'summer', 'autumn', 'winter'];

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
}
