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

  ngOnInit(): void {
    this.getAccount();
    this.isMobile = window.innerWidth <= 768;
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
