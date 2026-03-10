import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-form',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './form.html',
    styleUrl: './form.css'
})
export class FormComponent {
    @Input() title = '';
    @Input() subtitle = '';
    @Input() footerText = '';
    @Input() maxWidth = '520px';
}
