import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { SessionAccountResponse } from '../../../dtos/sessionAccountResponse';
import { LoStorageService } from '../../../services/local/lo-storage.service';
import { ProfileResponse } from '../../../dtos/profileResponse';
import { ProfileService } from '../../../services/profile.service';
import { CommonModule } from '@angular/common';
import { ErrorPageComponent } from '../../assets/error-page/error-page.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import * as fa from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-profile',
  imports: [
    CommonModule,
    ErrorPageComponent,
    FontAwesomeModule,
    TranslateModule,
    RouterModule
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  constructor(
    private activeRoute: ActivatedRoute,
    private loStorageService: LoStorageService,
    private profileService: ProfileService
  ) { }

  fa = fa;

  public isOwnProfile = false;
  public sessionAccount: SessionAccountResponse | null = null;
  public errorCode!: number;
  public profile!: ProfileResponse;
  public isCurrentUser = false;

  ngOnInit(): void {
    this.activeRoute.params.subscribe(params => {
      let username = params['username'];
      
      this.loStorageService.sessionAccount$.subscribe(
        (sessionAccount) => {
          this.sessionAccount = sessionAccount;
          
          if(!username || username === this.sessionAccount?.username) {
            this.isOwnProfile = true;
            if(this.sessionAccount) {
              username = this.sessionAccount.username;
            }
          } else {
            this.isOwnProfile = false;
          }

          // load profile
          this.getProfile(username);
        }
      );
    });
  }

  private getProfile(username: string) {
    this.profileService.getProfile(username).subscribe(
      (response) => {
        this.profile = response;

        if (this.sessionAccount) {
          this.isCurrentUser = this.sessionAccount.username === this.profile.username;
        }
      },
      (error) => {
        console.error(error);
        this.errorCode = error.status;
      }
    );
  }

}
