import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'error-page',
  imports: [
    CommonModule,
    TranslateModule,
    RouterModule
  ],
  templateUrl: './error-page.component.html',
  styleUrl: './error-page.component.scss'
})
export class ErrorPageComponent {
  @Input() public errorCode: number = 404;

  public codes = [ 401, 403, 404, 504 ];

  public translationExsists(code: number): boolean {
    return this.codes.includes(code);
  }
}
