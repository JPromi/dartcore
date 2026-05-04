import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-group-games',
  imports: [
    CommonModule,
    TranslateModule
  ],
  templateUrl: './group-games.component.html',
  styleUrl: './group-games.component.scss'
})
export class GroupGamesComponent {

}
