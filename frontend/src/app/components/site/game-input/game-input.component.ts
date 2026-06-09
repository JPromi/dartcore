import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { ActiveGameResponse } from '../../../dtos/activeGameResponse';
import { GameTypeEnum } from '../../../enums/gameTypeEnum';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import * as fa from '@fortawesome/free-solid-svg-icons';
import { GameThrowMultiplierEnum } from '../../../enums/gameThrowMultiplierEnum';
import { ActiveGamePlayerResponse } from '../../../dtos/activeGamePlayerResponse';
import { GameThrow } from '../../../entities/gameThrow';
import { GameThrowTypeEnum } from '../../../enums/gameThtowTypeEnum';
import { LoGameCalculationService } from '../../../services/local/lo-game-calculation.service';
import { GameService } from '../../../services/game.service';
import { ActivatedRoute, Router } from '@angular/router';
import { GameWsService } from '../../../services/game-ws.service';
import { ActiveGameThrowRequest } from '../../../dtos/activeGameThrowRequest';
import { PopupComponent } from '../../assets/popup/popup.component';
import { GamePlayerTileComponent } from '../../assets/game-player-tile/game-player-tile.component';

@Component({
  selector: 'app-game-input',
  imports: [
    CommonModule,
    FormsModule,
    TranslateModule,
    FontAwesomeModule,
    PopupComponent,
    GamePlayerTileComponent
  ],
  templateUrl: './game-input.component.html',
  styleUrl: './game-input.component.scss'
})
export class GameInputComponent implements OnInit, OnDestroy {

  constructor(
    private loGameCalculationService: LoGameCalculationService,
    private translate: TranslateService,
    private gameService: GameService,
    private activeRoute: ActivatedRoute,
    private gameWsService: GameWsService,
    private elementRef: ElementRef<HTMLElement>,
    private changeDetectorRef: ChangeDetectorRef,
    private router: Router
  ) { }

  fa = fa;
  gameThrowMultiplierEnum = GameThrowMultiplierEnum;
  gameThrowTypeEnum = GameThrowTypeEnum;

  game: ActiveGameResponse = new ActiveGameResponse();
  playerDisplayList: ActiveGamePlayerResponse[] = [];

  popupEndGameShow: boolean = false;

  showInput: boolean = true;

  currentGameTime = "";
  inputType = "keys"; // keys, board
  multiplier: GameThrowMultiplierEnum = GameThrowMultiplierEnum.SINGLE;
  isOnStartFullscreen = false;
  isFullscreen = false;
  keys = [
    { key: "1", value: 1 },
    { key: "2", value: 2 },
    { key: "3", value: 3 },
    { key: "4", value: 4 },
    { key: "5", value: 5 },
    { key: "6", value: 6 },
    { key: "7", value: 7 },
    { key: "8", value: 8 },
    { key: "9", value: 9 },
    { key: "10", value: 10 },
    { key: "11", value: 11 },
    { key: "12", value: 12 },
    { key: "13", value: 13 },
    { key: "14", value: 14 },
    { key: "15", value: 15 },
    { key: "16", value: 16 },
    { key: "17", value: 17 },
    { key: "18", value: 18 },
    { key: "19", value: 19 },
    { key: "20", value: 20 },
    { key: "25", value: 25 }
  ]

  currentGameTimeInterval: any = null;

  ngOnInit(): void {
    this.activeRoute.params.subscribe(params => {
      const uuid = params['uuid'];
      if (uuid) {
        this.gameWsService.connect(uuid);
        this._loadGame(uuid);

        // WS Player Update
        this.gameWsService.game$.subscribe((gameUpdate: ActiveGameResponse) => {
          this.applyGameUpdate(gameUpdate);
        });
      }
    });
  }

  ngOnDestroy(): void {
    if (this.currentGameTimeInterval) {
      clearInterval(this.currentGameTimeInterval);
      this.currentGameTimeInterval = null;
    }
  }

  public isButtonDisabled(key: number): boolean {
    if(this.multiplier === GameThrowMultiplierEnum.TRIPLE && key == 25) {
      return true;
    } else {
      return false;
    }
  }

  public pointsInput(keyValue: number): void {

    if(keyValue === 25 && this.multiplier === GameThrowMultiplierEnum.TRIPLE) {
      this.multiplier = GameThrowMultiplierEnum.SINGLE;
    }
    if(keyValue === 0) {
      this.multiplier = GameThrowMultiplierEnum.SINGLE;
    }

    // send
    const throwRequest = new ActiveGameThrowRequest(
      keyValue === 0 ? GameThrowTypeEnum.MISS : GameThrowTypeEnum.THROW,
      keyValue,
      this.multiplier
    )

    this.gameWsService.sendThrow(this.game.uuid, throwRequest);

    this.multiplier = GameThrowMultiplierEnum.SINGLE;
  }

  public undoLastThrow(): void {
    
    const throwRequest = new ActiveGameThrowRequest(
      GameThrowTypeEnum.THROW,
      0,
      null,
      true
    )

    this.gameWsService.sendThrow(this.game.uuid, throwRequest);
  }

  public toggleFullscreen() {
    if (document.fullscreenElement) {
      document.exitFullscreen();
      this.isFullscreen = false;
    } else {
      document.documentElement.requestFullscreen();
      this.isFullscreen = true;
    }
  }

  public endGame(isConfirmed: boolean): void {
    if(isConfirmed) {
      this.gameService.endGame(this.game.uuid).subscribe(
        {
          next: () => {
            this.gameWsService.disconnect();
            this.router.navigate(['/']);
          },
          error: () => {
            this.gameWsService.disconnect();
            this.router.navigate(['/']);
          }
        }
      );
    }
  }

  private gameTime() {
    this._setGameTime();
    this.currentGameTimeInterval = setInterval(() => {
      this._setGameTime();
    }, 1000);
  }

  private _setGameTime() {
    const now = new Date();
    if (this.game.startTime) {
      const startTime = new Date(this.game.startTime);
      const endTime = this.game.endTime ? new Date(this.game.endTime) : now;
      const seconds = Math.floor((endTime.getTime() - startTime.getTime()) / 1000);
      const outputHours = Math.floor(seconds / 3600);
      const outputMinutes = Math.floor((seconds % 3600) / 60).toString().padStart(2, '0');
      const outputSeconds = (seconds % 60).toString().padStart(2, '0');
      this.currentGameTime = (outputHours > 0 ? outputHours + ':' : '') + outputMinutes + ':' + outputSeconds;
    }
  }

  private sortPlayers() {
    const currentPlayer = this.game.players.find(p => p.isCurrentPlayer);
    if (!currentPlayer) {
      return [...this.game.players].sort((a, b) => a.orderIndex - b.orderIndex);
    }

    const currentIndex = currentPlayer.orderIndex;
    const total = this.game.players.length;

    return [...this.game.players].sort((a, b) => {
      if (a.isCurrentPlayer) return -1;
      if (b.isCurrentPlayer) return 1;

      if (a.isEliminated && !b.isEliminated) return 1;
      if (!a.isEliminated && b.isEliminated) return -1;

      const distA = (a.orderIndex - currentIndex + total) % total;
      const distB = (b.orderIndex - currentIndex + total) % total;
      return distA - distB;
    });
  }

  private applyGameUpdate(gameUpdate: ActiveGameResponse): void {
    const previousPositions = this.getPlayerTopPositions();
    this.game = gameUpdate;
    this.playerDisplayList = this.sortPlayers();
    this.changeDetectorRef.detectChanges();

    requestAnimationFrame(() => {
      this.animatePlayerReorder(previousPositions);
    });
  }

  private getPlayerTopPositions(): Map<string, number> {
    const positions = new Map<string, number>();
    const playerElements = this.elementRef.nativeElement.querySelectorAll<HTMLElement>('.players .player[data-player-uuid]');

    playerElements.forEach((element) => {
      const playerUuid = element.dataset['playerUuid'];
      if (playerUuid) {
        positions.set(playerUuid, element.getBoundingClientRect().top);
      }
    });

    return positions;
  }

  private animatePlayerReorder(previousPositions: Map<string, number>): void {
    if (previousPositions.size === 0) {
      return;
    }

    const playerElements = this.elementRef.nativeElement.querySelectorAll<HTMLElement>('.players .player[data-player-uuid]');

    playerElements.forEach((element) => {
      const playerUuid = element.dataset['playerUuid'];
      if (!playerUuid) {
        return;
      }

      const previousTop = previousPositions.get(playerUuid);
      if (previousTop === undefined) {
        return;
      }

      const currentTop = element.getBoundingClientRect().top;
      const deltaY = previousTop - currentTop;

      if (Math.abs(deltaY) < 1) {
        return;
      }

      element.animate(
        [
          { transform: `translateY(${deltaY}px)` },
          { transform: 'translateY(0)' }
        ],
        {
          duration: 280,
          easing: 'ease'
        }
      );
    });
  }

  private checkIsFullscreen() {
    if((window.innerWidth == screen.width && window.innerHeight == screen.height) || !document.fullscreenEnabled) {
      this.isOnStartFullscreen = true;
    } else {
      this.isOnStartFullscreen = false;
    }
  }

  private _loadGame(uuid: string): void {
    this.gameService.getGame(uuid).subscribe(
      (response: ActiveGameResponse) => {
        this.applyGameUpdate(response);
        this.checkIsFullscreen();
        this.gameTime();
      }
    );
  }
}
