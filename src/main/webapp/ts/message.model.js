System.register([], function (exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var Message;
    return {
        setters: [],
        execute: function () {
            /**
             * Message represents one message being sent in a Thread
             */
            Message = (function () {
                function Message(obj) {
                    this.isRead = obj && obj.isRead || false;
                    this.sentAt = obj && obj.sentAt || new Date();
                    this.author = obj && obj.author || null;
                    this.isUser = obj && obj.isUser || false;
                    this.text = obj && obj.text || null;
                    this.payload = obj && obj.payload || null;
                }
                return Message;
            }());
            exports_1("Message", Message);
        }
    };
});
//# sourceMappingURL=message.model.js.map