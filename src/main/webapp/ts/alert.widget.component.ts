import {
    Component, Input

} from 'angular2/core';
import {Widget} from "./widget.model";
import {WidgetComponent} from "./widget.component";


@Component({
    selector: 'alert-widget-component',

    template: `
        <div class="carousel-item-src">
            <div class="pictogram">
                <img [src]="widget.icon_url"/>
            </div>
            <div class="main-content">
                <div class="header">
                    <div class="date-of-source"> {{widget.date | date:'shortDate'}}</div>
                    &nbsp;&nbsp;
                    <div class="news-source"> {{widget.source}}</div>


                </div>
                <div class="body-of-item">
                    <div class="title-carousel"> {{widget.title}}</div>
                    <div class="desc-carousel"> {{widget.desc}}</div>
                </div>
            </div>
        </div>
    `
})
export class AlertWidgetComponent extends WidgetComponent {

}
