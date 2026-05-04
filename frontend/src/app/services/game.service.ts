import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GameNewRequest } from '../dtos/gameNewRequest';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GameService {

  constructor(
    private http: HttpClient,
  ) { }

  public createGame(gameData: GameNewRequest): Observable<string> {
    return this.http.post<string>(`${environment.baseUrl}/game`, gameData, { withCredentials: true });
  }

  public getGame(uuid: string): Observable<any> {
    return this.http.get<any>(`${environment.baseUrl}/game/${uuid}`, { withCredentials: true });
  }

  public endGame(uuid: string): Observable<void> {
    return this.http.delete<void>(`${environment.baseUrl}/game/${uuid}`, { withCredentials: true });
  }
}
