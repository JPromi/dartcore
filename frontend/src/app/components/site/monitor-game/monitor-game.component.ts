import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GameService } from '../../../services/game.service';
import { GameWsService } from '../../../services/game-ws.service';
import { ActiveGameResponse } from '../../../dtos/activeGameResponse';
import { CommonModule } from '@angular/common';
import { GamePlayerTileComponent } from '../../assets/game-player-tile/game-player-tile.component';
import { ActiveGamePlayerResponse } from '../../../dtos/activeGamePlayerResponse';

@Component({
  selector: 'app-monitor-game',
  imports: [
    CommonModule,
    GamePlayerTileComponent
  ],
  templateUrl: './monitor-game.component.html',
  styleUrl: './monitor-game.component.scss'
})
export class MonitorGameComponent implements OnInit, AfterViewInit, OnDestroy {

  @ViewChildren('playerTile', { read: ElementRef }) private playerTiles!: QueryList<ElementRef<HTMLElement>>;

  constructor(
    private activeRoute: ActivatedRoute,
    private gameService: GameService,
    private gameWsService: GameWsService
  ) { }

  public game: ActiveGameResponse | null = null;
  public currentPlayer: ActiveGamePlayerResponse | null = null; 
  public displayPlayers: ActiveGamePlayerResponse[] = [];
  public currentDisplayPlayer: ActiveGamePlayerResponse | null = null;
  public queuedDisplayPlayers: ActiveGamePlayerResponse[] = [];

  private token: string | null = null;
  private lastTileRects = new Map<string, DOMRectReadOnly>();
  private tileAnimationFrame: number | null = null;

  ngOnInit(): void {
    this.activeRoute.params.subscribe(params => {
      const monitorToken = params['token'];
      this.token = monitorToken;

      this.getActiveGames();
    });
  }

  ngAfterViewInit(): void {
    this.lastTileRects = this.captureTileRects();
  }

  ngOnDestroy(): void {
    if (this.tileAnimationFrame !== null) {
      cancelAnimationFrame(this.tileAnimationFrame);
    }
    localStorage.removeItem('dcn.screen');
  }

  private connectWebSocket(gameUuid: string, token: string): void {
    this.gameWsService.connect(gameUuid, token, 'screen');

    this.gameWsService.game$.subscribe((gameUpdate: ActiveGameResponse) => {
      this.updateGame(gameUpdate);
    });
  }

  private getActiveGames(): void {
    this.gameService.getActiveGamesAccount(this.token, 'screen').subscribe(games => {
      if (games.length > 0) {
        const activeGame = games[0];
        this.updateGame(activeGame);
        this.connectWebSocket(activeGame.uuid, this.token!);
      }
    });
  }

  private updateGame(gameUpdate: ActiveGameResponse): void {
    const previousRects = this.captureTileRects();

    this.game = gameUpdate;
    this.displayPlayers = this.buildDisplayPlayers(gameUpdate.players);
    this.currentDisplayPlayer = this.displayPlayers[0] ?? null;
    this.queuedDisplayPlayers = this.displayPlayers.slice(1);
    this.getActivePlayer();

    this.animateTileLayout(previousRects);
  }

  private getActivePlayer(): void {
    if(this.game) {
      const activePlayer = this.game.players.find(player => player.isCurrentPlayer);
      this.currentPlayer = activePlayer ? activePlayer : null;
    }
  }

  private buildDisplayPlayers(players: ActiveGamePlayerResponse[]): ActiveGamePlayerResponse[] {
    const orderedPlayers = [...players].sort((left, right) => left.orderIndex - right.orderIndex);
    const currentIndex = orderedPlayers.findIndex(player => player.isCurrentPlayer);

    if (currentIndex <= 0) {
      return orderedPlayers;
    }

    return [
      ...orderedPlayers.slice(currentIndex),
      ...orderedPlayers.slice(0, currentIndex)
    ];
  }

  private captureTileRects(): Map<string, DOMRectReadOnly> {
    const rects = new Map<string, DOMRectReadOnly>();

    if (!this.playerTiles) {
      return rects;
    }

    this.playerTiles.forEach((tile) => {
      const uuid = tile.nativeElement.dataset['playerUuid'];
      if (uuid) {
        rects.set(uuid, tile.nativeElement.getBoundingClientRect());
      }
    });

    return rects;
  }

  private animateTileLayout(previousRects: Map<string, DOMRectReadOnly>): void {
    if (!previousRects.size || !this.playerTiles) {
      this.lastTileRects = this.captureTileRects();
      return;
    }

    if (this.tileAnimationFrame !== null) {
      cancelAnimationFrame(this.tileAnimationFrame);
    }

    this.tileAnimationFrame = requestAnimationFrame(() => {
      this.tileAnimationFrame = requestAnimationFrame(() => {
        this.playerTiles.forEach((tile) => {
          const element = tile.nativeElement;
          const uuid = element.dataset['playerUuid'];
          const previousRect = uuid ? previousRects.get(uuid) : null;

          if (!uuid || !previousRect) {
            return;
          }

          const nextRect = element.getBoundingClientRect();
          const deltaX = previousRect.left - nextRect.left;
          const deltaY = previousRect.top - nextRect.top;
          const scaleX = previousRect.width / nextRect.width;
          const scaleY = previousRect.height / nextRect.height;
          const travelDistance = Math.hypot(deltaX, deltaY);

          if (!deltaX && !deltaY && scaleX === 1 && scaleY === 1) {
            return;
          }

          const duration = Math.min(700, Math.max(320, 220 + travelDistance * 0.45));

          element.animate(
            [
              {
                transform: `translate3d(${deltaX}px, ${deltaY}px, 0) scale(${scaleX}, ${scaleY})`,
                transformOrigin: 'top left'
              },
              {
                transform: 'translate3d(0, 0, 0) scale(1, 1)',
                transformOrigin: 'top left'
              }
            ],
            {
              duration,
              easing: 'cubic-bezier(0.16, 1, 0.3, 1)',
              fill: 'both'
            }
          );
        });

        this.lastTileRects = this.captureTileRects();
      });
    });
  }
}
