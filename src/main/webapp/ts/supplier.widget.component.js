System.register(["angular2/core", "./budget.pipe"], function (exports_1, context_1) {
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
    var core_1, budget_pipe_1, SupplierWidgetComponent;
    return {
        setters: [
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (budget_pipe_1_1) {
                budget_pipe_1 = budget_pipe_1_1;
            }
        ],
        execute: function () {
            SupplierWidgetComponent = (function () {
                function SupplierWidgetComponent() {
                }
                return SupplierWidgetComponent;
            }());
            __decorate([
                core_1.Input(),
                __metadata("design:type", Array)
            ], SupplierWidgetComponent.prototype, "suppliers", void 0);
            SupplierWidgetComponent = __decorate([
                core_1.Component({
                    selector: 'supplier-widget-component',
                    pipes: [budget_pipe_1.BudgetPipe],
                    template: "\n        <ul> \n            <li *ngFor=\"let supplier of suppliers\"> {{supplier.name}} - {{ supplier.cost | budgetPipe}}</li>\n        </ul>\n    "
                })
            ], SupplierWidgetComponent);
            exports_1("SupplierWidgetComponent", SupplierWidgetComponent);
        }
    };
});
//# sourceMappingURL=supplier.widget.component.js.map