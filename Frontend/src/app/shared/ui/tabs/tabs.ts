import { Component, Input, Output, EventEmitter } from '@angular/core';
import { UpperCasePipe } from '@angular/common';

export interface Tab {
 id: string;
 label: string;
}

@Component({
 selector: 'app-tabs',
 imports: [UpperCasePipe],
 templateUrl: './tabs.html',
 styleUrl: './tabs.css',
})
export class Tabs {
 @Input() tabs: Tab[] = [];
 @Input() activeTabId: string = '';

 @Output() tabChange = new EventEmitter<string>();

 selectTab(id: string) {
 if (this.activeTabId !== id) {
 this.activeTabId = id;
 this.tabChange.emit(id);
 }
 }
}
