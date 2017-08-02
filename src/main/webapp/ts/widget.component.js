System.register(["angular2/core", "./widget.model"], function (exports_1, context_1) {
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
    var core_1, widget_model_1, WidgetComponent;
    return {
        setters: [
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (widget_model_1_1) {
                widget_model_1 = widget_model_1_1;
            }
        ],
        execute: function () {
            WidgetComponent = (function () {
                function WidgetComponent() {
                }
                return WidgetComponent;
            }());
            __decorate([
                core_1.Input(),
                __metadata("design:type", widget_model_1.Widget)
            ], WidgetComponent.prototype, "widget", void 0);
            WidgetComponent = __decorate([
                core_1.Component({
                    selector: 'widget-component',
                    template: "<div class=\"carousel-item-src\">\n        <div class=\"pictogram\">\n            <!--  <div class=pictogram-container>-->\n            <img [src]=\"widget.icon_url\" />\n            <!--  </div>-->\n        </div>\n        <div class=\"main-content\">\n            <div class=\"header\">\n                <div class=\"date-of-source\"> 6/12/17 </div>\n                &nbsp;&nbsp;\n                <div class=\"news-source\"> Al Jazeera </div>\n\n\n            </div>\n            <div class=\"body-of-item\">\n                <div class=\"title-carousel\"> {{widget.title}} </div>\n                <div class=\"desc-carousel\"> Qatar is facing higher costs in selling its energy supplies.  </div>\n            </div>\n        </div>\n    </div>\n        "
                })
            ], WidgetComponent);
            exports_1("WidgetComponent", WidgetComponent);
        }
    };
});
//# sourceMappingURL=widget.component.js.map