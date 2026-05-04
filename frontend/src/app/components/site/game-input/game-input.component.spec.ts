import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameInputComponent } from './game-input.component';

describe('GameInputComponent', () => {
  let component: GameInputComponent;
  let fixture: ComponentFixture<GameInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameInputComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GameInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
