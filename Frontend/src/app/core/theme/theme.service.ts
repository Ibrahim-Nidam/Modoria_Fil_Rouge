import { Injectable, signal, effect, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export type Season = 'spring' | 'summer' | 'autumn' | 'winter';

@Injectable({
    providedIn: 'root'
})
export class ThemeService {
    private http = inject(HttpClient);
    private readonly darkModeStorageKey = 'modoria_dark_mode';

    public activeSeason = signal<Season>('spring');
    public isDarkMode = signal<boolean>(false);

    constructor() {
        this.initializeTheme();

        effect(() => {
            this.applyTheme(this.activeSeason(), this.isDarkMode());
        });
    }

    private initializeTheme() {
        const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
        const savedDarkMode = this.readDarkModePreference();

        // 1. Use persisted user preference if available, otherwise fallback to OS preference.
        if (savedDarkMode !== null) {
            this.isDarkMode.set(savedDarkMode);
        } else if (mediaQuery.matches) {
            this.isDarkMode.set(true);
        }

        // Keep following OS changes only when user has not explicitly chosen a preference.
        mediaQuery.addEventListener('change', event => {
            if (this.readDarkModePreference() === null) {
                this.isDarkMode.set(event.matches);
            }
        });

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
        this.isDarkMode.update(dark => {
            const next = !dark;
            this.persistDarkModePreference(next);
            return next;
        });
    }

    private readDarkModePreference(): boolean | null {
        const savedPreference = localStorage.getItem(this.darkModeStorageKey);
        if (savedPreference === 'true') {
            return true;
        }
        if (savedPreference === 'false') {
            return false;
        }
        return null;
    }

    private persistDarkModePreference(isDarkMode: boolean) {
        localStorage.setItem(this.darkModeStorageKey, String(isDarkMode));
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
