import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { ActiveGamePlayerResponse } from '../../../dtos/activeGamePlayerResponse';

@Component({
  selector: 'asset-game-results',
  imports: [CommonModule, TranslateModule],
  templateUrl: './game-results.component.html',
  styleUrl: './game-results.component.scss'
})
export class GameResultsComponent {
  public resultPlayers: ActiveGamePlayerResponse[] = [];

  @Input() set players(players: ActiveGamePlayerResponse[]) {
    this.resultPlayers = [...players].sort((left, right) =>
      Number(right.isWinner) - Number(left.isWinner) || left.orderIndex - right.orderIndex
    );
  }
}
