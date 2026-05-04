import { Component } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { LoStorageService } from '../../../services/local/lo-storage.service';
import { LoginResponse } from '../../../dtos/loginResponse';
import { TranslateModule } from '@ngx-translate/core';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../../environments/environment';
import { CommonModule } from '@angular/common';
import { LoAuthService } from '../../../services/local/lo-auth.service';
import { SessionAccountResponse } from '../../../dtos/sessionAccountResponse';

@Component({
  selector: 'app-totp',
  imports: [
    RouterModule,
    TranslateModule,
    FormsModule,
    CommonModule
  ],
  templateUrl: './totp.component.html',
  styleUrl: './totp.component.scss'
})
export class TotpComponent {
  
  constructor(
    private authService: AuthService,
    private router: Router,
    private loStorageService: LoStorageService,
    private loAuthService: LoAuthService,
  ) { }
  
  public totp: string = '';
  public loginResponse?: LoginResponse;
  public isLoading: boolean = false;

  public login() {
    this.loginResponse = undefined;
    this.isLoading = true;
    this.authService.loginTotp(this.totp).subscribe(
      (response) => {
        if(!response.error) {
          this.getSession();
        }
        this.loginResponse = response;
        this.isLoading = false;
      },
      (error) => {
        this.loginResponse = error.error;
        this.isLoading = false;
      }
    );
  }

  private getSession() {
    this.authService.session().subscribe(
      (response) => {
        this.rememberMe(response);
        this.loStorageService.setSessionAccount(response);
        this.router.navigate(['/']);
      },
      (error) => {
        
      }
    );
  }

  private rememberMe(session: SessionAccountResponse) {
    console.log('remember me check');
    var remember= this.loAuthService.isRememberMeTmp();
    if (remember) {
      console.log('remember me');
      this.loAuthService.setRememberMe(session);
      this.loAuthService.setRememberMeTmp(false);
    }
  } 
  
}
