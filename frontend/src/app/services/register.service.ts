import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RegistrationRequest } from '../dtos/registrationRequest';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(
    private http: HttpClient,
  ) { }

  public register(request: RegistrationRequest): Observable<any> {
    return this.http.post<any>(`${environment.baseUrl}/register`, request, { withCredentials: true });
  }

  public validateToken(token: string): Observable<string> {
    return this.http.put<string>(`${environment.baseUrl}/register/validate`, token, { withCredentials: true });
  }
}
