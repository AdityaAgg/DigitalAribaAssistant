System.register(["angular2/core", "./message.model", "./from-now.pipe", "./carousel.component", "./supplier.widget.component"], function (exports_1, context_1) {
    "use strict";
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var __moduleName = context_1 && context_1.id;
    var core_1, message_model_1, from_now_pipe_1, carousel_component_1, supplier_widget_component_1, ChatMessageComponent;
    return {
        setters: [
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (message_model_1_1) {
                message_model_1 = message_model_1_1;
            },
            function (from_now_pipe_1_1) {
                from_now_pipe_1 = from_now_pipe_1_1;
            },
            function (carousel_component_1_1) {
                carousel_component_1 = carousel_component_1_1;
            },
            function (supplier_widget_component_1_1) {
                supplier_widget_component_1 = supplier_widget_component_1_1;
            }
        ],
        execute: function () {
            ChatMessageComponent = (function () {
                function ChatMessageComponent() {
                }
                return ChatMessageComponent;
            }());
            __decorate([
                core_1.Input(),
                __metadata("design:type", message_model_1.Message)
            ], ChatMessageComponent.prototype, "message", void 0);
            ChatMessageComponent = __decorate([
                core_1.Component({
                    selector: 'chat-message',
                    pipes: [from_now_pipe_1.FromNowPipe],
                    directives: [carousel_component_1.CarouselComponent, supplier_widget_component_1.SupplierWidgetComponent],
                    template: "\n        <div class=\"msg-container\"\n             [ngClass]=\"{'base-sent': !message.isUser, 'base-receive': message.isUser}\">\n\n\n            <div class=\"messages\"\n                 [ngClass]=\"{'msg-sent': !message.isUser, 'msg-receive': message.isUser}\">\n                <p>{{message.text}}</p>\n\n                <carousel-component [widgets]=\"message.widgets\"></carousel-component>\n                <div *ngIf=\"message.suppliers!=null\">\n                    <supplier-widget-component [suppliers]=\"message.suppliers\"></supplier-widget-component>\n                </div>\n                <p class=\"time\">{{message.author}} \u2022 {{message.sentAt | fromNow}}</p>\n            </div>\n\n\n        </div>"
                })
            ], ChatMessageComponent);
            exports_1("ChatMessageComponent", ChatMessageComponent);
        }
    };
});
//# sourceMappingURL=chat.message.component.js.map