import { Component, OnInit } from '@angular/core';
import { RegisterService } from '../../../services/register.service';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { LoadingComponent } from '../../assets/loading/loading.component';
import { LoadingType } from '../../../enums/loadingType';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-register-token',
  imports: [
    CommonModule,
    LoadingComponent,
    RouterLink,
    TranslateModule,
],
  templateUrl: './register-token.component.html',
  styleUrl: './register-token.component.scss'
})
export class RegisterTokenComponent implements OnInit{

  constructor(
    private registerService: RegisterService,
    private activatedRoute: ActivatedRoute,
  ) { }

  LoadingType = LoadingType;
  isLoading = true;
  username?: string;

  ngOnInit(): void {
    setTimeout(() => {
      this.activatedRoute.params.subscribe(params => {
        const token = params['token'];
        this._activateAccount(token);
      });
    }, 2000);
  }


  private _activateAccount(token: string) {
    this.isLoading = true;
    this.registerService.validateToken(token).subscribe(
      (response) => {
        this.username = response;
        this.isLoading = false;
      },
      (error) => {
        this.isLoading = false;
        this.username = undefined;
      }
    );
  }

}
