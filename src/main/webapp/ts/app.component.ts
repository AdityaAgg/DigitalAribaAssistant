/**
 * (C) Copyright IBM Corp. 2016. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
import {Component, ElementRef} from 'angular2/core';
import {DialogService} from './dialog.service';
import {Http} from 'angular2/http';
import {CeDocComponent} from './ce.docs';
import {PayloadComponent} from './payload';
import {SupplierService} from "./supplier.service";
import {Message} from "./message.model";
import {ChatMessageComponent} from "./chat.message.component";
import {Widget} from "./widget.model";
import {SupplierModel} from "./supplier.model";

declare var $: JQueryStatic;


/*
 * Main entry point to the application. This component is responsible for the entire page layout.
 * Watch: https://www.youtube.com/watch?v=SasXUqBE-38&index=8&list=PLZDyxLlNKRY_GJskIreh9sQgExJ4z8oZO
 * to get an understanding of the UI components
 * or run and inspect element
 */
@Component({
    directives: [CeDocComponent, PayloadComponent, ChatMessageComponent],
    providers: [DialogService, SupplierService],
    selector: 'chat-app',
    template: `




        <div class="chat-window-container">
            <div class="chat-window">
                <div class="panel-container">
                    <div class="panel panel-default">

                        <div class="panel-heading top-bar">
                            <div class="panel-title-container">
                                <h3 class="panel-title">
                                    <span class="glyphicon glyphicon-comment"></span>
                                    Ariba Digital Assistant
                                </h3>
                            </div>

                            <div class="panel-buttons-container">
                            </div>
                        </div>

                        <div class="panel-body msg-container-base" id="scrollingChat">
                            <chat-message *ngFor="let segment of segments" [message]="segment"></chat-message>

                            <div class='clear'></div> <!--margin between this segment and the next-->
                            <!--<div *ngIf='segment.isUser && segment == segments[segments.length - 1]' class='load'></div>-->
                        </div>

                        <div class="panel-footer"> <!--TODO: appropriately style send button and top bar -->
                            <div class="input-group">
                                <input type="text" class="chat-input"
                                       placeholder="Write your message here..."
                                       [(ngModel)]='question' (keydown)='keypressed($event)'/>
                                <span class="input-group-btn">
            <button class="btn-chat"
                    (click)="keypressed($event)"
            >Send</button>
          </span>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>


    `,
})
export class AppComponent {
    // Store the response so we can display the JSON for end user to see
    // We will also need to use the response's context for subsequent calls

    private response: any = null;
    private timer: any = null;
    private setupTimer: any = null;
    private question: string = null;
    private segments: Message[] = []; // Array of requests and responses
    private workspace_id: string = null;
    private langData: any;
    private widgets: Widget[] = [];
    private suppliers: SupplierModel[] = [];

    constructor(private _dialogService: DialogService, private _supplierService: SupplierService, private http: Http, public el: ElementRef) {
        this.getLang();
    }


    private ngAfterViewInit(_dialogService: DialogService) {
        this.checkSetup(_dialogService);
    }

    /*
     * This method is responsible for detecting user locale and getting locale specific content to be displayed by making a
     * GET request to the respective file.
     */
    private getLang() {
        let browserLang = window.navigator.language;
        let complLang = browserLang.split('-');
        let lang = complLang[0];
        let lang_url = 'locale/' + lang + '.json';
        this.http.get(lang_url).map(res => res.json()).subscribe( //get introductory message - check locale/en.json
            data => {
                this.langData = data;

            },
            error => {
                let lang_url = 'locale/en.json';
                this.http.get(lang_url).map(res => res.json()).subscribe( //nested attempt
                    data => {
                        this.langData = data;
                        var obj = {};
                        obj["text"] = this.langData.Log;
                        obj["isUser"] = false;
                        this.segments.push(new Message(obj));
                    },
                    error => alert(JSON.stringify(error)));
            });
    }


    /*
    * This method is responsible for triggering a request whenever a Enter key is pressed .
    */
    private keypressed(event) {

        if (event.type === "click") {
            if ($(event.target).attr('class') === "btn-chat")
                this.sendData();
        }

        if (event && event.keyCode === 38) {
            let supplierName = "Aero Brand";
            this.supplierChange(supplierName);
        }// keycode set to up arrow for now


        if (event && event.keyCode === 13) {
            this.sendData();

        }
    }


    /*
     * This method is responsible for detecting if the set-up processs involving creation of various Watson services
     * and configuring them is complete. The status is checked every 5 seconds till its complete.
     * A loading screen is displayed to show set-up progress accordingly.
     *
     *
     */
    private checkSetup(_dialogService: DialogService) {
        this._dialogService.setup().subscribe(
            data => {
                this.workspace_id = data.WORKSPACE_ID;
                let setup_state = data.setup_state;
                let setup_status_msg = data.setup_status_message;
                let setup_phase = data.setup_phase;
                let setup_message = data.setup_message;
                let setup_step = data.setup_step;
                let setup = <HTMLElement>document.querySelector('.setup');
                let setup_status = <HTMLElement>document.querySelector('.setup-msg');
                let chat_app = <HTMLElement>document.querySelector('chat-app');
                let setupLoader = <HTMLElement>document.querySelector('.setup-loader');
                let setupPhase = <HTMLElement>document.querySelector('.setup-phase');
                let setupPhaseMsg = <HTMLElement>document.querySelector('.setup-phase-msg');
                let errorPhase = <HTMLElement>document.querySelector('.error-phase');
                let errorPhaseMsg = <HTMLElement>document.querySelector('.error-phase-msg');
                let circles = <HTMLElement>document.querySelector('.circles');
                let gerror = <HTMLElement>document.querySelector('.gerror');
                let werror = <HTMLElement>document.querySelector('.werror');
                let activeCircle = <HTMLElement>document.querySelector('.active-circle');
                let nactiveCircle = <HTMLElement>document.querySelector('.non-active-circle');
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
                        } else {
                            gerror.style.display = 'block';
                        }
                        errorPhase.style.display = 'block';
                        errorPhaseMsg.style.display = 'block';
                    } else {
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
                    this.setupTimer = setTimeout(() => {
                        this.checkSetup(_dialogService);
                    }, 5000);
                } else {
                    let payload = {'input': {'text': ''}};
                    let chatColumn = <HTMLElement>document.querySelector('#scrollingChat');
                    this.callConversationService(chatColumn, payload);
                    document.body.style.backgroundColor = 'white';
                    chat_app.style.opacity = '1';
                    setup.style.display = 'none';
                    if (this.setupTimer) {
                        clearTimeout(this.setupTimer);
                    }
                }
            },
            error => alert(JSON.stringify(error)));
    }


    /*
     * This method is responsible for changing the layout of payload section based on screen resolution.
     */
    private resizePayloadColumn(rightColumn) {
        if (window.innerWidth < 730) {
            rightColumn.classList.add('no-show');
        } else if (window.innerWidth < 830) {
            rightColumn.classList.remove('no-show');
            rightColumn.style.width = '340px';
        } else if (window.innerWidth < 860) {
            rightColumn.classList.remove('no-show');
            rightColumn.style.width = '445px';
        } else if (window.innerWidth < 951) {
            rightColumn.classList.remove('no-show');
            rightColumn.style.width = '395px';
        } else {
            rightColumn.classList.remove('no-show');
            rightColumn.style.width = '445px';
        }
    }


    /*
     * This method is responsible for preparing the data to send and call the method for Conversation Service
     */
    private sendData() {
        let chatColumn = <HTMLElement>document.querySelector('#scrollingChat');
        chatColumn.classList.add('loading');
        let q = '';
        if (this.question != null) {
            q = this.question;
        }
        this.question = '';
        let context = null;
        if (this.response != null) {
            context = this.response.context;
            // we are going to delete the context variable 'callRetrieveAndRank' before
            // sending back to the Conversation service
            if (context && context.callRetrieveAndRank) {
                delete context.callRetrieveAndRank;
            }
        }
        let input = {'text': q};
        let payload = {input, context};

        let obj = {};
        obj ["text"] = q;
        obj ["isUser"] = true;


        // Add the user utterance to the list of chat segments
        this.segments.push(new Message(obj));
        // Call the method which calls the proxy for the message api
        this.callConversationService(chatColumn, payload);
    }


    private supplierChange(supplierName: string) {
        let productName = "Electric Turbine Blade";
        if (!this.response.context.suppliers) {
            this._supplierService.request(productName).subscribe(supplierData => {
                console.log("success retrieving supplier data");
                this.response.context.suppliers = supplierData;
                let suppliers = this.response.context.suppliers;


                suppliers = suppliers.reduce(function (map, obj) {
                    map[obj.name] = obj.cost;
                    return map;
                }, {});
                let supplierDetails: number = suppliers[supplierName];
                let extraInfo = "";
                let responseText;
                if (supplierDetails && (400000 - supplierDetails < 0)) {
                    extraInfo = "You are $" + (supplierDetails - 400000) + " under budget.";
                    responseText = supplierName + " is a 5 star supplier with on time delivery. " + extraInfo;
                    let payload = this.response.context;
                    let obj = {};
                    obj["text"] = responseText;
                    obj["isUser"] = false;
                    obj["payload"] = payload;
                    this.segments.push(new Message(obj));
                }
            }, error => {
                console.log(error);
            }, () => {
                console.log("Complete")
            });
        } else {

            let suppliers = this.response.context.suppliers;

            suppliers = suppliers.reduce(function (map, obj) {
                map[obj.name] = obj.cost;
                return map;
            }, []);
            let supplierDetails: number = suppliers[supplierName];
            let extraInfo = "";
            let responseText;
            if (supplierDetails && (400000 - supplierDetails < 0)) {
                extraInfo = "You are $" + supplierDetails + " under budget.";
                responseText = supplierName + " is a 5 star supplier with on time delivery. " + extraInfo;
                let payload = this.response.context;
                let obj = {};
                obj["text"] = responseText;
                obj["isUser"] = false;
                obj["payload"] = payload;
                this.segments.push(new Message(obj));

            }
        }


    }


    /*
      * This method is responsible for making a request to Conversation service with the corresponding user query.
      */

    private callConversationService(chatColumn, payload) {
        let responseText = '';
        let ce: any = null;

        // Send the user utterance to dialog, also send previous context
        this._dialogService.message(this.workspace_id, payload).subscribe(
            data1 => {
                this.response = data1;
                if (data1) {
                    if (data1.error) {
                        responseText = data1.error;
                        data1 = this.langData.NResponse;
                    } else if (data1.output) {
                        this.widgets = [];
                        if (data1.output.delay) {
                            let delays = data1.context.delays;
                            let newsWidget = null;

                            for (let delay of delays) {
                                let obj = {};
                                obj["title"] = delay.title;
                                obj["date"] = delay.date;
                                obj["desc"] = delay.body;
                                obj["source"] = delay.source;
                                obj["url"] = delay.sourceUrl;
                                if (delay.type === "news") {
                                    obj["icon_url"] = "img/newspaper.svg";
                                } else {
                                    obj["icon_url"] = "img/form.svg";
                                }
                                newsWidget = new Widget(obj);

                                this.widgets.push(newsWidget);
                            }
                            console.log(this.widgets);

                        } else if (data1.output.past_forms) {
                            let recentDocs = data1.context.recent_docs;
                            let mainRecentDocs  = JSON.parse(recentDocs);

                            for (let doc of mainRecentDocs) {
                                let obj = {};
                                console.log(doc.origin);
                                obj["origin"] = doc.origin;
                                console.log(doc.destination);
                                obj["destination"] = doc.destination;

                                obj["product"] = doc.product;
                                obj["cost"] = doc.cost;
                                obj["requestor"] = doc.requestor;
                                console.log(obj);
                                this.widgets.push(new Widget(obj));
                                console.log(this.widgets);

                            }

                        } else if(data1.output.other_supplier_options) {
                            console.log(data1.output.other_supplier_options);
                            this.suppliers = [];
                            let currentSupplier = data1.context.current_supplier;
                            let localSuppliers = data1.context.suppliers;
                            console.log(localSuppliers);
                            for (let supplier of localSuppliers) {
                                console.log(supplier);
                                if(!(supplier.name == currentSupplier)) {
                                    let obj = {};

                                    obj["name"] = supplier.name;
                                    obj["cost"] = supplier.cost;
                                    let supplierModel = new SupplierModel(obj);
                                    this.suppliers.push(supplierModel);


                                }
                            }

                        }
                        if (data1.output.text && data1.output.text.length >= 1) {
                            responseText = data1.output.text.join('<br>');
                        }
                    }
                    let obj = {};
                    obj["text"] = responseText;
                    obj["isUser"] = false;
                    obj["payload"] = data1;
                    if (this.widgets != null) {
                        obj["widgets"] = this.widgets;
                        console.log(obj["widgets"]);
                    }
                    if(this.suppliers!=null && this.suppliers.length>0) {
                        obj["suppliers"] = this.suppliers;
                    }


                    this.segments.push(new Message(obj));
                    chatColumn.classList.remove('loading');
                    if (this.timer) {
                        clearTimeout(this.timer);
                    }
                    this.timer = setTimeout(() => {
                        let messages = document.getElementById('scrollingChat').getElementsByClassName('clear');
                        this.scrollToBottom();
                    }, 50);
                }

            },
            error => {
                let serviceDownMsg = this.langData.Log;
                let obj = {};
                obj["text"] = serviceDownMsg;
                obj["isUser"] = false;
                obj["payload"] = this.langData.NResponse;
                this.segments.push(new Message(obj));
                chatColumn.classList.remove('loading');
            });
    }

    scrollToBottom(): void {
        const scrollPane: any = this.el
            .nativeElement.querySelector('.msg-container-base');
        scrollPane.scrollTop = scrollPane.scrollHeight;
    }
}
