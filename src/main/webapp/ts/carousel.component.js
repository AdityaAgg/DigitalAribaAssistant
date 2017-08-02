System.register(["angular2/core", "./form.widget.component", "./alert.widget.component"], function (exports_1, context_1) {
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
    var core_1, form_widget_component_1, alert_widget_component_1, CarouselComponent;
    return {
        setters: [
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (form_widget_component_1_1) {
                form_widget_component_1 = form_widget_component_1_1;
            },
            function (alert_widget_component_1_1) {
                alert_widget_component_1 = alert_widget_component_1_1;
            }
        ],
        execute: function () {
            CarouselComponent = (function () {
                function CarouselComponent() {
                }
                CarouselComponent.prototype.ngAfterViewInit = function () {
                    /* $('.carousel').carousel({
                         interval: 2000,
                         ride: true,
                         keyboard:true
                     });*/
                    $('.carousel-indicator').on('click', function () {
                        $('.carousel').carousel(parseInt($(this).attr('data-slide-to')));
                        $('.carousel-indicator.active').removeClass('active');
                        $(this).addClass('active');
                    });
                };
                return CarouselComponent;
            }());
            __decorate([
                core_1.Input(),
                __metadata("design:type", Array)
            ], CarouselComponent.prototype, "widgets", void 0);
            CarouselComponent = __decorate([
                core_1.Component({
                    selector: 'carousel-component',
                    directives: [form_widget_component_1.FormWidgetComponent, alert_widget_component_1.AlertWidgetComponent],
                    changeDetection: core_1.ChangeDetectionStrategy.CheckOnce,
                    template: "\n        <div id=\"carouselExampleIndicators\" class=\"carousel slide\" data-ride=\"carousel\">\n\n            <div class=\"carousel-inner\" role=\"listbox\">\n                <div *ngFor=\"let widget of widgets; let i = index\" [ngClass]=\"{'active' :i==0, 'carousel-item':true}\">\n                    <div *ngIf=\"widget.product!=null\">\n                        <form-widget-component [widget]=\"widget\"></form-widget-component>\n                    </div>\n\n                    <div *ngIf=\"widget.product==null\">\n                        <alert-widget-component [widget]=\"widget\"></alert-widget-component>\n                    </div>\n                </div>\n\n            </div>\n\n            <ol class=\"carousel-indicators\">\n\n                <li *ngFor=\"let widget of widgets; let i = index\" data-target=\"#carouselExampleIndicators\"\n                    [attr.data-slide-to]=\"i\" [ngClass]=\"{'carousel-indicator':true, 'active' :i==0}\"></li>\n\n            </ol>\n\n\n        </div>"
                })
            ], CarouselComponent);
            exports_1("CarouselComponent", CarouselComponent);
        }
    };
});
//# sourceMappingURL=carousel.component.js.map