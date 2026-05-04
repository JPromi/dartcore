import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'auth-router',
  imports: [
    RouterOutlet,
    RouterModule,
    TranslateModule
  ],
  templateUrl: './auth-router.component.html',
  styleUrl: './auth-router.component.scss'
})
export class AuthRouterComponent {

}
