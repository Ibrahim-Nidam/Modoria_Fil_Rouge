import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Header } from '../../shared/layout/header/header';

@Component({
  selector: 'app-checkout-cancel',
  standalone: true,
  imports: [CommonModule, RouterLink, Header],
  templateUrl: './checkout-cancel.html',
})
export class CheckoutCancelComponent {}
