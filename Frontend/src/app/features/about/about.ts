import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Header } from '../../shared/layout/header/header';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule, RouterLink, Header],
  templateUrl: './about.html',
})
export class AboutComponent {}
