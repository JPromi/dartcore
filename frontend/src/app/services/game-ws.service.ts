import { Injectable } from '@angular/core';
import { Client, IMessage, Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Subject } from 'rxjs';
import { ActiveGamePlayerResponse } from '../dtos/activeGamePlayerResponse';
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

  private gameSubject = new Subject<ActiveGameResponse>();
  public game$ = this.gameSubject.asObservable();

  public connect(gameUuid: string, token: string | null = null, type: 'screen' | 'session' | 'client' = 'session'): void {
    if (this.stompClient) {
      this.stompClient.deactivate(); // sauber trennen
    }

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(`${environment.baseUrl}/../ws`),
      reconnectDelay: 5000,
      debug: str => console.log(str),
      connectHeaders: {
        ...(token && type === 'screen' ? { 'X-Screen-Token': token } : {}),
        ...(token && type === 'client' ? { 'X-Client-Token': token } : {})
      }
    });

    this.stompClient.onConnect = () => {
      this.connected = true;

      this.stompClient?.subscribe(
        `/response/game/${gameUuid}`,
        (message: IMessage) => {
          const body: ActiveGameResponse = JSON.parse(message.body);
          this.gameSubject.next(body);
        }
      );
    };

    this.stompClient.onStompError = frame => {
      this.connected = false;
    };

    this.stompClient.onWebSocketClose = () => {
      this.connected = false;
    }

    this.stompClient.activate();
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
