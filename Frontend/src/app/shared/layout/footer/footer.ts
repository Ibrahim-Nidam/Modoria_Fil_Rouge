import { Component, inject } from '@angular/core';
import { ThemeService } from '../../../core/theme/theme.service';

@Component({
    selector: 'app-footer',
    standalone: true,
    imports: [],
    templateUrl: './footer.html',
    styleUrl: './footer.css',
})
export class Footer {
    public themeService = inject(ThemeService);
    currentYear = new Date().getFullYear();

    get seasonalSlogan(): string {
        switch (this.themeService.activeSeason()) {
            case 'spring': return 'Bloom with Modoria Heritage.';
            case 'summer': return 'Elegance in the Summer Breeze.';
            case 'autumn': return 'Autumn Textures, Timeless Style.';
            case 'winter': return 'Chilled Sophistication.';
            default: return 'Luxury in every season.';
        }
    }
}
