import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Header } from '../../shared/layout/header/header';

@Component({
  selector: 'app-journal',
  standalone: true,
  imports: [CommonModule, RouterLink, Header],
  templateUrl: './journal.html',
})
export class JournalComponent {
  readonly entries = [
    {
      title: 'The Spring Layering Formula',
      summary: 'How to layer lightweight textures without losing structure.',
      tag: 'Style Guide',
    },
    {
      title: 'Capsule Shoes for Every Season',
      summary: 'A compact footwear lineup that carries your wardrobe.',
      tag: 'Shoes',
    },
    {
      title: 'Designer Notes: Quiet Luxury Cuts',
      summary: 'The tailoring details that elevate minimal silhouettes.',
      tag: 'Designers',
    },
  ];
}
