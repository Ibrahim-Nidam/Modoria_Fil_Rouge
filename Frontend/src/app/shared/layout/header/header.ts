import { Component, inject } from '@angular/core';
import { Button } from '../../ui/button/button';
import { InputComponent } from '../../ui/input/input';
import { ThemeService, Season } from '../../../core/theme/theme.service';

@Component({
    selector: 'app-header',
    standalone: true,
    imports: [Button, InputComponent],
    templateUrl: './header.html',
    styleUrl: './header.css',
})
export class Header {
    public themeService = inject(ThemeService);
    public availableSeasons: Season[] = ['spring', 'summer', 'autumn', 'winter'];

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
}
