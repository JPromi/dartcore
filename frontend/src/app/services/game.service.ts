import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GameNewRequest } from '../dtos/gameNewRequest';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActiveGameResponse } from '../dtos/activeGameResponse';

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
