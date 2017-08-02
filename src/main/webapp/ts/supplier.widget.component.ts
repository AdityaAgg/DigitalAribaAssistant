import {Component, Input} from 'angular2/core';
import {SupplierModel} from "./supplier.model";
import {BudgetPipe} from "./budget.pipe";


@Component({
    selector: 'supplier-widget-component',
    pipes: [BudgetPipe],
    template: `
        <ul> 
            <li *ngFor="let supplier of suppliers"> {{supplier.name}} - {{ supplier.cost | budgetPipe}}</li>
        </ul>
    `
})
export class SupplierWidgetComponent  {
    @Input() suppliers:SupplierModel[];


}
