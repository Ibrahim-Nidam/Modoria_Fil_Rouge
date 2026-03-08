import { Component, Input } from '@angular/core';
import { Button } from '../../ui/button/button';
import { InputComponent } from '../../ui/input/input';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [Button, InputComponent],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  @Input() collectionName?: string = 'Autumn'; // Default collection
  @Input() collectionIcon?: string = 'energy_savings_leaf'; // Default icon
}
