export class Widget {
    title: string;
    icon_url: string;
    url: string;
    date: Date;
    source: string;
    desc: string;
    origin: string;
    destination: string;
    product: string;
    requestor: string;



    constructor(obj?: any) {
        this.title = obj && obj.title || null;
        this.icon_url = obj && obj.icon_url || null;
        this.url = obj && obj.url || null;
        this.origin = obj && obj.origin || null;
        this.destination = obj && obj.destination || null;
        this.product = obj && obj.product || null;
        this.requestor = obj && obj.requestor || null;
        this.date = obj && new Date(obj.date) || new Date();
        this.desc = obj && obj.desc || null;
        this.source = obj && obj.source || null;
    }
}
