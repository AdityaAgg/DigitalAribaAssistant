import {
    Component, Input

} from 'angular2/core';
import {Widget} from "./widget.model";


@Component({
    selector: 'widget-component',

    template: `<div class="carousel-item-src">
        <div class="pictogram">
            <!--  <div class=pictogram-container>-->
            <img [src]="widget.icon_url" />
            <!--  </div>-->
        </div>
        <div class="main-content">
            <div class="header">
                <div class="date-of-source"> 6/12/17 </div>
                &nbsp;&nbsp;
                <div class="news-source"> Al Jazeera </div>


            </div>
            <div class="body-of-item">
                <div class="title-carousel"> {{widget.title}} </div>
                <div class="desc-carousel"> Qatar is facing higher costs in selling its energy supplies.  </div>
            </div>
        </div>
    </div>
        `
})
export class WidgetComponent {


    @Input() widget: Widget;

}
