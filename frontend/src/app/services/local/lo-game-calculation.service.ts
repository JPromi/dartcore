import { Injectable } from '@angular/core';
import { GameThrowMultiplierEnum } from '../../enums/gameThrowMultiplierEnum';

@Injectable({
  providedIn: 'root'
})
export class LoGameCalculationService {

  constructor() { }

  public getThrowMultiplierChar(multiplier: GameThrowMultiplierEnum): string {
    switch (multiplier) {
      case GameThrowMultiplierEnum.DOUBLE:
        return "D";
      case GameThrowMultiplierEnum.TRIPLE:
        return "T";
      default:
        return "";
    }
  }
}
