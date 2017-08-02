System.register(["angular2/core"], function (exports_1, context_1) {
    "use strict";
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __moduleName = context_1 && context_1.id;
    var core_1, BudgetPipe, fromNowPipeInjectables;
    return {
        setters: [
            function (core_1_1) {
                core_1 = core_1_1;
            }
        ],
        execute: function () {
            BudgetPipe = (function () {
                function BudgetPipe() {
                }
                BudgetPipe.prototype.transform = function (value, args) {
                    var budgetValue = 400000 - value;
                    if (budgetValue > 0) {
                        return "+$" + budgetValue;
                    }
                    else
                        return "-$" + Math.abs(budgetValue);
                };
                return BudgetPipe;
            }());
            BudgetPipe = __decorate([
                core_1.Pipe({
                    name: 'budgetPipe'
                })
            ], BudgetPipe);
            exports_1("BudgetPipe", BudgetPipe);
            exports_1("fromNowPipeInjectables", fromNowPipeInjectables = [
                BudgetPipe
            ]);
        }
    };
});
//# sourceMappingURL=budget.pipe.js.map