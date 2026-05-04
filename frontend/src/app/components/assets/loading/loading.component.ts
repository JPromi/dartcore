import { Component, Input } from '@angular/core';
import { LoadingType } from '../../../enums/loadingType';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'asset-loading',
  imports: [
    CommonModule
  ],
  templateUrl: './loading.component.html',
  styleUrl: './loading.component.scss'
})
export class LoadingComponent {
  
  @Input() public type: LoadingType = LoadingType.BOUNCING_DOTS;

  loadingTypes = LoadingType;
}
