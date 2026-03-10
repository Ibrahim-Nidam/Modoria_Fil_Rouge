import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs';
import { Footer } from './shared/layout/footer/footer';
import { ToastContainer } from './shared/layout/toast-container/toast-container';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [CommonModule, RouterOutlet, Footer, ToastContainer],
    templateUrl: './app.html',
    styleUrl: './app.css'
})
export class App {
    private router = inject(Router);
    public isAuthPage = signal(false);

    constructor() {
        this.router.events.pipe(
            filter(event => event instanceof NavigationEnd)
        ).subscribe((event: any) => {
            this.isAuthPage.set(event.urlAfterRedirects.includes('/auth/'));
        });
    }
}
