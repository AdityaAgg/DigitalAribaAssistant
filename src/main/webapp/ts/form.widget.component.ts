import {
    Component, Input

} from 'angular2/core';
import {Widget} from "./widget.model";
import {WidgetComponent} from "./widget.component";


@Component({
    selector: 'form-widget-component',

    template: `
        <div class="carousel-item-src">
            <div class="pictogram">
                <img src="img/form.svg"/>
            </div>
            <div class="main-content">
                <div class="header">
                    <div class="date-of-source"> 7/23/17</div>
                    
                </div>
                <div class="body-of-item">
                    <div class="title-carousel"> {{widget.product}} </div>
                    <div class="desc-carousel"> {{widget.origin}} &rarr; {{widget.destination}} </div>
                    <div class="desc-carousel"> Requestor: {{widget.requestor}} </div>
                </div>
            </div>
        </div>
    `
})
export class FormWidgetComponent extends WidgetComponent {

}
