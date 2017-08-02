System.register(["angular2/core", "./dialog.service", "angular2/http", "./ce.docs", "./payload", "./supplier.service", "./message.model", "./chat.message.component", "./widget.model", "./supplier.model"], function (exports_1, context_1) {
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
    var core_1, dialog_service_1, http_1, ce_docs_1, payload_1, supplier_service_1, message_model_1, chat_message_component_1, widget_model_1, supplier_model_1, AppComponent;
    return {
        setters: [
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (dialog_service_1_1) {
                dialog_service_1 = dialog_service_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            },
            function (ce_docs_1_1) {
                ce_docs_1 = ce_docs_1_1;
            },
            function (payload_1_1) {
                payload_1 = payload_1_1;
            },
            function (supplier_service_1_1) {
                supplier_service_1 = supplier_service_1_1;
            },
            function (message_model_1_1) {
                message_model_1 = message_model_1_1;
            },
            function (chat_message_component_1_1) {
                chat_message_component_1 = chat_message_component_1_1;
            },
            function (widget_model_1_1) {
                widget_model_1 = widget_model_1_1;
            },
            function (supplier_model_1_1) {
                supplier_model_1 = supplier_model_1_1;
            }
        ],
        execute: function () {
            AppComponent = (function () {
                function AppComponent(_dialogService, _supplierService, http, el) {
                    this._dialogService = _dialogService;
                    this._supplierService = _supplierService;
                    this.http = http;
                    this.el = el;
                    // Store the response so we can display the JSON for end user to see
                    // We will also need to use the response's context for subsequent calls
                    this.response = null;
                    this.timer = null;
                    this.setupTimer = null;
                    this.question = null;
                    this.segments = []; // Array of requests and responses
                    this.workspace_id = null;
                    this.widgets = [];
                    this.suppliers = [];
                    this.getLang();
                }
                AppComponent.prototype.ngAfterViewInit = function (_dialogService) {
                    this.checkSetup(_dialogService);
                };
                /*
                 * This method is responsible for detecting user locale and getting locale specific content to be displayed by making a
                 * GET request to the respective file.
                 */
                AppComponent.prototype.getLang = function () {
                    var _this = this;
                    var browserLang = window.navigator.language;
                    var complLang = browserLang.split('-');
                    var lang = complLang[0];
                    var lang_url = 'locale/' + lang + '.json';
                    this.http.get(lang_url).map(function (res) { return res.json(); }).subscribe(//get introductory message - check locale/en.json
                    function (//get introductory message - check locale/en.json
                        data) {
                        _this.langData = data;
                    }, function (error) {
                        var lang_url = 'locale/en.json';
                        _this.http.get(lang_url).map(function (res) { return res.json(); }).subscribe(//nested attempt
                        function (//nested attempt
                            data) {
                            _this.langData = data;
                            var obj = {};
                            obj["text"] = _this.langData.Log;
                            obj["isUser"] = false;
                            _this.segments.push(new message_model_1.Message(obj));
                        }, function (error) { return alert(JSON.stringify(error)); });
                    });
                };
                /*
                * This method is responsible for triggering a request whenever a Enter key is pressed .
                */
                AppComponent.prototype.keypressed = function (event) {
                    if (event.type === "click") {
                        if ($(event.target).attr('class') === "btn-chat")
                            this.sendData();
                    }
                    if (event && event.keyCode === 38) {
                        var supplierName = "Aero Brand";
                        this.supplierChange(supplierName);
                    } // keycode set to up arrow for now
                    if (event && event.keyCode === 13) {
                        this.sendData();
                    }
                };
                /*
                 * This method is responsible for detecting if the set-up processs involving creation of various Watson services
                 * and configuring them is complete. The status is checked every 5 seconds till its complete.
                 * A loading screen is displayed to show set-up progress accordingly.
                 *
                 *
                 */
                AppComponent.prototype.checkSetup = function (_dialogService) {
                    var _this = this;
                    this._dialogService.setup().subscribe(function (data) {
                        _this.workspace_id = data.WORKSPACE_ID;
                        var setup_state = data.setup_state;
                        var setup_status_msg = data.setup_status_message;
                        var setup_phase = data.setup_phase;
                        var setup_message = data.setup_message;
                        var setup_step = data.setup_step;
                        var setup = document.querySelector('.setup');
                        var setup_status = document.querySelector('.setup-msg');
                        var chat_app = document.querySelector('chat-app');
                        var setupLoader = document.querySelector('.setup-loader');
                        var setupPhase = document.querySelector('.setup-phase');
                        var setupPhaseMsg = document.querySelector('.setup-phase-msg');
                        var errorPhase = document.querySelector('.error-phase');
                        var errorPhaseMsg = document.querySelector('.error-phase-msg');
                        var circles = document.querySelector('.circles');
                        var gerror = document.querySelector('.gerror');
                        var werror = document.querySelector('.werror');
                        var activeCircle = document.querySelector('.active-circle');
                        var nactiveCircle = document.querySelector('.non-active-circle');
                        setup_status.innerHTML = setup_status_msg;
                        if (setup_state === 'not_ready') {
                            document.body.style.backgroundColor = 'darkgray';
                            chat_app.style.opacity = '0.25';
                            setup.style.display = 'block';
                            setupPhase.innerHTML = setup_phase;
                            setupPhaseMsg.innerHTML = setup_message;
                            if (setup_step === '0') {
                                errorPhase.innerHTML = setup_phase;
                                errorPhaseMsg.innerHTML = setup_message;
                                setupLoader.style.display = 'none';
                                setupPhase.style.display = 'none';
                                setupPhaseMsg.style.display = 'none';
                                circles.style.display = 'none';
                                if (setup_phase !== 'Error') {
                                    werror.style.display = 'block';
                                }
                                else {
                                    gerror.style.display = 'block';
                                }
                                errorPhase.style.display = 'block';
                                errorPhaseMsg.style.display = 'block';
                            }
                            else {
                                setupLoader.style.display = 'block';
                                setupPhase.style.display = 'block';
                                setupPhaseMsg.style.display = 'block';
                                circles.style.display = 'block';
                                gerror.style.display = 'none';
                                werror.style.display = 'none';
                                errorPhase.style.display = 'none';
                                errorPhaseMsg.style.display = 'none';
                            }
                            if (setup_step === '2') {
                                activeCircle.classList.remove('active-circle');
                                activeCircle.classList.add('non-active-circle');
                                nactiveCircle.classList.remove('non-active-circle');
                                nactiveCircle.classList.add('active-circle');
                            }
                            _this.setupTimer = setTimeout(function () {
                                _this.checkSetup(_dialogService);
                            }, 5000);
                        }
                        else {
                            var payload = { 'input': { 'text': '' } };
                            var chatColumn = document.querySelector('#scrollingChat');
                            _this.callConversationService(chatColumn, payload);
                            document.body.style.backgroundColor = 'white';
                            chat_app.style.opacity = '1';
                            setup.style.display = 'none';
                            if (_this.setupTimer) {
                                clearTimeout(_this.setupTimer);
                            }
                        }
                    }, function (error) { return alert(JSON.stringify(error)); });
                };
                /*
                 * This method is responsible for changing the layout of payload section based on screen resolution.
                 */
                AppComponent.prototype.resizePayloadColumn = function (rightColumn) {
                    if (window.innerWidth < 730) {
                        rightColumn.classList.add('no-show');
                    }
                    else if (window.innerWidth < 830) {
                        rightColumn.classList.remove('no-show');
                        rightColumn.style.width = '340px';
                    }
                    else if (window.innerWidth < 860) {
                        rightColumn.classList.remove('no-show');
                        rightColumn.style.width = '445px';
                    }
                    else if (window.innerWidth < 951) {
                        rightColumn.classList.remove('no-show');
                        rightColumn.style.width = '395px';
                    }
                    else {
                        rightColumn.classList.remove('no-show');
                        rightColumn.style.width = '445px';
                    }
                };
                /*
                 * This method is responsible for preparing the data to send and call the method for Conversation Service
                 */
                AppComponent.prototype.sendData = function () {
                    var chatColumn = document.querySelector('#scrollingChat');
                    chatColumn.classList.add('loading');
                    var q = '';
                    if (this.question != null) {
                        q = this.question;
                    }
                    this.question = '';
                    var context = null;
                    if (this.response != null) {
                        context = this.response.context;
                        // we are going to delete the context variable 'callRetrieveAndRank' before
                        // sending back to the Conversation service
                        if (context && context.callRetrieveAndRank) {
                            delete context.callRetrieveAndRank;
                        }
                    }
                    var input = { 'text': q };
                    var payload = { input: input, context: context };
                    var obj = {};
                    obj["text"] = q;
                    obj["isUser"] = true;
                    // Add the user utterance to the list of chat segments
                    this.segments.push(new message_model_1.Message(obj));
                    // Call the method which calls the proxy for the message api
                    this.callConversationService(chatColumn, payload);
                };
                AppComponent.prototype.supplierChange = function (supplierName) {
                    var _this = this;
                    var productName = "Electric Turbine Blade";
                    if (!this.response.context.suppliers) {
                        this._supplierService.request(productName).subscribe(function (supplierData) {
                            console.log("success retrieving supplier data");
                            _this.response.context.suppliers = supplierData;
                            var suppliers = _this.response.context.suppliers;
                            suppliers = suppliers.reduce(function (map, obj) {
                                map[obj.name] = obj.cost;
                                return map;
                            }, {});
                            var supplierDetails = suppliers[supplierName];
                            var extraInfo = "";
                            var responseText;
                            if (supplierDetails && (400000 - supplierDetails < 0)) {
                                extraInfo = "You are $" + (supplierDetails - 400000) + " under budget.";
                                responseText = supplierName + " is a 5 star supplier with on time delivery. " + extraInfo;
                                var payload = _this.response.context;
                                var obj = {};
                                obj["text"] = responseText;
                                obj["isUser"] = false;
                                obj["payload"] = payload;
                                _this.segments.push(new message_model_1.Message(obj));
                            }
                        }, function (error) {
                            console.log(error);
                        }, function () {
                            console.log("Complete");
                        });
                    }
                    else {
                        var suppliers = this.response.context.suppliers;
                        suppliers = suppliers.reduce(function (map, obj) {
                            map[obj.name] = obj.cost;
                            return map;
                        }, []);
                        var supplierDetails = suppliers[supplierName];
                        var extraInfo = "";
                        var responseText = void 0;
                        if (supplierDetails && (400000 - supplierDetails < 0)) {
                            extraInfo = "You are $" + supplierDetails + " under budget.";
                            responseText = supplierName + " is a 5 star supplier with on time delivery. " + extraInfo;
                            var payload = this.response.context;
                            var obj = {};
                            obj["text"] = responseText;
                            obj["isUser"] = false;
                            obj["payload"] = payload;
                            this.segments.push(new message_model_1.Message(obj));
                        }
                    }
                };
                /*
                  * This method is responsible for making a request to Conversation service with the corresponding user query.
                  */
                AppComponent.prototype.callConversationService = function (chatColumn, payload) {
                    var _this = this;
                    var responseText = '';
                    var ce = null;
                    // Send the user utterance to dialog, also send previous context
                    this._dialogService.message(this.workspace_id, payload).subscribe(function (data1) {
                        _this.response = data1;
                        if (data1) {
                            if (data1.error) {
                                responseText = data1.error;
                                data1 = _this.langData.NResponse;
                            }
                            else if (data1.output) {
                                _this.widgets = [];
                                if (data1.output.delay) {
                                    var delays = data1.context.delays;
                                    var newsWidget = null;
                                    for (var _i = 0, delays_1 = delays; _i < delays_1.length; _i++) {
                                        var delay = delays_1[_i];
                                        var obj_1 = {};
                                        obj_1["title"] = delay.title;
                                        obj_1["date"] = delay.date;
                                        obj_1["desc"] = delay.body;
                                        obj_1["source"] = delay.source;
                                        obj_1["url"] = delay.sourceUrl;
                                        if (delay.type === "news") {
                                            obj_1["icon_url"] = "img/newspaper.svg";
                                        }
                                        else {
                                            obj_1["icon_url"] = "img/form.svg";
                                        }
                                        newsWidget = new widget_model_1.Widget(obj_1);
                                        _this.widgets.push(newsWidget);
                                    }
                                    console.log(_this.widgets);
                                }
                                else if (data1.output.past_forms) {
                                    var recentDocs = data1.context.recent_docs;
                                    var mainRecentDocs = JSON.parse(recentDocs);
                                    for (var _a = 0, mainRecentDocs_1 = mainRecentDocs; _a < mainRecentDocs_1.length; _a++) {
                                        var doc = mainRecentDocs_1[_a];
                                        var obj_2 = {};
                                        console.log(doc.origin);
                                        obj_2["origin"] = doc.origin;
                                        console.log(doc.destination);
                                        obj_2["destination"] = doc.destination;
                                        obj_2["product"] = doc.product;
                                        obj_2["cost"] = doc.cost;
                                        obj_2["requestor"] = doc.requestor;
                                        console.log(obj_2);
                                        _this.widgets.push(new widget_model_1.Widget(obj_2));
                                        console.log(_this.widgets);
                                    }
                                }
                                else if (data1.output.other_supplier_options) {
                                    console.log(data1.output.other_supplier_options);
                                    _this.suppliers = [];
                                    var currentSupplier = data1.context.current_supplier;
                                    var localSuppliers = data1.context.suppliers;
                                    console.log(localSuppliers);
                                    for (var _b = 0, localSuppliers_1 = localSuppliers; _b < localSuppliers_1.length; _b++) {
                                        var supplier = localSuppliers_1[_b];
                                        console.log(supplier);
                                        if (!(supplier.name == currentSupplier)) {
                                            var obj_3 = {};
                                            obj_3["name"] = supplier.name;
                                            obj_3["cost"] = supplier.cost;
                                            var supplierModel = new supplier_model_1.SupplierModel(obj_3);
                                            _this.suppliers.push(supplierModel);
                                        }
                                    }
                                }
                                if (data1.output.text && data1.output.text.length >= 1) {
                                    responseText = data1.output.text.join('<br>');
                                }
                            }
                            var obj = {};
                            obj["text"] = responseText;
                            obj["isUser"] = false;
                            obj["payload"] = data1;
                            if (_this.widgets != null) {
                                obj["widgets"] = _this.widgets;
                                console.log(obj["widgets"]);
                            }
                            if (_this.suppliers != null && _this.suppliers.length > 0) {
                                obj["suppliers"] = _this.suppliers;
                            }
                            _this.segments.push(new message_model_1.Message(obj));
                            chatColumn.classList.remove('loading');
                            if (_this.timer) {
                                clearTimeout(_this.timer);
                            }
                            _this.timer = setTimeout(function () {
                                var messages = document.getElementById('scrollingChat').getElementsByClassName('clear');
                                _this.scrollToBottom();
                            }, 50);
                        }
                    }, function (error) {
                        var serviceDownMsg = _this.langData.Log;
                        var obj = {};
                        obj["text"] = serviceDownMsg;
                        obj["isUser"] = false;
                        obj["payload"] = _this.langData.NResponse;
                        _this.segments.push(new message_model_1.Message(obj));
                        chatColumn.classList.remove('loading');
                    });
                };
                AppComponent.prototype.scrollToBottom = function () {
                    var scrollPane = this.el
                        .nativeElement.querySelector('.msg-container-base');
                    scrollPane.scrollTop = scrollPane.scrollHeight;
                };
                return AppComponent;
            }());
            AppComponent = __decorate([
                core_1.Component({
                    directives: [ce_docs_1.CeDocComponent, payload_1.PayloadComponent, chat_message_component_1.ChatMessageComponent],
                    providers: [dialog_service_1.DialogService, supplier_service_1.SupplierService],
                    selector: 'chat-app',
                    template: "\n\n\n\n\n        <div class=\"chat-window-container\">\n            <div class=\"chat-window\">\n                <div class=\"panel-container\">\n                    <div class=\"panel panel-default\">\n\n                        <div class=\"panel-heading top-bar\">\n                            <div class=\"panel-title-container\">\n                                <h3 class=\"panel-title\">\n                                    <span class=\"glyphicon glyphicon-comment\"></span>\n                                    Ariba Digital Assistant\n                                </h3>\n                            </div>\n\n                            <div class=\"panel-buttons-container\">\n                            </div>\n                        </div>\n\n                        <div class=\"panel-body msg-container-base\" id=\"scrollingChat\">\n                            <chat-message *ngFor=\"let segment of segments\" [message]=\"segment\"></chat-message>\n\n                            <div class='clear'></div> <!--margin between this segment and the next-->\n                            <!--<div *ngIf='segment.isUser && segment == segments[segments.length - 1]' class='load'></div>-->\n                        </div>\n\n                        <div class=\"panel-footer\"> <!--TODO: appropriately style send button and top bar -->\n                            <div class=\"input-group\">\n                                <input type=\"text\" class=\"chat-input\"\n                                       placeholder=\"Write your message here...\"\n                                       [(ngModel)]='question' (keydown)='keypressed($event)'/>\n                                <span class=\"input-group-btn\">\n            <button class=\"btn-chat\"\n                    (click)=\"keypressed($event)\"\n            >Send</button>\n          </span>\n                            </div>\n                        </div>\n\n                    </div>\n                </div>\n            </div>\n        </div>\n\n\n    ",
                }),
                __metadata("design:paramtypes", [dialog_service_1.DialogService, supplier_service_1.SupplierService, http_1.Http, core_1.ElementRef])
            ], AppComponent);
            exports_1("AppComponent", AppComponent);
        }
    };
});
//# sourceMappingURL=app.component.js.map