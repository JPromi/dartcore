import { CommonModule } from '@angular/common';
import { Component, ElementRef, HostListener, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { LoStorageService } from '../../../services/local/lo-storage.service';
import { SessionAccountResponse } from '../../../dtos/sessionAccountResponse';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import * as fa from '@fortawesome/free-solid-svg-icons';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
  trigger,
  state,
  style,
  animate,
  transition,
} from '@angular/animations';
import { ActiveGameResponse } from '../../../dtos/activeGameResponse';
import { interval, map, shareReplay, startWith } from 'rxjs';

@Component({
  selector: 'asset-nav-main',
  imports: [
    RouterModule,
    CommonModule,
    TranslateModule,
    FontAwesomeModule
  ],
  templateUrl: './nav-main.component.html',
  styleUrl: './nav-main.component.scss',
  animations: [
    trigger(
      'fade', [
        transition(':enter', [
          style({ opacity: 0, transform: 'translateY(98%)' }),
          animate('100ms', style({ opacity: 1, transform: 'translateY(100%)' }))
        ]),
        transition(':leave', [
          style({ opacity: 1, transform: 'translateY(100%)' }),
          animate('150ms', style({ opacity: 0, transform: 'translateY(98%)' }))
        ])
      ]
    ),
    trigger(
      'openGames', [
        transition(':enter', [
          style({ opacity: 1, height: '0' }),
          animate('100ms', style({ opacity: 1, height: '*' }))
        ]),
        transition(':leave', [
          style({ opacity: 1, height: '*' }),
          animate('150ms', style({ opacity: 1, height: '0' }))
        ])
      ]
    )
  ]
})
export class NavMainComponent implements OnInit {

  constructor(
    private loStorageService: LoStorageService,
    private elementRef: ElementRef,
  ) { }

  fa = fa;

  public account: SessionAccountResponse | null = null;

  public isUserMenuOpen = false;
  public isMobileMenuOpen = false;
  public isMobile = false;

  public activeGames: ActiveGameResponse[] = [];
  openActiveGames: boolean = false;

  now$ = interval(1000).pipe(
    startWith(0),
    map(() => Date.now()),
    shareReplay(1)
  );

  ngOnInit(): void {
    this.getAccount();
    this.isMobile = window.innerWidth <= 768;
  }

  getActiveTime(time: Date | string | null, now: number): string {
    if (!time) return '';

    const diffSeconds = Math.floor((now - new Date(time).getTime()) / 1000);

    const hours = Math.floor(diffSeconds / 3600);
    const minutes = Math.floor((diffSeconds % 3600) / 60);
    const seconds = diffSeconds % 60;

    const mm = minutes.toString().padStart(2, '0');
    const ss = seconds.toString().padStart(2, '0');

    if (hours > 0) {
      return `${hours}:${mm}:${ss}`;
    }

    return `${minutes}:${ss}`;
  }

  public toggleUserMenu() {
    this.isUserMenuOpen = !this.isUserMenuOpen;
  }

  private getAccount() {
    this.loStorageService.sessionAccount$.subscribe(
      (account) => {
        this.account = account;
      }
    );

    this.loStorageService.activeGames$.subscribe({
      next: (games) => {
        this.activeGames = games;
      }
    })
  }

  @HostListener('document:click', ['$event'])
  public onClick(event: MouseEvent) {
    if(this.isUserMenuOpen) {
      const userMenuElement = this.elementRef.nativeElement.querySelector('#usermenu');
      if (userMenuElement && !userMenuElement.contains(event.target)) {
        this.isUserMenuOpen = false;
      }
    }

    if(this.isMobileMenuOpen && this.isMobile) {
      const mobileMenuElement = this.elementRef.nativeElement.querySelector('#mobilemenu');
      if (mobileMenuElement && !mobileMenuElement.contains(event.target)) {
        this.isMobileMenuOpen = false;
      }
    }
  }

  @HostListener('window:resize', ['$event'])
  private onResize(event: Event) {
    const width = window.innerWidth;
    if (width <= 768) {
      this.isMobile = true;
    } else {
      this.isMobile = false;
    }
  }

}
