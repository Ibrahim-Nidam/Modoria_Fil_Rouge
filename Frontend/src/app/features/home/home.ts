import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Header } from '../../shared/layout/header/header';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, Header],
  template: `
    <app-header></app-header>
    <main class="min-h-screen pt-20 flex flex-col items-center justify-center text-center p-4">
      <h1 class="text-4xl font-bold mb-4">Welcome to Modoria</h1>
      <p class="text-lg text-muted-foreground max-w-md">
        Discover the essence of seasonal fashion. Your journey into the world of curated elegance starts here.
      </p>
    </main>
  `,
  styles: [`
    :host {
      display: block;
    }
  `]
})
export class HomeComponent {}
