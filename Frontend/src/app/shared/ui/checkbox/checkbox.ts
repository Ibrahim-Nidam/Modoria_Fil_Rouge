import { Component, Input, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
    selector: 'app-checkbox',
    standalone: true,
    imports: [],
    templateUrl: './checkbox.html',
    styleUrl: './checkbox.css',
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => Checkbox),
            multi: true
        }
    ]
})
export class Checkbox implements ControlValueAccessor {
    @Input() label?: string;

    checked: boolean = false;
    disabled: boolean = false;

    onChange: any = () => { };
    onTouched: any = () => { };

    writeValue(value: any): void {
        this.checked = !!value;
    }

    registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        this.disabled = isDisabled;
    }

    onToggle(event: Event) {
        const target = event.target as HTMLInputElement;
        this.checked = target.checked;
        this.onChange(this.checked);
        this.onTouched();
    }
}
