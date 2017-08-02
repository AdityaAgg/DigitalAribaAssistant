import { Pipe, PipeTransform } from 'angular2/core';


/**
 * FromNowPipe let's us convert a date into a human-readable relative-time
 * such as "10 minutes ago".
 */
@Pipe({
    name: 'budgetPipe'
})
export class BudgetPipe implements PipeTransform {
    transform(value: any, args: Array<any>): string {
        let budgetValue = 400000 - value;
        if(budgetValue> 0){
            return "+$" + budgetValue;
        } else
            return "-$" + Math.abs(budgetValue);
    }
}

export const fromNowPipeInjectables: Array<any> = [
   BudgetPipe
];
