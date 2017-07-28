
/**
 * Message represents one message being sent in a Thread
 */
 export class Message {

   sentAt: Date;
   isRead: boolean;
   author: string;
   isUser: boolean;
   payload: Object;
   text: string;

   constructor(obj?: any) {

     this.isRead          = obj && obj.isRead          || false;
     this.sentAt          = obj && obj.sentAt          || new Date();
     this.author          = obj && obj.author          || null;
     this.isUser          = obj && obj.isUser          || false;
     this.text            = obj && obj.text            || null;
     this.payload         = obj && obj.payload         || null;
   }
 }




