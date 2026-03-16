import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Header } from '../../shared/layout/header/header';

@Component({
  selector: 'app-stylist',
  standalone: true,
  imports: [CommonModule, RouterLink, Header],
  templateUrl: './stylist.html',
})
export class StylistComponent {}
