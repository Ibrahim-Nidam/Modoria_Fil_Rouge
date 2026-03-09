import { Component, Input, Output, EventEmitter } from '@angular/core';

export interface TableColumn {
 key: string;
 label: string;
}

@Component({
 selector: 'app-table',
 imports: [],
 templateUrl: './table.html',
 styleUrl: './table.css',
})
export class Table {
 @Input() columns: TableColumn[] = [];
 @Input() data: any[] = [];
 @Input() hoverable: boolean = false;
 @Input() emptyMessage: string = 'No data available';

 @Output() rowClick = new EventEmitter<any>();

 onRowClick(row: any) {
 if (this.hoverable) {
 this.rowClick.emit(row);
 }
 }
}
