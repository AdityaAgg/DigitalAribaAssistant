import {
  Component,
  Input
} from 'angular2/core';

import {Message} from "./message.model"
import {FromNowPipe} from "./from-now.pipe";

@Component({
  selector: 'chat-message',
    pipes:[FromNowPipe],
  template: `<div class="msg-container"
                  [ngClass]="{'base-sent': !message.isUser, 'base-receive': message.isUser}">


      <div class="messages"
           [ngClass]="{'msg-sent': !message.isUser, 'msg-receive': message.isUser}">
          <p>{{message.text}}</p>
          <p class="time">{{message.sender}} • {{message.sentAt | fromNow}}</p>
      </div>


  </div>`
})
export class ChatMessageComponent  {
  @Input() message: Message;

}
