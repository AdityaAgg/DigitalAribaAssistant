System.register(["angular2/core", "angular2/http", "rxjs/add/operator/map"], function (exports_1, context_1) {
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
    var core_1, http_1, SupplierService;
    return {
        setters: [
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            },
            function (_1) {
            }
        ],
        execute: function () {
            SupplierService = (function () {
                function SupplierService(http) {
                    this.http = http;
                }
                SupplierService.prototype.request = function (productName) {
                    var headers = new http_1.Headers();
                    headers.append('Content-Type', 'application/json');
                    var productInfo = "Electric Turbine Blade";
                    productName = (productName) ? productName : productInfo;
                    if (productName) {
                        return this.http.get('https://supplier-provider.mybluemix.net/api/suppliers?product=' + productName).map(function (res) { return res.json(); });
                    }
                    else {
                        throw 'api call to supplier listing failed';
                    }
                };
                return SupplierService;
            }());
            SupplierService = __decorate([
                core_1.Injectable(),
                __metadata("design:paramtypes", [http_1.Http])
            ], SupplierService);
            exports_1("SupplierService", SupplierService);
        }
    };
});
//# sourceMappingURL=supplier.service.js.map