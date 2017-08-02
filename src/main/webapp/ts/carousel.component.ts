import {ChangeDetectionStrategy, Component, Input} from 'angular2/core';
import {Widget} from "./widget.model";
import {WidgetComponent} from "./widget.component"
import {FormWidgetComponent} from "./form.widget.component";
import {AlertWidgetComponent} from "./alert.widget.component";

declare var $: JQueryStatic;

@Component({
    selector: 'carousel-component',
    directives: [FormWidgetComponent, AlertWidgetComponent],
    changeDetection: ChangeDetectionStrategy.CheckOnce,
    template: `
        <div id="carouselExampleIndicators" class="carousel slide" data-ride="carousel">

            <div class="carousel-inner" role="listbox">
                <div *ngFor="let widget of widgets; let i = index" [ngClass]="{'active' :i==0, 'carousel-item':true}">
                    <div *ngIf="widget.product!=null">
                        <form-widget-component [widget]="widget"></form-widget-component>
                    </div>

                    <div *ngIf="widget.product==null">
                        <alert-widget-component [widget]="widget"></alert-widget-component>
                    </div>
                </div>

            </div>

            <ol class="carousel-indicators">

                <li *ngFor="let widget of widgets; let i = index" data-target="#carouselExampleIndicators"
                    [attr.data-slide-to]="i" [ngClass]="{'carousel-indicator':true, 'active' :i==0}"></li>

            </ol>


        </div>`
})
export class CarouselComponent {
    @Input() widgets: Widget[];

    ngAfterViewInit() {
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
    }

}
