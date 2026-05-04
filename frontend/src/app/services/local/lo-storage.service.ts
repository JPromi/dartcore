import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { SessionAccountResponse } from '../../dtos/sessionAccountResponse';

@Injectable({
  providedIn: 'root'
})
export class LoStorageService {

  constructor() { }

  private sessionAccount = new BehaviorSubject<SessionAccountResponse | null>(null);
  public sessionAccount$ = this.sessionAccount.asObservable();
  
  private errorCode = new BehaviorSubject<number | null>(null);
  public errorCode$ = this.errorCode.asObservable();

  public setSessionAccount(account: SessionAccountResponse | null): void {
    this.sessionAccount.next(account);
  }

  public setErrorCode(code: number | null): void {
    this.errorCode.next(code);
  }

  public setErrorCodeFromResponse(error: any): void {
    console.log('Error:', error);
    if (!error.ok) {
      console.log('Error status:', error.status);
      if(error.status == 0) {
        console.log('Error status: 0');
        this.errorCode.next(504);
      } else {
        console.log('Error status:', error.status);
        this.errorCode.next(error.status);
      }
    }
  }
}
