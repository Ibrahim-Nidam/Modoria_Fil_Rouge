import { Injectable, signal, effect, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export type Season = 'spring' | 'summer' | 'autumn' | 'winter';

@Injectable({
    providedIn: 'root'
})
export class ThemeService {
    private http = inject(HttpClient);

    // Signals for state management
    public activeSeason = signal<Season>('spring'); // Default fallback
    public isDarkMode = signal<boolean>(false);

    constructor() {
        this.initializeTheme();

        // Reactively update the <html class="..."> whenever season or dark mode changes
        effect(() => {
            this.applyTheme(this.activeSeason(), this.isDarkMode());
        });
    }

    private initializeTheme() {
        // 1. Check system preference for dark mode
        if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
            this.isDarkMode.set(true);
        }

        // Listen for OS dark mode changes
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', event => {
            this.isDarkMode.set(event.matches);
        });

        // 2. Fetch current season from backend
        const apiUrl = 'http://localhost:8081/api/v1';
        this.http.get<{ season: Season }>(`${apiUrl}/seasons/current`).subscribe({
            next: (res) => {
                if (res && res.season) {
                    this.activeSeason.set(res.season);
                }
            },
            error: (err) => {
                console.warn('Could not fetch active season from backend, defaulting to spring.', err);
            }
        });
    }

    public setSeason(season: Season) {
        this.activeSeason.set(season);
    }

    public toggleDarkMode() {
        this.isDarkMode.update(dark => !dark);
    }

    private applyTheme(season: Season, isDark: boolean) {
        const htmlElement = document.documentElement;

        // Remove all existing theme classes
        htmlElement.classList.remove('theme-spring', 'theme-summer', 'theme-autumn', 'theme-winter');

        // Add the active season class
        htmlElement.classList.add(`theme-${season}`);

        // Toggle dark mode
        if (isDark) {
            htmlElement.classList.add('dark');
        } else {
            htmlElement.classList.remove('dark');
        }
    }
}
