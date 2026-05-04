import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'input-checkbox-slider',
  imports: [],
  templateUrl: './input-checkbox-slider.component.html',
  styleUrl: './input-checkbox-slider.component.scss'
})
export class InputCheckboxSliderComponent {

  @Input() public checked: boolean = false;
  @Input() public disabled: boolean = false;
  @Output() public checkedChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor() { }

  public toggle() {
    if (!this.disabled) {
      this.checked = !this.checked;
      this.checkedChange.emit(this.checked);
    }
  }
}
