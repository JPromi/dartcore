import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoginRequest } from '../dtos/loginRequest';
import { Observable } from 'rxjs';
import { LoginResponse } from '../dtos/loginResponse';
import { environment } from '../../environments/environment';
import { SessionAccountResponse } from '../dtos/sessionAccountResponse';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(
    private http: HttpClient,
  ) { }

  public login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.baseUrl}/auth`, request, { withCredentials: true });
  }

  public loginTotp(request: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.baseUrl}/auth/totp`, request, { withCredentials: true });
  }

  public loginSession(): Observable<LoginResponse> {
    return this.http.get<LoginResponse>(`${environment.baseUrl}/auth`, { withCredentials: true });
  }

  public session(): Observable<SessionAccountResponse> {
    return this.http.get<SessionAccountResponse>(`${environment.baseUrl}/auth/session`, { withCredentials: true });
  }

  public logout(): Observable<void> {
    return this.http.delete<void>(`${environment.baseUrl}/auth`, { withCredentials: true });
  }
}
