/**
 * Message represents one message being sent in a Thread
 */

import {Widget} from "./widget.model";
import {SupplierModel} from "./supplier.model";

export class Message {

   sentAt: Date;
   isRead: boolean;
   author: string;
   isUser: boolean;
   payload: Object;
   text: string;
   widgets: Widget[];
   suppliers: SupplierModel[];

   constructor(obj?: any) {

     this.isRead          = obj && obj.isRead          || false;
     this.sentAt          = obj && obj.sentAt          || new Date();
     this.author          = obj && obj.author          || null;
     this.isUser          = obj && obj.isUser          || false;
     this.text            = obj && obj.text            || null;
     this.payload         = obj && obj.payload         || null;
     this.widgets         = obj && obj.widgets         || null;
     this.suppliers       = obj && obj.suppliers       || null;

   }
 }




