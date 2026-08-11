import { Injectable } from '@angular/core';
import { Client, IMessage, Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Subject } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActiveGameThrowRequest } from '../dtos/activeGameThrowRequest';
import { ActiveGameResponse } from '../dtos/activeGameResponse';

@Injectable({
  providedIn: 'root'
})
export class GameWsService {

  constructor() { }
  
  private stompClient: Client | null = null;
  private connected = false;
  private pendingGameUuid: string | null = null;
  private subscribedGameUuids = new Set<string>();

  private gameSubject = new Subject<ActiveGameResponse>();
  public game$ = this.gameSubject.asObservable();

  private screenGameCreatedSubject = new Subject<ActiveGameResponse>();
  public screenGameCreated$ = this.screenGameCreatedSubject.asObservable();

  public connect(gameUuid: string | null, token: string | null = null, type: 'screen' | 'session' | 'client' = 'session'): void {
    if (this.stompClient) {
      this.stompClient.deactivate(); // sauber trennen
    }
    this.connected = false;
    this.pendingGameUuid = gameUuid;
    this.subscribedGameUuids.clear();

    const wsUrl = new URL(`${environment.baseUrl}/../ws`, window.location.origin);
    if (token && type === 'screen') {
      wsUrl.searchParams.set('screenToken', token);
    } else if (token && type === 'client') {
      wsUrl.searchParams.set('clientToken', token);
    }

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(wsUrl.toString()),
      reconnectDelay: 5000,
      debug: str => console.log(str),
      connectHeaders: {
        ...(token && type === 'screen' ? { 'X-Screen-Token': token } : {}),
        ...(token && type === 'client' ? { 'X-Client-Token': token } : {})
      }
    });

    this.stompClient.onConnect = () => {
      this.connected = true;

      if (gameUuid) {
        this.subscribeToGame(gameUuid);
      } else if (this.pendingGameUuid) {
        this.subscribeToGame(this.pendingGameUuid);
      }

      if (token && type === 'screen') {
        this.stompClient?.subscribe(
          `/response/location-screen/${token}/game-created`,
          (message: IMessage) => {
            const body: ActiveGameResponse = JSON.parse(message.body);
            this.screenGameCreatedSubject.next(body);
          }
        );
      }
    };

    this.stompClient.onStompError = frame => {
      this.connected = false;
    };

    this.stompClient.onWebSocketClose = () => {
      this.connected = false;
    }

    this.stompClient.activate();
  }

  public subscribeToGame(gameUuid: string): void {
    this.pendingGameUuid = gameUuid;

    if (this.stompClient?.connected && !this.subscribedGameUuids.has(gameUuid)) {
      this.subscribedGameUuids.add(gameUuid);
      this.stompClient?.subscribe(
        `/response/game/${gameUuid}`,
        (message: IMessage) => {
          const body: ActiveGameResponse = JSON.parse(message.body);
          this.gameSubject.next(body);
        }
      );
    }
  }

  public sendThrow(gameUuid: string, payload: ActiveGameThrowRequest): void {
    if (this.stompClient?.connected) {
      this.stompClient.publish({
        destination: `/data/game/${gameUuid}/throw`,
        body: JSON.stringify(payload),
      });
    } else {
      console.warn('STOMP not connected, cannot send throw');
    }
  }

  public disconnect(): void {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.connected = false;
      console.log('Disconnected from WS');
    }
  }

}
