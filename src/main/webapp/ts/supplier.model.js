System.register([], function (exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var SupplierModel;
    return {
        setters: [],
        execute: function () {
            SupplierModel = (function () {
                function SupplierModel(obj) {
                    this.name = obj && obj.name || null;
                    this.rating = obj && obj.rating || null;
                    this.cost = obj && obj.cost || null;
                }
                return SupplierModel;
            }());
            exports_1("SupplierModel", SupplierModel);
        }
    };
});
//# sourceMappingURL=supplier.model.js.map