System.register(["angular2/core", "./widget.component"], function (exports_1, context_1) {
    "use strict";
    var __extends = (this && this.__extends) || (function () {
        var extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return function (d, b) {
            extendStatics(d, b);
            function __() { this.constructor = d; }
            d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
        };
    })();
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __moduleName = context_1 && context_1.id;
    var core_1, widget_component_1, AlertWidgetComponent;
    return {
        setters: [
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (widget_component_1_1) {
                widget_component_1 = widget_component_1_1;
            }
        ],
        execute: function () {
            AlertWidgetComponent = (function (_super) {
                __extends(AlertWidgetComponent, _super);
                function AlertWidgetComponent() {
                    return _super !== null && _super.apply(this, arguments) || this;
                }
                return AlertWidgetComponent;
            }(widget_component_1.WidgetComponent));
            AlertWidgetComponent = __decorate([
                core_1.Component({
                    selector: 'alert-widget-component',
                    template: "\n        <div class=\"carousel-item-src\">\n            <div class=\"pictogram\">\n                <img [src]=\"widget.icon_url\"/>\n            </div>\n            <div class=\"main-content\">\n                <div class=\"header\">\n                    <div class=\"date-of-source\"> {{widget.date | date:'shortDate'}}</div>\n                    &nbsp;&nbsp;\n                    <div class=\"news-source\"> {{widget.source}}</div>\n\n\n                </div>\n                <div class=\"body-of-item\">\n                    <div class=\"title-carousel\"> {{widget.title}}</div>\n                    <div class=\"desc-carousel\"> {{widget.desc}}</div>\n                </div>\n            </div>\n        </div>\n    "
                })
            ], AlertWidgetComponent);
            exports_1("AlertWidgetComponent", AlertWidgetComponent);
        }
    };
});
//# sourceMappingURL=alert.widget.component.js.map