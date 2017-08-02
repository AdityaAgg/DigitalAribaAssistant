import {Component, Input} from 'angular2/core';

import {Message} from "./message.model"
import {FromNowPipe} from "./from-now.pipe";
import {CarouselComponent} from "./carousel.component"
import {SupplierWidgetComponent} from "./supplier.widget.component";

@Component({
    selector: 'chat-message',
    pipes: [FromNowPipe],
    directives: [CarouselComponent, SupplierWidgetComponent],
    template: `
        <div class="msg-container"
             [ngClass]="{'base-sent': !message.isUser, 'base-receive': message.isUser}">


            <div class="messages"
                 [ngClass]="{'msg-sent': !message.isUser, 'msg-receive': message.isUser}">
                <p>{{message.text}}</p>

                <carousel-component [widgets]="message.widgets"></carousel-component>
                <div *ngIf="message.suppliers!=null">
                    <supplier-widget-component [suppliers]="message.suppliers"></supplier-widget-component>
                </div>
                <p class="time">{{message.author}} • {{message.sentAt | fromNow}}</p>
            </div>


        </div>`
})
export class ChatMessageComponent {
    @Input() message: Message;
}
