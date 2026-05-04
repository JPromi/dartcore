import { Component, OnInit } from '@angular/core';
import { Login } from '../../../entities/login';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { LoginRequest } from '../../../dtos/loginRequest';
import { LoginResponse } from '../../../dtos/loginResponse';
import { Router, RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';
import { PastUser } from '../../../entities/pastUser';
import { CookieService } from 'ngx-cookie-service';
import { LoStorageService } from '../../../services/local/lo-storage.service';
import { environment } from '../../../../environments/environment';
import { SessionAccountResponse } from '../../../dtos/sessionAccountResponse';
import { LoAuthService } from '../../../services/local/lo-auth.service';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import * as fa from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    RouterModule,
    TranslateModule,
    CommonModule,
    FontAwesomeModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  constructor(
    private authService: AuthService,
    private router: Router,
    private loAuthService: LoAuthService,
    private loStorageService: LoStorageService,
  ) { }

  fa = fa;

  public loginObject: Login = new Login();
  public loginResponse?: LoginResponse;
  public isLoading: boolean = false;
  public isPasswordVisible: boolean = false;
  public isSavedUser: boolean = false;
  public lastUsers: PastUser[] = [];

  public sessionAccount?: SessionAccountResponse

  ngOnInit(): void {
    this.getLastUsers();

    this.getSession();
  }

  public login() {
    const request: LoginRequest = LoginRequest.fromLogin(this.loginObject);
    
    this.loginResponse = undefined;
    this.isLoading = true;

    this.authService.login(request).subscribe(
      (response) => {
        this.setSession(response);
        this.loginResponse = response;
        this.isLoading = false;
        if(response.totpRequired) {
          this.loAuthService.setRememberMeTmp(this.loginObject.rememberMe);
          this.router.navigate(['/barrier/login/totp']);
        } else {
          this.getSession();
        }
      },
      (error) => {
        this.loginResponse = error.error;
        this.isLoading = false;
      }
    );
  }

  public selectUser(user: PastUser) {
    this.loginObject.username = user.username;
    this.isSavedUser = false;
  }

  private getLastUsers() {
    const lastUsers = this.loAuthService.getLastUsers();
    if (lastUsers) {
      if (lastUsers.length > 0) {
        this.lastUsers = lastUsers;
        this.isSavedUser = true;
      }
    }
    // const lastUsers = localStorage.getItem('dcn.lastUsers');
    // if (lastUsers) {
    //   const lastUsersArray = JSON.parse(lastUsers);
    //   if (lastUsersArray.length > 0) {
    //     this.lastUsers = lastUsersArray;
    //     this.isSavedUser = true;
    //   }
    // }
  }

  private setSession(loginResponse: LoginResponse) {
    document.cookie = `dcn.session=${loginResponse.token};domain=${environment.rootUrl};path=/;max-age=${60*60*24*365};secure=true;SameSite=Lax`;
    localStorage.setItem('dcn.session', loginResponse.token);
  }

  private getSession() {
    this.authService.session().subscribe(
      (response) => {
        if(this.loginObject.rememberMe) this.loAuthService.setRememberMe(response);
        this.loStorageService.setSessionAccount(response);
        this.sessionAccount = response;
        this.router.navigate(['/']);
      },
      (error) => {
        
      }
    );
  }
}
