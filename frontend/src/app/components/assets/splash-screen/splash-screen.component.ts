import { animate, style, transition, trigger } from '@angular/animations';
import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'asset-splash-screen',
  imports: [
    CommonModule
  ],
  templateUrl: './splash-screen.component.html',
  styleUrl: './splash-screen.component.scss',
  animations: [
    trigger(
      'fade', [
        transition(':enter', [
          style({ opacity: 0 }),
          animate('250ms', style({ opacity: 1 }))
        ]),
        transition(':leave', [
          style({ opacity: 1 }),
          animate('250ms', style({ opacity: 0 }))
        ])
      ]
    )
  ]
})
export class SplashScreenComponent {
  @Input() public initLoad: boolean = true;
  @Input() public isLoading: boolean = true;
}
