import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GameNewRequest } from '../dtos/gameNewRequest';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActiveGameResponse } from '../dtos/activeGameResponse';
import { NewGameLocationResponse } from '../dtos/newGameLocationResponse';

@Injectable({
  providedIn: 'root'
})
export class GameService {

  constructor(
    private http: HttpClient,
  ) { }

  public getActiveGamesAccount(token: string | null = null, type: 'screen' | 'session' | 'client' = 'session'): Observable<ActiveGameResponse[]> {
    let headers = new HttpHeaders();

    if (token && type === 'screen') {
      headers = headers.set('X-Screen-Token', token);;
    } else if (token && type === 'client') {
      headers = headers.set('X-Client-Token', token);
    }
    return this.http.get<ActiveGameResponse[]>(`${environment.baseUrl}/game/active`, { withCredentials: true, headers });
  }

  public createGame(gameData: GameNewRequest): Observable<string> {
    return this.http.post<string>(`${environment.baseUrl}/game`, gameData, { withCredentials: true });
  }

  public getLocationsForNewGame(groupUuid: string): Observable<NewGameLocationResponse[]> {
    return this.http.get<NewGameLocationResponse[]>(`${environment.baseUrl}/game/locations`, {
      withCredentials: true,
      params: { groupUuid }
    });
  }

  public getGame(uuid: string, token: string | null = null, type: 'screen' | 'session' | 'client' = 'session'): Observable<any> {
    let headers = new HttpHeaders();

    if (token && type === 'screen') {
      headers = headers.set('X-Screen-Token', token);
    } else if (token && type === 'client') {
      headers = headers.set('X-Client-Token', token);
    }

    return this.http.get<any>(`${environment.baseUrl}/game/${uuid}`, { withCredentials: true, headers });
  }

  public endGame(uuid: string): Observable<void> {
    return this.http.delete<void>(`${environment.baseUrl}/game/${uuid}`, { withCredentials: true });
  }
}
