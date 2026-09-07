import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ActiveGamePlayerResponse } from '../../../dtos/activeGamePlayerResponse';
import { GameThrowMultiplierEnum } from '../../../enums/gameThrowMultiplierEnum';
import { GameThrowTypeEnum } from '../../../enums/gameThtowTypeEnum';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { GameThrow } from '../../../entities/gameThrow';
import { LoGameCalculationService } from '../../../services/local/lo-game-calculation.service';
import { GameHintResponse } from '../../../dtos/gameHintResponse';

@Component({
  selector: 'asset-game-player-tile',
  imports: [
    CommonModule,
    TranslateModule
  ],
  templateUrl: './game-player-tile.component.html',
  styleUrl: './game-player-tile.component.scss'
})
export class GamePlayerTileComponent {

  constructor(
    private translate: TranslateService,
    private loGameCalculationService: LoGameCalculationService
  ) { }

  @Input() player!: ActiveGamePlayerResponse;

  public gameThrowTypeEnum = GameThrowTypeEnum;

  public getReadablePoints(gameThrow: GameThrow): string {
    switch (gameThrow.type) {
      case GameThrowTypeEnum.THROW:
        return `${this.loGameCalculationService.getThrowMultiplierChar(gameThrow.multiplier)}${gameThrow.score}`;
        break;

      case GameThrowTypeEnum.MISS:
        return this.translate.instant("page.game.active.player.points.miss.text");
        break;

      default:
        return "";
        break;
    }
  }

  public getReadableHintPoints(gameThrow: GameHintResponse): string {
    return `${this.loGameCalculationService.getThrowMultiplierChar(gameThrow.multiplier)}${gameThrow.points}`;
  }

  public toFixedNumber(value: number | null, digits: number): string {
    if(value === null) {
      return "-";
    } else {
      return parseFloat(value.toFixed(digits)).toString();
    }
  }
}
