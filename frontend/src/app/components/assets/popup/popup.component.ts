import { animate, style, transition, trigger } from '@angular/animations';
import { CommonModule } from '@angular/common';
import { Component, EventEmitter, HostListener, Input, Output } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'asset-popup',
  imports: [
    CommonModule,
    TranslateModule
  ],
  templateUrl: './popup.component.html',
  styleUrl: './popup.component.scss',
  animations: [
    trigger(
      'fade', [
        transition(':enter', [
          style({ opacity: 0 }),
          animate('100ms', style({ opacity: 1 }))
        ]),
        transition(':leave', [
          style({ opacity: 1 }),
          animate('150ms', style({ opacity: 0 }))
        ])
      ]
    )
  ]
})
export class PopupComponent {

  @Input() title: string = "";
  @Input() description: string = ""
  @Input() type: 'info' | 'delete' | 'leave' = 'info';

  @Output() close: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Input() show: boolean = false;
  @Output() showChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor() { }

  submit(confirmed: boolean) {
    this.showChange.emit(false);
    this.close.emit(confirmed);
  }

  @HostListener('window:keydown.escape')
  onKeydownHandler() {
    this.submit(false);
  }

}
