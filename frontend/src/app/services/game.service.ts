import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GameNewRequest } from '../dtos/gameNewRequest';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActiveGameResponse } from '../dtos/activeGameResponse';
import { NewGameLocationResponse } from '../dtos/newGameLocationResponse';
import { ExternalInputContextResponse } from '../dtos/externalInputContextResponse';

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

  public createGameForClient(gameData: GameNewRequest, token: string): Observable<string> {
    const headers = new HttpHeaders().set('X-Client-Token', token);
    return this.http.post<string>(`${environment.baseUrl}/game/client`, gameData, { withCredentials: true, headers });
  }

  public getExternalInputContext(token: string): Observable<ExternalInputContextResponse> {
    const headers = new HttpHeaders().set('X-Client-Token', token);
    return this.http.get<ExternalInputContextResponse>(`${environment.baseUrl}/game/client/context`, { withCredentials: true, headers });
  }

  public getLocationsForNewGame(groupUuid: string): Observable<NewGameLocationResponse[]> {
    return this.http.get<NewGameLocationResponse[]>(`${environment.baseUrl}/game/locations`, {
      withCredentials: true,
      params: { groupUuid }
    });
  }

  public getLastLocationGame(token: string): Observable<ActiveGameResponse> {
    const headers = new HttpHeaders().set('X-Screen-Token', token);
    return this.http.get<ActiveGameResponse>(`${environment.baseUrl}/game/location/last`, { withCredentials: true, headers });
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
