export class SupplierModel {
    name: string;
    rating: string;
    cost: number;


    constructor(obj?: any) {
        this.name = obj && obj.name || null;
        this.rating = obj && obj.rating || null;
        this.cost = obj && obj.cost || null;
        
    }
}
