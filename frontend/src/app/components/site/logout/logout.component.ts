import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { LoStorageService } from '../../../services/local/lo-storage.service';
import { Router } from '@angular/router';
import { environment } from '../../../../environments/environment';
import { LoadingComponent } from '../../assets/loading/loading.component';
import { LoadingType } from '../../../enums/loadingType';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-logout',
  imports: [
    LoadingComponent,
    TranslateModule
  ],
  templateUrl: './logout.component.html',
  styleUrl: './logout.component.scss'
})
export class LogoutComponent implements OnInit {
  
  constructor(
    private authService: AuthService,
    private loStorageService: LoStorageService,
    private router: Router
  ) { }

  public loadingType = LoadingType.BOUNCING_DOTS;

  ngOnInit(): void {
    this.logout();
  }
  
  public logout() {
    this.authService.logout().subscribe(
      (response) => {
        document.cookie = `dcn.session=;domain=${environment.rootUrl};path=/;max-age=${0};secure=true;SameSite=Lax`;
        this.loStorageService.setSessionAccount(null);
        this.router.navigate(['/barrier/login']);
      },
      (error) => {
        console.log(error);
        this.router.navigate(['/barrier/login']);
      }
    );
  }
}
