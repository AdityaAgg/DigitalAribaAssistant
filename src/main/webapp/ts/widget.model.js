System.register([], function (exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var Widget;
    return {
        setters: [],
        execute: function () {
            Widget = (function () {
                function Widget(obj) {
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
                return Widget;
            }());
            exports_1("Widget", Widget);
        }
    };
});
//# sourceMappingURL=widget.model.js.map