import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LoadingComponent } from '../../assets/loading/loading.component';
import { LoadingType } from '../../../enums/loadingType';
import { GameService } from '../../../services/game.service';

@Component({
  selector: 'app-external-input-router',
  imports: [CommonModule, LoadingComponent],
  templateUrl: './external-input-router.component.html',
  styleUrl: './external-input-router.component.scss'
})
export class ExternalInputRouterComponent implements OnInit {

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private gameService: GameService
  ) { }

  LoadingType = LoadingType;
  hasError = false;

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const token = params['token'];
      if (!token) {
        this.hasError = true;
        return;
      }

      this.setClientCookie(token);
      this.gameService.getExternalInputContext(token).subscribe({
        next: context => {
          if (context.activeGameUuid) {
            this.router.navigate(['/', 'game', 'active', context.activeGameUuid], {
              queryParams: { clientToken: token },
              replaceUrl: true
            });
          } else {
            this.router.navigate(['/', 'ex', 'input', token, 'create'], {
              queryParams: {
                group: context.groupUuid,
                location: context.locationUuid,
                clientToken: token,
                lockedLocation: true
              },
              replaceUrl: true
            });
          }
        },
        error: () => {
          this.hasError = true;
        }
      });
    });
  }

  private setClientCookie(token: string): void {
    document.cookie = `dcn.client=${encodeURIComponent(token)};${this.cookieDomain()}path=/;max-age=${60 * 60 * 24};${this.secureCookie()}SameSite=Lax`;
  }

  private cookieDomain(): string {
    const hostname = window.location.hostname;
    if (hostname === 'localhost' || hostname === '127.0.0.1' || !hostname.includes('.')) {
      return '';
    }
    return `domain=.${hostname.split('.').slice(-2).join('.')};`;
  }

  private secureCookie(): string {
    return window.location.protocol === 'https:' ? 'secure;' : '';
  }
}
