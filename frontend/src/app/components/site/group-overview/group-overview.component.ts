import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-group-overview',
  imports: [
    CommonModule,
    TranslateModule
  ],
  templateUrl: './group-overview.component.html',
  styleUrl: './group-overview.component.scss'
})
export class GroupOverviewComponent {

}
