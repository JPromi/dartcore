import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { NavMainComponent } from '../../assets/nav-main/nav-main.component';
import { LoStorageService } from '../../../services/local/lo-storage.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-main-router',
  imports: [
    RouterOutlet,
    NavMainComponent,
    CommonModule
  ],
  templateUrl: './main-router.component.html',
  styleUrl: './main-router.component.scss'
})
export class MainRouterComponent implements OnInit {
  constructor(
    private loStorageService: LoStorageService,
    private router: Router
  ) { }

  public isLoggedIn: boolean = false;

  ngOnInit(): void {
    this.checkSession();
  }

  private checkSession() {
    this.loStorageService.sessionAccount$.subscribe(
      (account) => {
        if (account) {
          this.isLoggedIn = true;
          // User is logged in
        } else {
          this.router.navigate(['/barrier/login']);
        }
      }
    );
  }

}
