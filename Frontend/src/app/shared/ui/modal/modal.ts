import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Button } from '../button/button';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [Button],
  templateUrl: './modal.html',
  styleUrl: './modal.css',
})
export class Modal {
  @Input() title: string = '';
  @Input() isOpen: boolean = false;

  @Output() isOpenChange = new EventEmitter<boolean>();

  close() {
    this.isOpen = false;
    this.isOpenChange.emit(this.isOpen);
  }
}
