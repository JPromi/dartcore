import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InputCheckboxSliderComponent } from './input-checkbox-slider.component';

describe('InputCheckboxSliderComponent', () => {
  let component: InputCheckboxSliderComponent;
  let fixture: ComponentFixture<InputCheckboxSliderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InputCheckboxSliderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InputCheckboxSliderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
