import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GameService } from '../../../services/game.service';
import { environment } from '../../../../environments/environment';
import { GameWsService } from '../../../services/game-ws.service';
import { ActiveGameResponse } from '../../../dtos/activeGameResponse';

@Component({
  selector: 'app-monitor-game',
  imports: [],
  templateUrl: './monitor-game.component.html',
  styleUrl: './monitor-game.component.scss'
})
export class MonitorGameComponent implements OnInit, OnDestroy {

  constructor(
    private activeRoute: ActivatedRoute,
    private gameService: GameService,
    private gameWsService: GameWsService
  ) { }

  private token: string | null = null;

  ngOnInit(): void {
    this.activeRoute.params.subscribe(params => {
      const monitorToken = params['token'];
      this.token = monitorToken;

      this.getGame();
    });
  }

  ngOnDestroy(): void {
    localStorage.removeItem('dcn.screen');
  }

  connectWebSocket(gameUuid: string, token: string): void {
    this.gameWsService.connect(gameUuid, token, 'screen');

    this.gameWsService.game$.subscribe((gameUpdate: ActiveGameResponse) => {
      console.log('Received game update via WebSocket:', gameUpdate);
    });
  }

  getGame(): void {
    this.gameService.getActiveGamesAccount(this.token, 'screen').subscribe(games => {
      console.log('Active games:', games);

      if (games.length > 0) {
        const game = games[0];
        this.connectWebSocket(game.uuid, this.token!);
      }
    });
  }
}
